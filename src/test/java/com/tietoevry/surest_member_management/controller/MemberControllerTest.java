package com.tietoevry.surest_member_management.controller;

import com.tietoevry.surest_member_management.dto.MemberCreateDTO;
import com.tietoevry.surest_member_management.dto.MemberResponseDTO;
import com.tietoevry.surest_member_management.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberServiceImpl memberService;

    @InjectMocks
    private MemberController memberController;

    @Test
    void getMemberById_success() {
        UUID id = UUID.randomUUID();

        MemberResponseDTO responseDto = new MemberResponseDTO();
        responseDto.setId(id);
        responseDto.setFirstName("Utkrisht");

        when(memberService.getMemberById(id)).thenReturn(responseDto);

        ResponseEntity<MemberResponseDTO> response =
                memberController.getMemberById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Utkrisht", response.getBody().getFirstName());

        verify(memberService).getMemberById(id);
    }

    @Test
    void getAllMembers_success() {
        MemberResponseDTO member = new MemberResponseDTO();
        member.setId(UUID.randomUUID());

        Page<MemberResponseDTO> page =
                new PageImpl<>(List.of(member));

        when(memberService.getAllMembers(0, 10, "id", "asc"))
                .thenReturn(page);

        Page<MemberResponseDTO> result =
                memberController.getAllMembers(0, 10, "id", "asc");

        assertEquals(1, result.getContent().size());
        verify(memberService).getAllMembers(0, 10, "id", "asc");
    }

    @Test
    void createMember_success() {
        MemberCreateDTO createDto = new MemberCreateDTO();

        createDto.setFirstName("Utkrisht");
        createDto.setLastName("Kumar");
        createDto.setEmail("utkrishtkumar@gmail.com");
        createDto.setDateOfBirth(LocalDate.of(1999,1,16));

        MemberResponseDTO responseDto = new MemberResponseDTO();
        responseDto.setId(UUID.randomUUID());
        responseDto.setFirstName("Utkrisht");

        when(memberService.createMember(createDto))
                .thenReturn(responseDto);

        ResponseEntity<MemberResponseDTO> response =
                memberController.createMember(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Utkrisht", response.getBody().getFirstName());

        verify(memberService).createMember(createDto);
    }

    @Test
    void updateMember_success() {
        UUID id = UUID.randomUUID();

        MemberCreateDTO updateDto = new MemberCreateDTO();
                updateDto.setFirstName("Updated");
                updateDto.setLastName("User");
                updateDto.setEmail("updated@tietoevry.com");
                updateDto.setDateOfBirth(LocalDate.of(1999,1,16));

        MemberResponseDTO responseDto = new MemberResponseDTO();
        responseDto.setId(id);
        responseDto.setFirstName("Updated");

        when(memberService.updateMember(id, updateDto))
                .thenReturn(responseDto);

        ResponseEntity<MemberResponseDTO> response =
                memberController.updateMember(id, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", response.getBody().getFirstName());

        verify(memberService).updateMember(id, updateDto);
    }

    @Test
    void deleteMember_success() {
        UUID id = UUID.randomUUID();

        doNothing().when(memberService).deleteMember(id);

        ResponseEntity<Void> response =
                memberController.deleteMember(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(memberService).deleteMember(id);
    }
}