package com.swd.app.controller;

import com.swd.app.resDto.LoginResponseDto;
import com.swd.cms.mapper.UserMapper;
import com.swd.common.BaseController;
import com.swd.entities.User;
import com.swd.model.dto.ApiMessageDto;
import com.swd.services.UserDetailsImpl;
import com.swd.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/user")
public class AppUserController extends BaseController {
    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ApiMessageDto<Object> getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userDetails.getId());
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setId(user.getId());
        loginResponseDto.setUsername(user.getUsername());
        loginResponseDto.setFullName(user.getFullName());
        return makeResponse(true, loginResponseDto, "Get current user successful!");
    }
}
