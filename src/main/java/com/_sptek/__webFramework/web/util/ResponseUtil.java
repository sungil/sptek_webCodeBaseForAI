package com._sptek.__webFramework.web.util;

import com._sptek.__webFramework.core.resultCode.CommonErrorCodeEnum;
import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek.__webFramework.security.util.SecurityUtil;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
/**
 * HTTP 응답 헤더/본문 조회와 보안 파일 다운로드 응답 생성을 지원하는 유틸리티.
 */
public class ResponseUtil {

    /**
     * 응답 헤더를 delimiter 없이 Map으로 복사한다.
     */
    public static HashMap<String, String> getResponseHeaderMap(HttpServletResponse response) {
        return getResponseHeaderMap(response, "");
    }

    /**
     * 응답 헤더들을 Map으로 복사하고 각 헤더 값 뒤에 지정 delimiter를 붙인다.
     */
    public static HashMap<String, String> getResponseHeaderMap(HttpServletResponse response, String delimiter) {
        StringBuilder headerString = new StringBuilder();
        HashMap<String, String> headers = new HashMap<>();

        // 요청 헤더 이름을 가져오기
        Set<String> headerNames = TypeConvertUtil.collectionToSet(response.getHeaderNames());

        // 모든 헤더를 순회하며 로그로 남기기
        for (String headerName : headerNames) {
            Enumeration<String> headerValues = TypeConvertUtil.collectionToEnumeration(response.getHeaders(headerName));

            // 헤더 값을 리스트 형태로 변환하여 출력
            StringBuilder values = new StringBuilder();
            while (headerValues.hasMoreElements()) {
                values.append(headerValues.nextElement()).append(", ");
            }

            // 마지막 쉼표와 공백 제거
            if (values.length() > 0) {
                values.setLength(values.length() - 2);  // 마지막 쉼표와 공백 제거
            }

            // 최종 문자열에 추가
            //headerString.append(headerName).append(" = ").append(values.toString()).append("\n");
            headers.put(headerName, values.append(delimiter).toString());
        }
        return headers;
    }

    /**
     * 보안 경로 권한을 확인한 뒤 실제 저장소 파일을 byte[] ResponseEntity로 만든다.
     */
    public static ResponseEntity<byte[]> makeResponseEntityFromFile(Path securedFilePath) throws Exception {
        if (securedFilePath == null) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        if (!SecurityUtil.hasPermissionForSecuredFilePath(securedFilePath)) {
            throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR);
        }

        Path resolvedPath = SecurityUtil.resolveStoragePath(securedFilePath);
        File finalFile = resolvedPath.toFile();
        //log.debug("Final request file: {}", finalFile.getAbsolutePath());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", Files.probeContentType(resolvedPath));

        byte[] fileBytes = FileCopyUtils.copyToByteArray(finalFile);
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    /**
     * ContentCachingResponseWrapper에 저장된 응답 본문을 로그용 문자열로 반환한다.
     */
    public static String getResponseBody(ContentCachingResponseWrapper contentCachingResponseWrapper) {
        byte[] content = contentCachingResponseWrapper.getContentAsByteArray();
        if (content.length == 0) return "";
        if (content.length > 30_000) return "N/A (The body is too big and skipped it.)"; // 그냥 적당이 잡은 수치임(필요시변경)
        try {
            return "\n" + new String(content, contentCachingResponseWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "N/A (Unsupported Encoding)";
        }
    }
}
