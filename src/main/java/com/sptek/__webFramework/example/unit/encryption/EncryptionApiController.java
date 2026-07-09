package com.sptek.__webFramework.example.unit.encryption;

import com.sptek.__webFramework.security.crypto.Enable_DecryptAuto_At_DtoString;
import com.sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek.__webFramework.security.crypto.GlobalEncryptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Encryption", description = "")

public class EncryptionApiController {

    @GetMapping("/01/example/encryption/jasyptForProperty")
    @Operation(summary = "01. jasypt로 암호호 된 서버 프로퍼티 값이 자동으로 복호화 되어 바인딩 되는지 확인 ", description = "")
    public Object JasyptForProperty(@Parameter(hidden = true) @Value("${jasypt.decryptTest.encValue}") String encValue) {
        return encValue;
    }

    @PostMapping("/02/example/encryption/allTypeEncryptForString")
    @Operation(summary = "02. plain text 파람에 대해 DES, AES, RSA, Jasypt 로 각각 암/복호화 처리", description = "")
    public Object allTypeEncryptForString(@RequestBody String plainText) {
        //4가지 방식으로 암호화 처리
        HashMap<String, String> encryptedMap = new HashMap<>();
        encryptedMap.put(GlobalEncryptor.Type.sptDES.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptDES, plainText)); // 보안 취약
        encryptedMap.put(GlobalEncryptor.Type.sptAES.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptAES, plainText));
        encryptedMap.put(GlobalEncryptor.Type.sptRSA.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptRSA, plainText)); // RSA는 저장하는 용도로 사용 하지 말것
        encryptedMap.put(GlobalEncryptor.Type.sptJASYPT.name(), GlobalEncryptor.encrypt(GlobalEncryptor.Type.sptJASYPT, plainText));

        //암호화된 모든 값을 다시 복호화
        HashMap<String, String> decryptedMap = new HashMap<>();
        decryptedMap.put(GlobalEncryptor.Type.sptDES.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptDES.name())));
        decryptedMap.put(GlobalEncryptor.Type.sptAES.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptAES.name())));
        decryptedMap.put(GlobalEncryptor.Type.sptRSA.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptRSA.name())));
        decryptedMap.put(GlobalEncryptor.Type.sptJASYPT.name(), GlobalEncryptor.decrypt(encryptedMap.get(GlobalEncryptor.Type.sptJASYPT.name())));

        //결과 처리
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("plainText", plainText);
        resultMap.put("encryptedMap", encryptedMap);
        resultMap.put("decryptedMap", decryptedMap);
        return resultMap;
    }

    @PostMapping("/03/example/encryption/allTypeDecryptForString")
    @Operation(summary = "03. 암호화된 (DES, AES, RSA, Jasypt) 파람 값의 복호화", description = "")
    public Object allTypeDecryptForString(@RequestBody String encryptText) {
        return GlobalEncryptor.decrypt(encryptText);
    }

    @PostMapping("/04/example/encryption/allTypeDecryptForDto")
    @Operation(summary = "04. 암호화된(DES, AES, RSA, Jasypt) 필드를 포함 하는 객체의 복호화", description = "")
    public Object allTypeDecryptForDto(@RequestBody ParentDto parentDto) throws Exception {
        return GlobalEncryptor.decrypt(parentDto);
    }


    // 테스를 위한 임의 DTOs
    //====================================================================================
    @Data
    public static class ParentDto {
        @Enable_DecryptAuto_At_DtoString
        @Schema(example = "ENC_sptJASYPT(Ux9vDuJKHsiyR37oKh2ivn6C4cNOftTu07ZosIJmd4ACCs3qSc8vxNd8hWWuD9PH)")
        private String field1;
        private String field2;
        private ChileDto field3 = new ChileDto();
    }

    @Data
    public static class ChileDto {
        @Enable_DecryptAuto_At_DtoString
        @Schema(example = "ENC_sptDES(/QUmzFi1GuzK5BnAZ1RrNxtcUMMUx+yU)")
        private String field1;

        @Enable_DecryptAuto_At_DtoString
        @Schema(example = "ENC_sptAES(i7SNnO3OyWwC5CbizW5OovemxzXdFhPMbt1uuEG+kRg=)")
        private String field2;

    }
    //====================================================================================
}
