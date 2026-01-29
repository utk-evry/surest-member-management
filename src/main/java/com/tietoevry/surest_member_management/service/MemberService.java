package com.tietoevry.surest_member_management.service;

import com.tietoevry.surest_member_management.dto.MemberCreateDTO;
import com.tietoevry.surest_member_management.dto.MemberResponseDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface MemberService {

    MemberResponseDTO getMemberById(UUID id);
    Page<MemberResponseDTO> getAllMembers(int page, int size, String sortBy, String sortDir);
    MemberResponseDTO createMember(MemberCreateDTO requestMember);
    MemberResponseDTO updateMember(UUID id, MemberCreateDTO requestMember);
    void deleteMember(UUID id);
}
