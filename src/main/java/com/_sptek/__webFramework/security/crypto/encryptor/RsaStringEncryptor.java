package com._sptek.__webFramework.security.crypto.encryptor;

import com._sptek.__webFramework.security.crypto.registry.EncryptorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

/**
 * 애플리케이션 기동 중 생성된 RSA 키쌍으로 문자열을 암복호화하는 모듈.
 *
 * <p>키쌍은 서버 재기동 시 새로 생성되므로 RSA 암호문을 장기 저장 용도로 사용하지 않는다.
 * 주로 클라이언트가 서버 공개키로 일회성 값을 암호화해 전달하는 흐름에 사용한다.</p>
 */
@Slf4j
@Component
public class RsaStringEncryptor implements StringEncryptor {
    private final String ALGORITHM = "RSA";
    private static KeyPair keyPair;

    RsaStringEncryptor() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048); // 키 길이: 2048bit
        keyPair = keyGen.generateKeyPair();

        //Encryption 에 사용 등록 처리
        EncryptorRegistry.register(EncryptorRegistry.Type.sptRSA, this);
    }

    /**
     * 현재 애플리케이션 인스턴스에서 생성된 RSA 공개키를 반환한다.
     *
     * <p>공개키는 시스템 지원 API를 통해 클라이언트 암호화용으로 제공된다.</p>
     */
    public static PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * 현재 공개키로 평문을 암호화해 Base64 문자열로 반환한다.
     *
     * <p>서버 재기동 후에는 복호화에 필요한 개인키가 바뀌므로 반환값을 저장 데이터로 사용하지 않는다.</p>
     */
    @Override
    public String encrypt(String plainText) {
        try {
            PublicKey publicKey = getPublicKey();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while RSA encrypting", e);
        }
    }

    /**
     * 현재 개인키로 Base64 RSA 암호문을 복호화한다.
     */
    @Override
    public String decrypt(String base64Encrypted) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64Encrypted));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while RSA decrypting", e);
        }
    }
}
