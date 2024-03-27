package com.example.enlaco.Controller;

import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Service.LikeService;
import com.example.enlaco.Service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;
    private final UsersService usersService;

    @PostMapping("up/{rid}")
    public ResponseEntity<?> addLike(HttpSession session, @PathVariable("rid")@Positive int rid) throws Exception {

        //이메일을 불러옴
        String email = (String) session.getAttribute("userEmail");
        UsersEntity users = usersService.getUserByEmail(email);
        //id랑 유저 추가
        likeService.addLike(rid, users);
        return ResponseEntity.status(HttpStatus.CREATED).build();
        // AJAX 요청에 대한 응답을 반환
        //return success(null); // 적절한 응답 메시지를 반환

    }

}
