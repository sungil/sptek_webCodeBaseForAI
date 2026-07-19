package com._sptek.__webFramework.security.crypto.encryptor;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AesStringEncryptorTest {

    private static final String AES_256_BASE64_KEY = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

    private final AesStringEncryptor encryptor = new AesStringEncryptor(AES_256_BASE64_KEY);

    @Test
    void decryptRestoresPlainTextEncryptedWithGcm() {
        String encrypted = encryptor.encrypt("plain-text");

        assertThat(encryptor.decrypt(encrypted)).isEqualTo("plain-text");
    }

    @Test
    void encryptUsesDifferentIvForSamePlainText() {
        String first = encryptor.encrypt("plain-text");
        String second = encryptor.encrypt("plain-text");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void decryptRejectsTamperedCipherText() {
        String encrypted = encryptor.encrypt("plain-text");
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        decoded[decoded.length - 1] = (byte) (decoded[decoded.length - 1] ^ 1);
        String tampered = Base64.getEncoder().encodeToString(decoded);

        assertThatThrownBy(() -> encryptor.decrypt(tampered))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error while AES decrypting");
    }
}
