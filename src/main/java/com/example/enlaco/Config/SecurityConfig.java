package com.example.enlaco.Config;

import com.example.enlaco.Config.oauth.CustomLoginSuccessHandler;
import com.example.enlaco.Config.oauth.OAuthLoginFailureHandler;
import com.example.enlaco.Config.oauth.OAuthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    //1. 암호의 암호화
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //2. 커스텀 로그인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //매핑에 따른 접근권한 부여
        http.authorizeHttpRequests((auth)->{
            auth.antMatchers("/","/recipe/list","/recipe/detail","/member/login","/member/insert").permitAll();
            auth.antMatchers("/recipe/insert","/recipe/modify","/recipe/recom","/storage/list","/storage/detail","/storage/insert","/storage/modify","/storage/remove","/member/mypage").hasAnyRole("USER", "ADMIN");
        });

        //로그인 처리에 대한 설정
        http.formLogin()
                .loginPage("/member/login")
                .successHandler(customLoginSuccessHandler)
                /*.defaultSuccessUrl("/recipe/list")*/
                .usernameParameter("memail")
                .passwordParameter("mpwd")
                .failureUrl("/member/login/error");
        //페이지 변조방지 사용 안함.
        http.csrf().disable();

        //로그아웃에 대한 설정
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/");
        http.oauth2Login()
                .loginPage("/member/login")
                .successHandler(oAuthLoginSuccessHandler)
                .failureHandler(oAuthLoginFailureHandler);
        /*
        http.rememberMe()
                .key("uniqueAndSecret")
                .rememberMeParameter("autoLogin")
                .rememberMeCookieName("rememberMeCookie")
                .tokenValiditySeconds(60*60*24*30); //초*분*시*일=30일간 유지
         */
        return http.build();
    }
}
