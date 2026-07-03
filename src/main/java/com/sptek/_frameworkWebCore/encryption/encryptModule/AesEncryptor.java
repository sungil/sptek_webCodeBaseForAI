package com.sptek._frameworkWebCore.encryption.encryptModule;

import com.sptek._frameworkWebCore.encryption.GlobalEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES/CBC/PKCS5Padding 기반의 문자열 암복호화 모듈.
 *
 * <p>설정에서 Base64 인코딩된 대칭키를 받아 사용하고, 암호화 결과에는 매번 생성한 IV와
 * 암호문을 함께 Base64로 인코딩한다. Spring Bean 생성 시 {@link GlobalEncryptor}에
 * {@code sptAES} 타입으로 등록된다.</p>
 */
@Slf4j
@Component
public class AesEncryptor implements StringEncryptor {
    private final String ALGORITHM = "AES";
    private final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec secretKey;

    AesEncryptor(@Value("${aesEncryptor.base64SecretKey}") String base64SecretKey) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
        this.secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

        //Encryption 에 사용 등록 처리
        GlobalEncryptor.register(GlobalEncryptor.Type.sptAES, this);
    }

    /**
     * 평문을 AES로 암호화하고 IV와 암호문을 결합한 Base64 문자열을 반환한다.
     *
     * <p>IV는 호출마다 새로 생성하며 반환값 앞쪽에 포함되므로 별도 IV 저장소가 필요하지 않다.</p>
     */
    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);

            // IV 생성
            byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // IV와 암호문 결합
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
     * IV와 암호문이 결합된 Base64 문자열을 AES로 복호화한다.
     */
    @Override
    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);

            // IV 분리
            byte[] iv = new byte[16];
            System.arraycopy(decoded, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 암호문 분리
            byte[] encrypted = new byte[decoded.length - iv.length];
            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while AES decrypting", e);
        }
    }
}
