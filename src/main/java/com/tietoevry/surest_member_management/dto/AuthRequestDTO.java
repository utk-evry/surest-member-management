package com.tietoevry.surest_member_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {

    @NotBlank(message = "Required field missing")
    private String username;

    @NotBlank(message = "Required field missing")
    private String password;

}
