package com._sptek.__webFramework.security.authentication.provider;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FormLoginAuthenticationProviderTest {

    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final FormLoginAuthenticationProvider provider = new FormLoginAuthenticationProvider(userDetailsService, passwordEncoder);

    @Test
    void authenticateReturnsUserDetailsPrincipalWithoutRawPasswordCredentials() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("raw-password", "encoded-password")).thenReturn(true);

        Authentication result = provider.authenticate(
                new UsernamePasswordAuthenticationToken("user@example.com", "raw-password")
        );

        assertThat(result.getPrincipal()).isSameAs(userDetails);
        assertThat(result.getCredentials()).isNull();
        assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void authenticateThrowsBadCredentialsForWrongPasswordWithoutUsernameInMessage() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> provider.authenticate(
                new UsernamePasswordAuthenticationToken("user@example.com", "wrong-password")
        ))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials")
                .hasMessageNotContaining("user@example.com");
    }

    @Test
    void authenticateThrowsBadCredentialsWhenCredentialsAreMissing() {
        assertThatThrownBy(() -> provider.authenticate(
                new UsernamePasswordAuthenticationToken("user@example.com", null)
        ))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");
    }

    @Test
    void supportsUsernamePasswordAuthenticationToken() {
        assertThat(provider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
    }
}
