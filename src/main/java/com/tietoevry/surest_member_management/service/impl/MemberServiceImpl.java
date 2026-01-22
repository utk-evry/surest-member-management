package com.tietoevry.surest_member_management.service.impl;

import com.tietoevry.surest_member_management.dto.MemberCreateDto;
import com.tietoevry.surest_member_management.dto.MemberResponseDto;
import com.tietoevry.surest_member_management.entity.Member;
import com.tietoevry.surest_member_management.exception.EmailAlreadyExistsException;
import com.tietoevry.surest_member_management.exception.MemberNotFoundException;
import com.tietoevry.surest_member_management.mapper.MemberResponseMapper;
import com.tietoevry.surest_member_management.repository.MemberRepository;
import com.tietoevry.surest_member_management.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberResponseMapper memberResponseMapper;

    public MemberServiceImpl(MemberRepository memberRepository, MemberResponseMapper memberResponseMapper) {
        this.memberRepository = memberRepository;
        this.memberResponseMapper = memberResponseMapper;
    }

    @Cacheable(value = "members", key = "#id")
    public MemberResponseDto getMemberById(UUID id) {
        log.debug("Fetching member by id={}", id);

        Optional<Member> memberOp = memberRepository.findById(id);

        if (memberOp.isEmpty()) {
            log.warn("Member with id={} not found", id);
            throw new MemberNotFoundException("No such member with that id is found.");
        }

        log.debug("Member with id={} fetched successfully", id);
        return memberResponseMapper.toDto(memberOp.get());

    }

    public Page<MemberResponseDto> getAllMembers(int page, int size, String sortBy, String sortDir) {
        log.debug("Fetching all members page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Member> memberPage = memberRepository.findAll(pageable);

        log.debug("Fetched {} members from repository", memberPage.getTotalElements());
        return memberPage.map(memberResponseMapper::toDto);
    }

    @Transactional
    public MemberResponseDto createMember(MemberCreateDto requestMember) {

        log.debug("Creating member with email={}", requestMember.getEmail());
        if (memberRepository.existsByEmail(requestMember.getEmail())) {
            log.warn("Attempt to create member failed: email={} already exists", requestMember.getEmail());
            throw new EmailAlreadyExistsException("Email ID already Exists.");
        }

        Member newMember = new Member();
        newMember.setFirstName(requestMember.getFirstName());
        newMember.setLastName(requestMember.getLastName());
        newMember.setDateOfBirth(requestMember.getDateOfBirth());
        newMember.setEmail(requestMember.getEmail());

        Member saved = memberRepository.saveAndFlush(newMember);

        log.info("Member created successfully with id={}", saved.getId());
        return memberResponseMapper.toDto(saved);

    }

    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public MemberResponseDto updateMember(UUID id, MemberCreateDto requestMember) {

        log.debug("Updating member with id={}", id);
        Optional<Member> memberOp = memberRepository.findById(id);
        if (memberOp.isEmpty()) throw new MemberNotFoundException("No such member with that id is found.");

        Member toBeupdatedMember = memberOp.get();
        toBeupdatedMember.setFirstName(requestMember.getFirstName());
        toBeupdatedMember.setLastName(requestMember.getLastName());
        toBeupdatedMember.setDateOfBirth(requestMember.getDateOfBirth());
        toBeupdatedMember.setEmail(requestMember.getEmail());

        Member updatedMember = memberRepository.save(toBeupdatedMember);

        log.info("Member with id={} updated successfully", updatedMember.getId());
        return memberResponseMapper.toDto(updatedMember);

    }

    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public void deleteMember(UUID id) {

        log.debug("Deleting member with id={}", id);

        Optional<Member> memberOp = memberRepository.findById(id);
        if (memberOp.isEmpty()) {
            log.warn("Attempt to delete failed: member id={} not found", id);
            throw new MemberNotFoundException("No such member with that id is found.");
        }
        memberRepository.delete(memberOp.get());

    }

}
