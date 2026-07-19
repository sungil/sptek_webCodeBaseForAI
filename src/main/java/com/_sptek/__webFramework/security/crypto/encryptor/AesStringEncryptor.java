package com._sptek.__webFramework.security.crypto.encryptor;

import com._sptek.__webFramework.security.crypto.registry.EncryptorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES/GCM/NoPadding 기반의 문자열 암복호화 모듈.
 *
 * <p>설정에서 Base64 인코딩된 AES 키를 받아 사용하고, 암호화 결과에는 매번 생성한 IV와
 * 인증 태그가 포함된 암호문을 함께 Base64로 인코딩한다. GCM은 암호문 변조를 인증 태그로
 * 감지할 수 있으므로 기존 CBC 방식보다 신규 데이터 암호화에 적합하다. Spring Bean 생성 시
 * {@link EncryptorRegistry}에 {@code sptAES} 타입으로 등록된다.</p>
 */
@Slf4j
@Component
public class AesStringEncryptor implements StringEncryptor {
    private final String ALGORITHM = "AES";
    private final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SecretKeySpec secretKey;

    AesStringEncryptor(@Value("${aesEncryptor.base64SecretKey}") String base64SecretKey) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
        validateAesKeyLength(secretKeyBytes);
        this.secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

        //Encryption 에 사용 등록 처리
        EncryptorRegistry.register(EncryptorRegistry.Type.sptAES, this);
    }

    /**
     * 평문을 AES-GCM으로 암호화하고 IV와 인증 태그 포함 암호문을 결합한 Base64 문자열을 반환한다.
     *
     * <p>IV는 호출마다 새로 생성하며 반환값 앞쪽에 포함되므로 별도 IV 저장소가 필요하지 않다.
     * GCM에서는 같은 키와 IV 조합을 반복 사용하면 안 되므로 IV는 반드시 매번 새로 생성한다.</p>
     */
    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);

            // IV 생성
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // IV와 인증 태그 포함 암호문 결합
            byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while AES encrypting", e);
        }
    }

    /**
     * IV와 인증 태그 포함 암호문이 결합된 Base64 문자열을 AES-GCM으로 복호화한다.
     */
    @Override
    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            if (decoded.length <= GCM_IV_LENGTH_BYTES) {
                throw new IllegalArgumentException("AES-GCM encrypted text is too short");
            }

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);

            // IV 분리
            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH_BYTES);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            // 인증 태그 포함 암호문 분리
            byte[] encrypted = Arrays.copyOfRange(decoded, GCM_IV_LENGTH_BYTES, decoded.length);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while AES decrypting", e);
        }
    }

    private static void validateAesKeyLength(byte[] secretKeyBytes) {
        int length = secretKeyBytes.length;
        if (length != 16 && length != 24 && length != 32) {
            throw new IllegalArgumentException("AES key length must be 16, 24, or 32 bytes after Base64 decoding");
        }
    }
}
