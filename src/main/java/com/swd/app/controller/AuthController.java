package com.swd.app.controller;

import com.swd.app.reqDto.LoginDto;
import com.swd.app.reqDto.RegisterDto;
import com.swd.app.resDto.LoginResponseDto;
import com.swd.cms.dto.UserDto;
import com.swd.cms.mapper.UserMapper;
import com.swd.common.BaseController;
import com.swd.entities.Role;
import com.swd.entities.User;
import com.swd.exception.BadRequestException;
import com.swd.model.dto.ApiMessageDto;
import com.swd.security.jwt.JwtUtils;
import com.swd.services.RoleService;
import com.swd.services.UserDetailsImpl;
import com.swd.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "App")
public class AuthController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ApiMessageDto<Object> register(@Valid @RequestBody RegisterDto registerDto) {
        // Check if username is already taken
        if (Boolean.TRUE.equals(userService.existsByUsername(registerDto.getUsername()))) {
            throw new BadRequestException("Username is already taken!");
        }
        if (Boolean.TRUE.equals(userService.existByPhoneNumber(registerDto.getPhoneNumber()))) {
            throw new BadRequestException("Phone number is already taken!");
        }
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(encoder.encode(registerDto.getPassword()));
        user.setPhone(registerDto.getPhoneNumber());
        Role role = roleService.getByName("ROLE_USER");
        user.setRoles(Collections.singleton(role));
        User savedUser = userService.addUser(user);
        return makeResponse(true, userMapper.fromEntityToUserDto(savedUser), "Register successful!");
    }

    @PostMapping("/login")
    public ApiMessageDto<Object> login(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        User user = userService.getById(userDetails.getId());
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setId(user.getId());
        loginResponseDto.setUsername(user.getUsername());
        loginResponseDto.setToken(jwt);
        loginResponseDto.setFullName(user.getFullName());
        loginResponseDto.setRoles(roles);

        return makeResponse(true, loginResponseDto, "Login successful!");
    }


}
