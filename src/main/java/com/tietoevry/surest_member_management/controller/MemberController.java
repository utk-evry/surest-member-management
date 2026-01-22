package com.tietoevry.surest_member_management.controller;

import com.tietoevry.surest_member_management.dto.MemberCreateDto;
import com.tietoevry.surest_member_management.dto.MemberResponseDto;
import com.tietoevry.surest_member_management.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/members",produces = {MediaType.APPLICATION_JSON_VALUE})
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable UUID id){

        log.info("Hit Endpoint: Get /api/v1/members/{}",id);

        MemberResponseDto member = memberService.getMemberById(id);

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @GetMapping
    public Page<MemberResponseDto> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        log.info(
                "Hit Endpoint: GET /api/v1/members page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir
        );
        return memberService.getAllMembers(page,size,sortBy,sortDir);
    }

    @PostMapping
    public  ResponseEntity<MemberResponseDto> createMember(@Valid @RequestBody MemberCreateDto requestMember){
        log.info("Hit Endpoint: POST /api/v1/members");
        return new ResponseEntity<>(memberService.createMember(requestMember),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable UUID id, @Valid @RequestBody MemberCreateDto requestMember){
        log.info("Hit Endpoint: PUT /api/v1/members/{}", id);
        return new ResponseEntity<>(memberService.updateMember(id,requestMember),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id){
        log.info("Hit Endpoint: DELETE /api/v1/members/{}", id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
