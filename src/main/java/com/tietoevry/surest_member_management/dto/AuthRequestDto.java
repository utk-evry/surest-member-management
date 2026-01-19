package com.tietoevry.surest_member_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    @NotBlank(message = "Required field -- username")
    private String username;

    @NotBlank(message = "Required field -- password")
    private String password;

}
