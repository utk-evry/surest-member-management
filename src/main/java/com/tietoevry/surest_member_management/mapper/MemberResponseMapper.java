package com.tietoevry.surest_member_management.mapper;

import com.tietoevry.surest_member_management.dto.MemberResponseDTO;
import com.tietoevry.surest_member_management.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberResponseMapper {

    MemberResponseDTO toDto(Member member);
}
