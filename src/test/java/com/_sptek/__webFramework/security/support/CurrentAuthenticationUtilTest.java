package com._sptek.__webFramework.security.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentAuthenticationUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isRealLoginReturnsFalseForAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "anonymous",
                        SecurityConstants.ANONYMOUS_USER,
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
                )
        );

        assertThat(CurrentAuthenticationUtil.isRealLogin()).isFalse();
    }

    @Test
    void isRealLoginReturnsTrueForAuthenticatedNonAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        AuthorityUtils.createAuthorityList("ROLE_USER")
                )
        );

        assertThat(CurrentAuthenticationUtil.isRealLogin()).isTrue();
    }
}
