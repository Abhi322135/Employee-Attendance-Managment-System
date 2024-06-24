package com.javadeveloperzone.component.JwtComponent;

import com.javadeveloperzone.constant.CookieConstant;
import com.javadeveloperzone.constant.JWTMessages;
import com.javadeveloperzone.service.AuthenticationService.UserAuthenticationService;
import com.javadeveloperzone.service.AuthenticationService.UserExtend;
import io.jsonwebtoken.ExpiredJwtException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    private final UserAuthenticationService userAuthentication;
    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    public JWTRequestFilter(UserAuthenticationService userAuthentication, JWTTokenUtil jwtTokenUtil) {
        this.userAuthentication = userAuthentication;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader(CookieConstant.AUTHORIZATION);
        String username = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith(CookieConstant.BEARER)) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getEmailFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println(JWTMessages.JWT_NOT_FOUND);
            } catch (ExpiredJwtException e) {
                System.out.println(JWTMessages.JWT_EXPIRED);
            }
        }
        else if (requestTokenHeader==null ) {
            jwtToken = extractTokenFromCookie(request);

            if(jwtToken!=null) {
                username = jwtTokenUtil.getEmailFromToken(jwtToken);
            }
        } else {
            logger.warn(JWTMessages.JWT_NOT_BEARER);
        }
        if (jwtTokenUtil.isTokenInvalidated(jwtToken) || jwtToken==null){
            username=null;
        }

        // Once we get the token validate it.

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)     {

            UserExtend userDetails = (UserExtend)this.userAuthentication.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
           Cookie cookie1= Arrays.stream(cookies).filter((cookie)->cookie.getName().equals(CookieConstant.NAME)).findFirst().orElse(null);
           if (cookie1!=null) return cookie1.getValue();
        }
        return null;
    }

}
