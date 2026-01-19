package com.tietoevry.surest_member_management.controller;


import com.tietoevry.surest_member_management.dto.MemberResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class MemberControllerIT {

        @Autowired
        private TestRestTemplate restTemplate;

        private static String jwtTokenAdmin;
        private static String jwtTokenUser;

        private static UUID createdMemberId;

        @BeforeAll
        static void init(@Autowired TestRestTemplate restTemplate) {
            // Login as admin
            Map<String, String> adminLogin = Map.of(
                    "username", "admin",
                    "password", "admin123"
            );

            ResponseEntity<Map> adminResponse = restTemplate.postForEntity(
                    "/api/v1/auth/login",
                    adminLogin,
                    Map.class
            );

            jwtTokenAdmin = "Bearer " + adminResponse.getBody().get("token");

            // Login as user
            Map<String, String> userLogin = Map.of(
                    "username", "user",
                    "password", "user123"
            );

            ResponseEntity<Map> userResponse = restTemplate.postForEntity(
                    "/api/v1/auth/login",
                    userLogin,
                    Map.class
            );

            jwtTokenUser = "Bearer " + userResponse.getBody().get("token");
        }

        @Test
        @Order(1)
        void testCreateMember_asAdmin() {
            Map<String, Object> memberRequest = Map.of(
                    "firstName", "John",
                    "lastName", "Doe",
                    "dateOfBirth", "1990-01-01T00:00:00",
                    "email", "john.doe@example.com"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtTokenAdmin);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(memberRequest, headers);

            ResponseEntity<MemberResponseDto> response = restTemplate.postForEntity(
                    "/api/v1/members",
                    request,
                    MemberResponseDto.class
            );

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getId());

            createdMemberId = response.getBody().getId();
        }

        @Test
        @Order(2)
        void testGetMemberById_asUser() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtTokenUser);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<MemberResponseDto> response = restTemplate.exchange(
                    "/api/v1/members/" + createdMemberId,
                    HttpMethod.GET,
                    request,
                    MemberResponseDto.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("John", response.getBody().getFirstName());
        }

        @Test
        @Order(3)
        void testUpdateMember_asAdmin() {
            Map<String, Object> updateRequest = Map.of(
                    "firstName", "Jane",
                    "lastName", "Doe",
                    "dateOfBirth", "1990-01-01T00:00:00",
                    "email", "jane.doe@example.com"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtTokenAdmin);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<MemberResponseDto> response = restTemplate.exchange(
                    "/api/v1/members/" + createdMemberId,
                    HttpMethod.PUT,
                    request,
                    MemberResponseDto.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Jane", response.getBody().getFirstName());
            assertEquals("jane.doe@example.com", response.getBody().getEmail());
        }

        @Test
        @Order(4)
        void testDeleteMember_asAdmin() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtTokenAdmin);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/members/" + createdMemberId,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @Order(5)
        void testGetAllMembers_asUser() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtTokenUser);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/members",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

