package com.example.enlaco.Config.oauth;

import com.example.enlaco.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
}
