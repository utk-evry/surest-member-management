package com.tietoevry.surest_member_management.service;

import com.tietoevry.surest_member_management.dto.MemberCreateDto;
import com.tietoevry.surest_member_management.dto.MemberResponseDto;
import com.tietoevry.surest_member_management.entity.Member;
import com.tietoevry.surest_member_management.exception.EmailAlreadyExistsException;
import com.tietoevry.surest_member_management.exception.MemberNotFoundException;
import com.tietoevry.surest_member_management.mapper.MemberResponseMapper;
import com.tietoevry.surest_member_management.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberResponseMapper memberResponseMapper;

    public MemberService(MemberRepository memberRepository, MemberResponseMapper memberResponseMapper) {
        this.memberRepository = memberRepository;
        this.memberResponseMapper = memberResponseMapper;
    }

    @Cacheable(value = "members", key="#id")
    public MemberResponseDto getMemberById(UUID id){
        Optional<Member> memberOp = memberRepository.findById(id);

        if(memberOp.isEmpty()) throw new MemberNotFoundException("No such member with that id is found.");

        return memberResponseMapper.toDto(memberOp.get());

    }

    public Page<MemberResponseDto> getAllMembers(int page, int size,String sortBy, String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Member> memberPage = memberRepository.findAll(pageable);

        return memberPage.map(memberResponseMapper::toDto);
    }

    @Transactional
    public MemberResponseDto createMember(MemberCreateDto requestMember){

        if(memberRepository.existsByEmail(requestMember.getEmail())){
            throw new EmailAlreadyExistsException("Email ID already Exists.");
        }

        Member newMember = new Member();
        newMember.setFirstName(requestMember.getFirstName());
        newMember.setLastName(requestMember.getLastName());
        newMember.setDateOfBirth(requestMember.getDateOfBirth());
        newMember.setEmail(requestMember.getEmail());

        Member saved = memberRepository.saveAndFlush(newMember);

        return memberResponseMapper.toDto(saved);

    }

    @Transactional
    @CachePut(value = "members", key = "#id")
    public MemberResponseDto updateMember(UUID id, MemberCreateDto requestMember){
        Optional<Member> memberOp = memberRepository.findById(id);
        if(memberOp.isEmpty()) throw new MemberNotFoundException("No such member with that id is found.");

        Member toBeupdatedMember = memberOp.get();
        toBeupdatedMember.setFirstName(requestMember.getFirstName());
        toBeupdatedMember.setLastName(requestMember.getLastName());
        toBeupdatedMember.setDateOfBirth(requestMember.getDateOfBirth());
        toBeupdatedMember.setEmail(requestMember.getEmail());

        Member updatedMember = memberRepository.save(toBeupdatedMember);

        return memberResponseMapper.toDto(updatedMember);

    }

    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public void deleteMember(UUID id){
        Optional<Member> memberOp = memberRepository.findById(id);
        if(memberOp.isEmpty()) throw new MemberNotFoundException("No such member with that id is found.");

        memberRepository.delete(memberOp.get());

    }

}
