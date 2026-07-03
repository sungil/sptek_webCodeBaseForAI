package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.code.CommonErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._projectCommon.commonObject.code.SecureFilePathTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.AntPathMatcher;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
/**
 * HTML escape, 요청 패턴 분류, 보안 파일 경로 생성/검증/권한 확인을 제공하는 보안 보조 유틸리티.
 */
public class SecurityUtil {

    /**
     * 전달된 문자열을 HTML 엔티티로 escape 한다.
     */
    public static String charEscape(String orgStr) {
        return orgStr == null ? orgStr : StringEscapeUtils.escapeHtml4(orgStr);
    }

    /**
     * 세션/로그 등 필수 처리에서 제외할 수 있는 프레임워크 보조 요청 패턴을 반환한다.
     */
    public static List<String> getNotEssentialRequestPatterns(){
        //return Arrays.asList("foo", "bar");
        return Arrays.asList(
                "/swagger-ui.html"
                , "/api-docs/**"
                , "/v2/api-docs/**"
                , "/v3/api-docs/**"
                , "/configuration/ui/**"
                , "/configuration/security/**"
                , "/swagger-resources/**"
                , "/swagger-ui/**"
                , "/swagger/**"
                , "/actuator/**"
                , "/webjars/**"
                , "/error/**"
                , "/err/**"
                , "/static/**"
                , "/systemSupportApi/notEssential/**"
                , "/github-markdown-css/**"
                , "/h2-console/**"
                , "/static/favicon.ico"
        );
    }

    /**
     * 필수 처리 제외 요청 패턴을 Spring Security matcher 등에 전달하기 쉬운 배열로 반환한다.
     */
    public static String[] getNotEssentialRequestPatternsArray() {
        List<String> patterns = getNotEssentialRequestPatterns();
        String[] patternsArray = patterns.toArray(new String[0]);
        return patternsArray;
    }

    /**
     * 현재 request path가 필수 처리 제외 패턴에 매칭되는지 확인한다.
     */
    public static boolean isNotEssentialRequest(){
        return isNotEssentialRequest(SpringUtil.getRequest());
    }

    /**
     * 전달된 request path가 필수 처리 제외 패턴에 매칭되는지 확인한다.
     */
    public static boolean isNotEssentialRequest(@NotNull HttpServletRequest request) {
        return matchesRequestPatterns(request, getNotEssentialRequestPatterns());
    }

    /**
     * 정적 리소스로 취급할 확장자 기반 요청 패턴을 반환한다.
     */
    public static List<String> getStaticResourceRequestPatterns(){
        //return Arrays.asList("foo", "bar");
        return Arrays.asList(
                "/**/*.html", "/**/*.htm", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif"
                , "/**/*.svg", "/**/*.webp", "/**/*.ico", "/**/*.mp4", "/**/*.webm", "/**/*.ogg", "/**/*.mp3", "/**/*.wav"
                , "/**/*.woff", "/**/*.woff2", "/**/*.ttf", "/**/*.otf", "/**/*.eot", "/**/*.pdf", "/**/*.xml", "/**/*.json"
                , "/**/*.csv", "/**/*.txt"
        );
    }

    /**
     * 정적 리소스 요청 패턴을 Spring Security matcher 등에 전달하기 쉬운 배열로 반환한다.
     */
    public static String[] getStaticResourceRequestPatternArray() {
        List<String> patterns = getStaticResourceRequestPatterns();
        String[] patternsArray = patterns.toArray(new String[0]);
        return patternsArray;
    }

    /**
     * 현재 request path가 정적 리소스 요청 패턴에 매칭되는지 확인한다.
     */
    public static boolean isStaticResourceRequest(){
        return isStaticResourceRequest(SpringUtil.getRequest());
    }

    /**
     * 전달된 request path가 정적 리소스 요청 패턴에 매칭되는지 확인한다.
     */
    public static boolean isStaticResourceRequest(@NotNull HttpServletRequest request){
        return matchesRequestPatterns(request, getStaticResourceRequestPatterns());
    }

    private static boolean matchesRequestPatterns(@NotNull HttpServletRequest request, List<String> requestPatterns) {
        String requestPath = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatterns) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }


    /**
     * 누구나 접근 가능한 저장소 보안 경로 prefix를 반환한다.
     */
    public static Path getSecuredFilePathForAnyone() {
        return Path.of(SecureFilePathTypeEnum.ANYONE.getPathName());
    }

    /**
     * 로그인 사용자 접근 저장소 보안 경로 prefix를 반환한다.
     */
    public static Path getSecuredFilePathForLogin() {
        return Path.of(SecureFilePathTypeEnum.LOGIN.getPathName());
    }

    /**
     * 현재 로그인 사용자 전용 저장소 보안 경로를 반환한다.
     */
    public static Path getSecuredFilePathForUser() throws Exception {
        return Path.of(SecureFilePathTypeEnum.USER.getPathName(), AuthenticationUtil.getMyId().toString());
    }

    /**
     * 역할 이름 집합을 정렬해 역할 기반 저장소 보안 경로를 반환한다.
     */
    public static Path getSecuredFilePathForRole(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) throw new IllegalArgumentException("roleNames is required");
        Set<String> sortedSet = new TreeSet<>(roleNames);
        return Path.of(SecureFilePathTypeEnum.ROLE.getPathName(), String.join("-", sortedSet));
    }

    /**
     * 권한 enum 집합을 정렬해 권한 기반 저장소 보안 경로를 반환한다.
     */
    public static Path getSecuredFilePathForAuth(Set<AuthorityEnum> authorities) {
        if (authorities == null || authorities.isEmpty()) throw new IllegalArgumentException("Authorities is required");
        Set<String> sortedSet = authorities.stream().map(AuthorityEnum::name).collect(Collectors.toCollection(TreeSet::new));
        return Path.of(SecureFilePathTypeEnum.AUTH.getPathName(), String.join("-", sortedSet));
    }

    /**
     * 보안 경로 첫 segment에 대응하는 실제 저장소 루트 경로를 application property에서 조회한다.
     */
    public static Path getStorageRootPath(Path securedFilePath) {
        if (securedFilePath == null) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        try {
            return Path.of(SpringUtil.getApplicationProperty(String.format("storage.%s.localRootPath", securedFilePath.getName(0).toString())).toString());
        } catch (Exception e) {
            throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "Unsupported SecurePathEnum value: " + securedFilePath.getName(0).toString());
        }
    }

    /**
     * 문자열 보안 경로를 Path로 변환하고 traversal/절대경로 등 금지 형식을 검증한다.
     */
    public static Path parseSecuredFilePath(String securedFilePath) {
        if (securedFilePath == null || securedFilePath.isBlank()) {
            throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        }

        Path parsedSecuredFilePath = Path.of(securedFilePath.trim());
        validateSecuredFilePath(parsedSecuredFilePath);
        return parsedSecuredFilePath;
    }

    /**
     * 검증된 보안 경로를 저장소 루트 하위의 실제 파일 시스템 경로로 해석한다.
     */
    public static Path resolveStoragePath(Path securedFilePath) {
        validateSecuredFilePath(securedFilePath);

        Path storageRootPath = getStorageRootPath(securedFilePath).toAbsolutePath().normalize();
        Path normalizedSecuredFilePath = securedFilePath.normalize();
        Path resolvedPath = storageRootPath.resolve(normalizedSecuredFilePath).normalize();

        if (!resolvedPath.startsWith(storageRootPath)) {
            throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "Invalid securedFilePath: " + securedFilePath);
        }

        return resolvedPath;
    }

    /**
     * 보안 경로가 상대 경로이며 "." 또는 ".." segment를 포함하지 않는지 검증한다.
     */
    private static void validateSecuredFilePath(Path securedFilePath) {
        if (securedFilePath == null) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        if (securedFilePath.toString().isBlank()) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        if (securedFilePath.isAbsolute()) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "Absolute securedFilePath is not allowed");
        if (securedFilePath.getNameCount() == 0) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        if (securedFilePath.normalize().getNameCount() == 0) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "Invalid securedFilePath: " + securedFilePath);

        for (Path pathPart : securedFilePath) {
            if (".".equals(pathPart.toString()) || "..".equals(pathPart.toString())) {
                throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "Path traversal is not allowed: " + securedFilePath);
            }
        }
    }

    /**
     * 현재 사용자가 지정 보안 경로에 접근할 수 있는지 경로 prefix와 인증 정보를 기준으로 확인한다.
     */
    public static boolean hasPermissionForSecuredFilePath(Path securedFilePath) throws Exception {
        if (securedFilePath == null) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");

        try {
            String SecurePathType = securedFilePath.getName(0).toString();

            if (SecurePathType.equals(SecureFilePathTypeEnum.ANYONE.getPathName())) {
                return true;

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.LOGIN.getPathName())) {
                return AuthenticationUtil.isRealLogin();

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.USER.getPathName())) {
                return securedFilePath.getName(1).toString().equals(String.valueOf(AuthenticationUtil.getMyId()));

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.ROLE.getPathName())) {
                Set<String> rolesInSecuredFilePath = Arrays.stream(securedFilePath.getName(1).toString().split("-")).collect(Collectors.toSet());
                return !Collections.disjoint(rolesInSecuredFilePath, AuthenticationUtil.getMyRoles());

            } else if (SecurePathType.equals(SecureFilePathTypeEnum.AUTH.getPathName())) {
                Set<String> authsInSecuredFilePath = Arrays.stream(securedFilePath.getName(1).toString().split("-")).collect(Collectors.toSet());
                return !Collections.disjoint(authsInSecuredFilePath, AuthenticationUtil.getMyAuths());

            } else {
                throw new Exception("Unsupported securedFilePath: " +  securedFilePath);
            }
        } catch (Exception ex) {
            throw new Exception("Unsupported securedFilePath: " +  securedFilePath);
        }
    }
}
