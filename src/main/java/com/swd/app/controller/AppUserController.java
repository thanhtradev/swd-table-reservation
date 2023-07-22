package com.swd.app.controller;

import com.swd.app.reqDto.UpdateUserDetailResponseDto;
import com.swd.app.resDto.LoginResponseDto;
import com.swd.app.resDto.UserDetailResponseDto;
import com.swd.cms.mapper.UserMapper;
import com.swd.common.BaseController;
import com.swd.entities.User;
import com.swd.model.dto.ApiMessageDto;
import com.swd.services.UserDetailsImpl;
import com.swd.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/user")
@Tag(name = "App")
public class AppUserController extends BaseController {
    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ApiMessageDto<Object> getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userDetails.getId());
        UserDetailResponseDto userDetailResponseDto = new UserDetailResponseDto();
        userDetailResponseDto.setId(user.getId());
        userDetailResponseDto.setUsername(user.getUsername());
        userDetailResponseDto.setPhone(user.getPhone());
        userDetailResponseDto.setFullName(user.getFullName());
        return makeResponse(true, userDetailResponseDto, "Get current user successful!");
    }

    @PostMapping("/update")
    public ApiMessageDto<Object> updateUser(@Valid UpdateUserDetailResponseDto updateUserDetailResponseDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userDetails.getId());

        if (updateUserDetailResponseDto.getFullName() != null) {
            user.setFullName(updateUserDetailResponseDto.getFullName());
        }
        if (updateUserDetailResponseDto.getPhone() != null) {
            user.setPhone(updateUserDetailResponseDto.getPhone());
        }
        userService.addUser(user);
        UserDetailResponseDto userDetailResponseDto = new UserDetailResponseDto();
        userDetailResponseDto.setId(user.getId());
        userDetailResponseDto.setUsername(user.getUsername());
        userDetailResponseDto.setPhone(user.getPhone());
        userDetailResponseDto.setFullName(user.getFullName());
        return makeResponse(true, userDetailResponseDto, "Update user successful!");
    }
}
