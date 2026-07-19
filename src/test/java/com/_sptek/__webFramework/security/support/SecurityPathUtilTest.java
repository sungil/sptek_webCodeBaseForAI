package com._sptek.__webFramework.security.support;

import com._sptek.__webFramework.core.exception.ServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityPathUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void hasPermissionForUserPathReturnsFalseWhenCurrentUserIsAnonymous() throws Exception {
        assertThat(SecurityPathUtil.hasPermissionForSecuredFilePath(Path.of("user", "null", "sample.txt"))).isFalse();
    }

    @Test
    void hasPermissionForUserPathRejectsMissingUserSegment() {
        assertThatThrownBy(() -> SecurityPathUtil.hasPermissionForSecuredFilePath(Path.of("user")))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Invalid securedFilePath");
    }

    @Test
    void hasPermissionForUnknownPathTypeThrowsServiceException() {
        assertThatThrownBy(() -> SecurityPathUtil.hasPermissionForSecuredFilePath(Path.of("unknown", "sample.txt")))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Unsupported securedFilePath");
    }
}
