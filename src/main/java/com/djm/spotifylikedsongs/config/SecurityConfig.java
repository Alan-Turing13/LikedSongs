package com.djm.spotifylikedsongs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/home", "/index","/liked-songs/", "/liked-songs/get", "/css/**")
                    .permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2.disable()
//                .loginPage("/login")
//                .defaultSuccessUrl("/liked-songs/get", true)
            );
        return http.build();
    }
}
