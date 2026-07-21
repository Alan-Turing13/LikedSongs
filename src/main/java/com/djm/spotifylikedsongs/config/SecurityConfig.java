package com.djm.spotifylikedsongs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
            ).csrf(c -> c.disable()
            ).sessionManagement(
    session -> session.sessionFixation().none());
        return http.build();
    }
}
