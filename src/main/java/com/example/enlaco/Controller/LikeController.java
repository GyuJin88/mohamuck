package com.example.enlaco.Controller;

import com.example.enlaco.Entity.RecipeEntity;
import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Service.LikeService;
import com.example.enlaco.Service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Positive;
import java.util.Map;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;
    private final UsersService usersService;


    @PostMapping("/api/likes/up/{rid}")
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


    /*
    @PutMapping("/recipe/modify/like")
    public boolean modifyRecipeLike(HttpSession session,
                                    @RequestParam("postNum") int rid) throws Exception {
        //int rid = Integer.valueOf(params.get("postNum"));
        String email = (String) session.getAttribute("userEmail");
        UsersEntity users = usersService.getUserByEmail(email);

        //좋아요가 새로 생겼다면 true, 기존에 좋아요가 있었다면 false
        return likeService.modifyLikeStatus(users, rid);
    }

     */

}
