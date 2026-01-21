package com.tietoevry.surest_member_management.integration;



import com.tietoevry.surest_member_management.dto.AuthRequestDto;
import com.tietoevry.surest_member_management.dto.AuthResponseDto;
import com.tietoevry.surest_member_management.dto.MemberCreateDto;
import com.tietoevry.surest_member_management.dto.MemberResponseDto;
import com.tietoevry.surest_member_management.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void cleanDb() {
        memberRepository.deleteAll();
    }

    /* -------------------- AUTH HELPERS -------------------- */

    private String fetchAdminToken() throws Exception {
        return getJwt("admin", "admin123");
    }

    private String fetchUserToken() throws Exception {
        return getJwt("user", "user123");
    }

    private String getJwt(String username, String password) throws Exception {
        AuthRequestDto request = new AuthRequestDto();
            request.setUsername(username);
            request.setPassword(password);
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDto authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthResponseDto.class
        );

        return authResponse.getToken();
    }

    /* -------------------- TESTS -------------------- */

    @Test
    void createMember_asAdmin_success() throws Exception {
        String token = fetchAdminToken();
        MemberCreateDto dto = new MemberCreateDto();
                    dto.setFirstName("Utkrisht");
                    dto.setLastName("Kumar");
                    dto.setEmail("utkrisht.kumar@gmail.com");
                    dto.setDateOfBirth(LocalDate.of(1999,1,16));

        mockMvc.perform(
                        post("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void getMemberById_asUser_success() throws Exception {
        String adminToken = fetchAdminToken();

        UUID memberId = createMemberAndReturnId(adminToken);

        String userToken = fetchUserToken();

        mockMvc.perform(
                        get("/api/v1/members/{id}", memberId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()));
    }

    @Test
    void getAllMembers_withPagination_success() throws Exception {
        String token = fetchAdminToken();

        createMemberAndReturnId(token);


        mockMvc.perform(
                        get("/api/v1/members")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("sortDir", "asc")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void updateMember_asAdmin_success() throws Exception {
        String token = fetchAdminToken();

        UUID memberId = createMemberAndReturnId(token);

        MemberCreateDto updateDto = new  MemberCreateDto();
        updateDto.setFirstName("Adarsh");
        updateDto.setLastName("Kumar");
        updateDto.setEmail("utkrisht.kumar@gmail.com");
        updateDto.setDateOfBirth(LocalDate.of(1999,1,16));

        mockMvc.perform(
                        put("/api/v1/members/{id}", memberId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Adarsh"))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));
    }

    @Test
    void deleteMember_asAdmin_success() throws Exception {
        String token = fetchAdminToken();

        UUID memberId = createMemberAndReturnId(token);

        mockMvc.perform(
                        delete("/api/v1/members/{id}", memberId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMember_asUser_forbidden() throws Exception {
        String adminToken = fetchAdminToken();
        UUID memberId = createMemberAndReturnId(adminToken);

        String userToken = fetchUserToken();

        mockMvc.perform(
                        delete("/api/v1/members/{id}", memberId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidJwtToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.token")
                )
                .andExpect(status().isUnauthorized());
    }

//    /* -------------------- UTIL -------------------- */
//
    private UUID createMemberAndReturnId(String token) throws Exception {
        MemberCreateDto dto = new MemberCreateDto();
                            dto.setFirstName("Utkrisht");
                            dto.setLastName("Kumar");
                            dto.setDateOfBirth(LocalDate.of(1999,1,16));
                            dto.setEmail("utkrisht.kumar@gmail.com");


        MvcResult result = mockMvc.perform(
                        post("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andReturn();

        MemberResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                MemberResponseDto.class
        );

        return response.getId();
    }
}

