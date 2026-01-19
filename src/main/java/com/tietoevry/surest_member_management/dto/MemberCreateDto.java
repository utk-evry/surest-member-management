package com.tietoevry.surest_member_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberCreateDto {

    @NotBlank(message = "Required field -- firstName")
    private String firstName;

    @NotBlank(message = "Required field -- lastName")
    private String lastName;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Required field -- email")
    private String email;
}
