package com.swd.app.reqDto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserDetailResponseDto {
    private Long id;
    private String fullName;
    private String phone;
}
