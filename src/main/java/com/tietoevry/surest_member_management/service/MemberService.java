package com.tietoevry.surest_member_management.service;

import com.tietoevry.surest_member_management.dto.MemberCreateDto;
import com.tietoevry.surest_member_management.dto.MemberResponseDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface MemberService {

    MemberResponseDto getMemberById(UUID id);
    Page<MemberResponseDto> getAllMembers(int page, int size, String sortBy, String sortDir);
    MemberResponseDto createMember(MemberCreateDto requestMember);
    MemberResponseDto updateMember(UUID id, MemberCreateDto requestMember);
    void deleteMember(UUID id);
}
