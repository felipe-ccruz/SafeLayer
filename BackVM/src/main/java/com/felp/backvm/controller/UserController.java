package com.felp.backvm.controller;

import com.felp.backvm.dto.ChangePlanRequest;
import com.felp.backvm.dto.LoginRequest;
import com.felp.backvm.dto.RegisterRequest;
import com.felp.backvm.dto.UserDTO;
import com.felp.backvm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@RequestBody @Valid RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO login(@RequestBody @Valid LoginRequest request) {
        return service.login(request);
    }

    @PatchMapping("/{id}/plan")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO changePlan(@PathVariable Long id, @RequestBody @Valid ChangePlanRequest request) {
        return service.changePlan(id, request.getPlan());
    }
}
