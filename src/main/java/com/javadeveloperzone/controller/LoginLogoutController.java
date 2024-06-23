package com.javadeveloperzone.controller;

import com.javadeveloperzone.models.JwtAuthenticationModel.AuthenticationRequest;
import com.javadeveloperzone.service.UserService.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("")
public class LoginLogoutController {
     private final UserService userService;

    public LoginLogoutController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authenticate/user")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response,HttpServletRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(authenticationRequest,response,request));
    }

    @PostMapping("/logout/user")
    public ResponseEntity<?> logOut(HttpServletRequest httpServletRequest,HttpServletResponse response) {
        userService.logOut(httpServletRequest,response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User Logged Out");
    }
}
