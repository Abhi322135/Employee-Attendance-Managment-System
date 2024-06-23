package com.javadeveloperzone.service.UserService;

import com.javadeveloperzone.models.JwtAuthenticationModel.AuthenticationRequest;
import com.javadeveloperzone.models.JwtAuthenticationModel.AuthenticationResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request);

    void logOut(HttpServletRequest httpServletRequest, HttpServletResponse response);
}
