package com.javadeveloperzone.service.UserService;

import com.javadeveloperzone.component.JwtComponent.JWTTokenUtil;
import com.javadeveloperzone.models.JwtAuthenticationModel.AuthenticationRequest;
import com.javadeveloperzone.models.JwtAuthenticationModel.AuthenticationResponse;
import com.javadeveloperzone.service.AuthenticationService.UserAuthenticationService;
import com.javadeveloperzone.service.AuthenticationService.UserExtend;
import com.javadeveloperzone.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private final JWTTokenUtil jwtTokenUtil;
    private final UserAuthenticationService userAuthentication;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(JWTTokenUtil jwtTokenUtil, UserAuthenticationService userAuthentication, AuthenticationManager authenticationManager) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userAuthentication = userAuthentication;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) {
        try {
            authenticate(authenticationRequest.getEmail(),authenticationRequest.getPassword());
        } catch (Exception e) {
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Authentication failed because of wrong username or password");
        }
        final UserDetails userDetails= userAuthentication.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt= jwtTokenUtil.generateToken((UserExtend) userDetails);
        if (authenticationRequest.getRememberMe()){
            Cookie cookie = new Cookie("JWT-TOKEN", jwt);
            cookie.setMaxAge(30 * 24 * 60 * 60);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
        return (new AuthenticationResponse(jwt));
    }

    @Override
    public void logOut(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        String token= null;
        try {
            token = httpServletRequest.getHeader("Authorization").substring(7);
            System.out.println(token);
        } catch (Exception ignored) {
        }

            Optional<Cookie> cookie= Arrays.stream(httpServletRequest.getCookies()).filter((c -> c.getName().equals("JWT-TOKEN")))
                    .findFirst();
        if (cookie.isPresent()){
            Cookie cookie1=cookie.get();
            cookie1.setMaxAge(0);
            cookie1.setHttpOnly(true);
            cookie1.setPath("/");
            response.addCookie(cookie1);
        }
            //System.out.println(cookie1.getValue());


        jwtTokenUtil.invalidateToken(token);
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            ExceptionUtils.sendMessage(HttpStatus.UNAUTHORIZED,"USER_DISABLED");
        } catch (BadCredentialsException e) {
            ExceptionUtils.sendMessage(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS");
        } catch (Exception e) {
            ExceptionUtils.sendMessage(HttpStatus.FORBIDDEN,"Incorrect Username or Password");
        }
    }
}
