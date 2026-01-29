package com.tietoevry.surest_member_management.service;


import com.tietoevry.surest_member_management.dto.MemberCreateDTO;
import com.tietoevry.surest_member_management.dto.MemberResponseDTO;
import com.tietoevry.surest_member_management.entity.Member;
import com.tietoevry.surest_member_management.exception.EmailAlreadyExistsException;
import com.tietoevry.surest_member_management.exception.MemberNotFoundException;
import com.tietoevry.surest_member_management.mapper.MemberResponseMapper;
import com.tietoevry.surest_member_management.repository.MemberRepository;
import com.tietoevry.surest_member_management.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberResponseMapper memberResponseMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberResponseDTO memberDto;
    private MemberCreateDTO createDto;

    private final UUID memberId = UUID.randomUUID();

    @BeforeEach
    void setUp() {

        member = new Member();
        member.setId(memberId);
        member.setFirstName("Utkrisht");
        member.setLastName("Kumar");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));
        member.setEmail("utkrisht.kumar@gmail.com");

        memberDto = new MemberResponseDTO();
        memberDto.setId(memberId);
        memberDto.setFirstName("Utkrisht");
        memberDto.setLastName("Kumar");
        memberDto.setDateOfBirth(member.getDateOfBirth());
        memberDto.setEmail(member.getEmail());

        createDto = new MemberCreateDTO();
        createDto.setFirstName("Utkrisht");
        createDto.setLastName("Kumar");
        createDto.setDateOfBirth(member.getDateOfBirth());
        createDto.setEmail(member.getEmail());
    }

    @Test
    void testGetMemberById_found() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberResponseMapper.toDto(member)).thenReturn(memberDto);

        MemberResponseDTO result = memberService.getMemberById(memberId);

        assertNotNull(result);
        assertEquals(memberId, result.getId());
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void testGetMemberById_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(memberId));
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void testCreateMember_success() {
        when(memberRepository.existsByEmail(createDto.getEmail())).thenReturn(false);
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(member);
        when(memberResponseMapper.toDto(member)).thenReturn(memberDto);

        MemberResponseDTO result = memberService.createMember(createDto);

        assertNotNull(result);
        assertEquals(memberDto.getEmail(), result.getEmail());
        verify(memberRepository, times(1)).saveAndFlush(any(Member.class));
    }

    @Test
    void testCreateMember_emailExists() {
        when(memberRepository.existsByEmail(createDto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> memberService.createMember(createDto));
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateMember_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);
        when(memberResponseMapper.toDto(member)).thenReturn(memberDto);

        MemberResponseDTO result = memberService.updateMember(memberId, createDto);

        assertNotNull(result);
        assertEquals(memberDto.getEmail(), result.getEmail());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdateMember_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberId, createDto));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void testDeleteMember_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).delete(member);

        assertDoesNotThrow(() -> memberService.deleteMember(memberId));
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    void testDeleteMember_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(memberId));
        verify(memberRepository, never()).delete(any());
    }

    @Test
    void testGetAllMembers() {
        List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList);
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);
        when(memberResponseMapper.toDto(member)).thenReturn(memberDto);

        Page<MemberResponseDTO> result = memberService.getAllMembers(0, 10, "id", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals(memberDto.getId(), result.getContent().get(0).getId());
        verify(memberRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllMembers_sortDesc() {
        List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList);

        when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);
        when(memberResponseMapper.toDto(member)).thenReturn(memberDto);

        Page<MemberResponseDTO> result =
                memberService.getAllMembers(0, 10, "id", "desc");

        assertEquals(1, result.getTotalElements());
        verify(memberRepository).findAll(any(Pageable.class));
    }

}

