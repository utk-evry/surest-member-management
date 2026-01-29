package com.tietoevry.surest_member_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


//    @Test
//    void shouldSkipJwtValidationForAuthEndpoints() throws Exception {
//        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");
//
//        filter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain).doFilter(request, response);
//        verifyNoInteractions(jwtUtil, userDetailsService);
//    }


    @Test
    void shouldContinueFilterWhenAuthorizationHeaderMissing() throws Exception {
//        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);
    }


    @Test
    void shouldContinueFilterWhenAuthorizationHeaderInvalid() throws Exception {
//        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abc");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);
    }


    @Test
    void shouldAuthenticateWhenJwtTokenIsValid() throws Exception {
        String token = "valid-token";
        String username = "testuser";

        UserDetails userDetails = new User(username, "password", Collections.emptyList());

//        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
        assert authentication.getPrincipal().equals(userDetails);

        verify(filterChain).doFilter(request, response);
    }


    @Test
    void shouldClearSecurityContextWhenJwtTokenIsInvalid() throws Exception {
        String token = "invalid-token";

//        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(jwtUtil.validateToken(token)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;

        verify(filterChain).doFilter(request, response);
    }
}
