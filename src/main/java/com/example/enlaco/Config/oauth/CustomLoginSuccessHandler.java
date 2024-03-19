package com.example.enlaco.Config.oauth;

import com.example.enlaco.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;

    /*
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        if (session != null) { //섹션이 존재하면 로그인한 이메일을 등록
            String email = authentication.getName();
            String oauthType = "";
            userService.userIdToSession(session,email, oauthType);
        }

        super.setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request,response,authentication);
    }

     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth 2.0 로그인인 경우
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // 여기에서 OAuth 2.0 로그인 사용자에 대한 추가 처리를 수행할 수 있습니다.
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // 폼 로그인인 경우
            UsernamePasswordAuthenticationToken formToken = (UsernamePasswordAuthenticationToken) authentication;
            // 여기에서 폼 로그인 사용자에 대한 추가 처리를 수행할 수 있습니다.
        }

        HttpSession session = request.getSession();
        if (session != null) {
            String email = authentication.getName();
            String oauthType = ""; // OAuth 2.0 로그인이 아닌 경우에도 oauthType을 설정해야 할 수 있습니다.
            userService.userIdToSession(session, email, oauthType);
        }

        super.setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
