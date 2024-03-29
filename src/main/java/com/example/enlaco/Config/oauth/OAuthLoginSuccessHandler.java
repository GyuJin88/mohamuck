package com.example.enlaco.Config.oauth;

import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UsersService usersService;

    @Value("${admin.email}")
    public String adminEmail;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 토큰에서 email, oauthType 추출
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String email = null;
        String oauthType = token.getAuthorizedClientRegistrationId();

        // oauth 타입에 따라 데이터가 다르기에 분기
        if("kakao".equals(oauthType.toLowerCase())) {
            // kakao는 kakao_account 내에 email이 존재함.
            email = ((Map<String, Object>) token.getPrincipal().getAttribute("kakao_account")).get("email").toString();
        }
        else if("google".equals(oauthType.toLowerCase())) {
            email = token.getPrincipal().getAttribute("email").toString();
        }
        else if("naver".equals(oauthType.toLowerCase())) {
            // naver는 response 내에 email이 존재함.
            email = ((Map<String, Object>) token.getPrincipal().getAttribute("response")).get("email").toString();
        }

        log.info("LOGIN SUCCESS : {} FROM {}", email, oauthType);

        UsersEntity user = usersService.getUserByEmailAndOAuthType(email, oauthType);

        // 관리자 여부 확인
        boolean isAdmin = email.equals(adminEmail);

        // 세션에 user 저장
        log.info("USER SAVED IN SESSION");
        HttpSession session = request.getSession();

        if(session != null) {
            //session.setAttribute("user", email);
            usersService.userIdToSession(session, email, oauthType);
            session.setAttribute("ADMIN", isAdmin); // 관리자 여부 저장
        }


        super.onAuthenticationSuccess(request, response, authentication);


    }

}
