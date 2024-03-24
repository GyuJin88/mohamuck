package com.example.enlaco.Service;

import com.example.enlaco.Constant.Role;
import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Repository.UsersRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UsersRepository usersRepository;

    //@Getter
    @Value("${admin.email}")
    private String adminEmail;


    public boolean isAdminLoggedIn(HttpSession session) {
        // 세션에서 현재 로그인한 사용자의 이메일을 가져옵니다.
        String userEmail = (String) session.getAttribute("userEmail");

        // 관리자 이메일과 세션의 이메일을 비교하여 관리자로 로그인되었는지 확인합니다.
        return userEmail != null && userEmail.equals(adminEmail);
    }

    public void grantAdminRole(String userEmail) {
        UsersEntity user = usersRepository.findByEmailIgnoreCase(userEmail);
        if (user != null && userEmail.equals(adminEmail)) {
            user.setRole(Role.ADMIN); // 사용자의 역할을 관리자로 설정
            usersRepository.save(user); // 변경된 사용자 정보를 저장
        } else {
            // 사용자를 찾지 못하거나 사용자의 이메일이 관리자 이메일이 아닌 경우 예외 처리
            throw new IllegalArgumentException("Invalid user or not an admin email: " + userEmail);
        }
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}
