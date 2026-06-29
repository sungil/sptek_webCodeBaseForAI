package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.code.CommonErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._projectCommon.commonObject.code.SecureFilePathTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.util.AntPathMatcher;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtil {

    /*주어진 html을 html 엔티티 코드로 변경해 준다.
    For example:
            "bread" &amp; "butter"
    becomes:
            &amp;quot;bread&amp;quot; &amp;amp; &amp;quot
    */
    public static String charEscape(String orgStr) {
        return orgStr == null ? orgStr : StringEscapeUtils.escapeHtml4(orgStr);
    }

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
    public static String[] getNotEssentialRequestPatternsArray() {
        List<String> patterns = getNotEssentialRequestPatterns();
        String[] patternsArray = patterns.toArray(new String[0]);
        return patternsArray;
    }

    public static boolean isNotEssentialRequest(){
        List<String> requestPatterns = getNotEssentialRequestPatterns();
        String requestPath = SpringUtil.getRequest().getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatterns) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }

    public static List<String> getStaticResourceRequestPatterns(){
        //return Arrays.asList("foo", "bar");
        return Arrays.asList(
                "/**/*.html", "/**/*.htm", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif"
                , "/**/*.svg", "/**/*.webp", "/**/*.ico", "/**/*.mp4", "/**/*.webm", "/**/*.ogg", "/**/*.mp3", "/**/*.wav"
                , "/**/*.woff", "/**/*.woff2", "/**/*.ttf", "/**/*.otf", "/**/*.eot", "/**/*.pdf", "/**/*.xml", "/**/*.json"
                , "/**/*.csv", "/**/*.txt"
        );
    }

    public static String[] getStaticResourceRequestPatternArray() {
        List<String> patterns = getStaticResourceRequestPatterns();
        String[] patternsArray = patterns.toArray(new String[0]);
        return patternsArray;
    }

    public static boolean isStaticResourceRequest(){
        List<String> requestPatterns = getStaticResourceRequestPatterns();
        String requestPath = SpringUtil.getRequest().getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String requestPattern : requestPatterns) {
            if(pathMatcher.match(requestPattern, requestPath))
                return true;
        }
        return false;
    }


    public static Path getSecuredFilePathForAnyone() {
        return Path.of(SecureFilePathTypeEnum.ANYONE.getPathName());
    }

    public static Path getSecuredFilePathForLogin() {
        return Path.of(SecureFilePathTypeEnum.LOGIN.getPathName());
    }

    public static Path getSecuredFilePathForUser() throws Exception {
        return Path.of(SecureFilePathTypeEnum.USER.getPathName(), AuthenticationUtil.getMyId().toString());
    }

    public static Path getSecuredFilePathForRole(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) throw new IllegalArgumentException("roleNames is required");
        Set<String> sortedSet = new TreeSet<>(roleNames);
        return Path.of(SecureFilePathTypeEnum.ROLE.getPathName(), String.join("-", sortedSet));
    }

    public static Path getSecuredFilePathForAuth(Set<AuthorityEnum> authorities) {
        if (authorities == null || authorities.isEmpty()) throw new IllegalArgumentException("Authorities is required");
        Set<String> sortedSet = authorities.stream().map(AuthorityEnum::name).collect(Collectors.toCollection(TreeSet::new));
        return Path.of(SecureFilePathTypeEnum.AUTH.getPathName(), String.join("-", sortedSet));
    }

    public static Path getStorageRootPath(Path securedFilePath) {
        if (securedFilePath == null) throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        try {
            return Path.of(SpringUtil.getApplicationProperty(String.format("storage.%s.localRootPath", securedFilePath.getName(0).toString())).toString());
        } catch (Exception e) {
            throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "Unsupported SecurePathEnum value: " + securedFilePath.getName(0).toString());
        }
    }

    public static Path parseSecuredFilePath(String securedFilePath) {
        if (securedFilePath == null || securedFilePath.isBlank()) {
            throw new ServiceException(CommonErrorCodeEnum.BAD_REQUEST_ERROR, "securedFilePath is required");
        }

        Path parsedSecuredFilePath = Path.of(securedFilePath.trim());
        validateSecuredFilePath(parsedSecuredFilePath);
        return parsedSecuredFilePath;
    }

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

    //현재 사용자 가 해당 securedFilePath 에 Access 권한이 있는지 확인 함
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
