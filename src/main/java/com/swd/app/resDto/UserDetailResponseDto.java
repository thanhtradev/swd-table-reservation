package com.swd.app.resDto;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailResponseDto {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private List<String> roles;
}
