package com.tietoevry.surest_member_management.controller;

import com.tietoevry.surest_member_management.dto.AuthRequestDTO;
import com.tietoevry.surest_member_management.dto.AuthResponseDTO;
import com.tietoevry.surest_member_management.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_withValidCredentials_returnsJwtToken() {
        // given
        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("user");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("user"))
                .thenReturn("mocked-jwt-token");

        // when
        ResponseEntity<AuthResponseDTO> response =
                authController.login(request);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mocked-jwt-token", response.getBody().getToken());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("user");
    }

    @Test
    void login_withInvalidCredentials_throwsException() {
        // given
        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("user");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        // when / then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        assertEquals("Bad credentials", ex.getMessage());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtil);
    }
}
