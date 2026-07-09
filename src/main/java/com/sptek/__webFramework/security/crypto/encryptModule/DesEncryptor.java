package com.sptek.__webFramework.security.crypto.encryptModule;

import com.sptek.__webFramework.security.crypto.GlobalEncryptor;
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
 * DES/CBC/PKCS5Padding 기반의 문자열 암복호화 모듈.
 *
 * <p>DES는 보안성이 낮으므로 호환성처럼 꼭 필요한 경우에만 사용한다. 새 데이터 암호화에는
 * AES 또는 요구사항에 맞는 다른 모듈을 우선 검토한다. Spring Bean 생성 시
 * {@link GlobalEncryptor}에 {@code sptDES} 타입으로 등록된다.</p>
 */
@Slf4j
@Component
public class DesEncryptor implements StringEncryptor {
    private final String ALGORITHM = "DES";
    private final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    private final SecretKeySpec secretKey;

    DesEncryptor(@Value("${desEncryptor.base64SecretKey}") String base64SecretKey) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
        if (secretKeyBytes.length != 8) {
            // DES 키는 반드시 8바이트 (64비트)여야 함
            throw new IllegalArgumentException("Error while DES encrypting, DES key length must be an 11-character Base64-encoded (64 bits)");
        }
        this.secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

        //Encryption 에 사용 등록 처리
        GlobalEncryptor.register(GlobalEncryptor.Type.sptDES, this);
    }

    /**
     * 평문을 DES로 암호화하고 IV와 암호문을 결합한 Base64 문자열을 반환한다.
     */
    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 8바이트 IV 생성
            byte[] iv = new byte[8];
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
            throw new RuntimeException("Error while DES encrypting", e);
        }
    }

    /**
     * IV와 암호문이 결합된 Base64 문자열을 DES로 복호화한다.
     */
    @Override
    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 8바이트 IV 분리
            byte[] iv = new byte[8];
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
            throw new RuntimeException("Error while DES decrypting", e);
        }
    }
}
