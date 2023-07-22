package com.swd.app.reqDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String phoneNumber;
}
