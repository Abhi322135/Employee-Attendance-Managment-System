package com.javadeveloperzone.config;

import com.javadeveloperzone.component.JwtComponent.JWTRequestFilter;
import com.javadeveloperzone.models.FolderModel.Role;
import com.javadeveloperzone.service.AuthenticationService.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserAuthenticationService userAuthenticationService;
    private final JWTRequestFilter jwtRequestFilter;
    @Autowired
    public SpringSecurityConfig(UserAuthenticationService userAuthenticationService, JWTRequestFilter jwtRequestFilter){
        this.userAuthenticationService = userAuthenticationService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
         auth.userDetailsService(userAuthenticationService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/authenticate/user").permitAll()
                .antMatchers("/**/logout/user").hasAnyAuthority(Role.ADMIN.name(),Role.MANAGER.name(),Role.EMPLOYEE.name())
                .antMatchers("/**/admin/**").hasAuthority(Role.ADMIN.name())
                .antMatchers("/**/save").hasAuthority(Role.ADMIN.name())
                .antMatchers("/**/all").hasAuthority(Role.ADMIN.name())
                .antMatchers("/**/delete").hasAuthority(Role.ADMIN.name())
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();

    }
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
