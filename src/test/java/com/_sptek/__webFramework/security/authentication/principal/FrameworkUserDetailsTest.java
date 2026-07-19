package com._sptek.__webFramework.security.authentication.principal;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

class FrameworkUserDetailsTest {

    @Test
    void getAuthoritiesReturnsEmptyCollectionWhenAuthorityNamesAreNull() {
        FrameworkUserDetails userDetails = FrameworkUserDetails.builder()
                .authorityNames(null)
                .build();

        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    void getAuthoritiesIgnoresNullAndBlankAuthorityNames() {
        FrameworkUserDetails userDetails = FrameworkUserDetails.builder()
                .authorityNames(new LinkedHashSet<>(Arrays.asList("ROLE_USER", null, "", "AUTH_TEST")))
                .build();

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER", "AUTH_TEST");
    }
}
