package com.sptek.__webFramework.core.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
/**
 * 파일명 추출과 디렉터리 생성 같은 파일 시스템 보조 기능을 제공한다.
 */
public class FileUtil {

    /**
     * 경로 문자열에서 확장자를 포함한 파일명만 추출한다.
     */
    public static String extractFileNameOnly(String fileNameWithPath) {
        Path path = Paths.get(fileNameWithPath);
        Path fileNameOnly = path.getFileName();
        return fileNameOnly.toString();
    }

    /**
     * 전달된 디렉터리 경로를 생성하며, 이미 존재하는 경로는 그대로 통과한다.
     */
    public static void createDirectories(Path directories) throws IOException {
        if (directories != null) {
            //FileAttribute<?> fileAttrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x"));
            //Files.createDirectories(parentDir, fileAttrs);
            Files.createDirectories(directories);
        }
        log.debug("dir path : " + directories);
    }

}
