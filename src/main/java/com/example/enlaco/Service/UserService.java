package com.example.enlaco.Service;

import com.example.enlaco.Constant.Role;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.UserEntity;
import com.example.enlaco.Repository.MemberRepository;
import com.example.enlaco.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();
        log.info("ATTR INFO : {}", attributes.toString());

        String email = null;
        String name = (String) attributes.get("name");
        String oauthType = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User user2 = super.loadUser(userRequest);

        // oauth 타입에 따라 데이터가 다르기에 분기
        if("kakao".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
        }
        else if("google".equals(oauthType.toLowerCase())) {
            email = attributes.get("email").toString();
        }
        else if("naver".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) attributes.get("response")).get("email").toString();
        }

        // User 존재여부 확인 및 없으면 생성
        if(getUserByEmailAndOAuthType(email, oauthType) == null) {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setNickname(name);
            user.setOauthType(oauthType);
            user.setRole(Role.USER);
            save(user);
        }

        return super.loadUser(userRequest);
    }

    // 저장, 조회만 수행. 기타 예외처리 및 다양한 로직은 연습용이므로
    public void save(UserEntity user) {
        userRepository.save(user);
    }

    public UserEntity getUserByEmailAndOAuthType(String email, String oauthType) {
        return userRepository.findByEmailAndOauthType(email, oauthType).orElse(null);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    //로그인시 회원테이블에서 필요한 값을 섹션에 저장
    public void userIdToSession(HttpSession session, String email, String oauthType) {
        UserEntity user = getUserByEmail(email);

        if(user != null) {
            session.setAttribute("userEmail", user.getEmail()); //아이디(이메일)
            session.setAttribute("userId", user.getId()); //회원이 저장된 번호
            session.setAttribute("userNickname", user.getNickname()); //회원이름
            session.setAttribute("userRole", user.getRole()); //회원등급
        }
    }

    /*
    //로그인시 필요한 값을 세션에 저장
    public void toSession(HttpSession session, String email) {
        MemberEntity member = getUserByEmail(email);

        if (member != null) {
            session.setAttribute("userEmail", member.getMemail());
            session.setAttribute("mid", member.getMid());
            session.setAttribute("mnickname", member.getMnick());
        }
    }
    public MemberEntity getUserByEmail(String email) {
        return userRepository.findByMemail(email).orElse(null);
    }

     */
}
