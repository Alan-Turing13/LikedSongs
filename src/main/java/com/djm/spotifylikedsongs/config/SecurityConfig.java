package com.djm.spotifylikedsongs.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/home",
                        "/index",
                        "/liked-songs/**",
                        "/css/**",
                        "/songs",
                        "/login",
                        "/callback",
                        "/error")
                    .permitAll()
                .anyRequest().authenticated()
            ).oauth2Login(oauth2 -> oauth2.disable()
            ).csrf(c -> c.disable());
        return http.build();
    }
}
