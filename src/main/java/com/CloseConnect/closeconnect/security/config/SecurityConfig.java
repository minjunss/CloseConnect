package com.CloseConnect.closeconnect.security.config;

import com.CloseConnect.closeconnect.security.oatuh2.CustomLogoutSuccessHandler;
import com.CloseConnect.closeconnect.security.oatuh2.OAuth2AuthenticationFailureHandler;
import com.CloseConnect.closeconnect.security.oatuh2.OAuth2AuthenticationSuccessHandler;
import com.CloseConnect.closeconnect.security.oatuh2.cookie.CookieAuthorizationRequestRepository;
import com.CloseConnect.closeconnect.security.oatuh2.jwt.JwtAuthenticationFilter;
import com.CloseConnect.closeconnect.security.oatuh2.jwt.JwtTokenProvider;
import com.CloseConnect.closeconnect.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final MemberService memberService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "OPTIONS"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setMaxAge(360000L);
            return config;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //httpBasic, csrf, formLogin, rememberMe, logout, session disable

        http
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //요청에 대한 권한 설정
        http.authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                        .requestMatchers("/oauth2/authorize/**", "/ws/**").permitAll()
                        .requestMatchers("/api/test**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        .anyRequest().authenticated());

        //OAuth2Login 설정
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorize") // 소셜 로그인 url
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository) // 인증 요청을 cookie 에 저장
                )
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/oauth2/callback/*")) // 소셜 인증 후 redirect url
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(memberService)) //OAuth2 인증 과정에서 Authentication 생성에 필요한 OAuth2User 를 반환하는 클래스를 지정
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );

        http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                .logoutUrl("/oauth2/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler));

        //jwt filter 설정
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
