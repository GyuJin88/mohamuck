package com.example.enlaco.Controller;

import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final UsersService usersService;
    private final RecipeService recipeService;
    private final CommentService commentService;
    private final MyPageService myPageService;

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    @GetMapping("/member/mypage")
    public String mypageList(@PageableDefault(page = 1) Pageable pageable,
                                HttpSession session, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");

        Page<RecipeDTO> recipeDTOS = myPageService.listTokenLogin(email, pageable);

        /*
        //페이지 정보
        int blockLimit = 5;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1) * blockLimit+1;
        int endPage = ((startPage+blockLimit-1)<recipeDTOS.getTotalPages())? startPage+blockLimit-1:recipeDTOS.getTotalPages();

        int prevPage = recipeDTOS.getNumber();
        int curPage = recipeDTOS.getNumber()+1;
        int nextPage = recipeDTOS.getNumber()+2;
        int lastPage = recipeDTOS.getTotalPages();

         */

        // 페이지 정보
        int startPage = Math.max(1, recipeDTOS.getNumber() / 5 * 5 + 1);
        int endPage = Math.min(startPage + 4, recipeDTOS.getTotalPages());
        int prevPage = Math.max(1, recipeDTOS.getNumber() - 1);
        int curPage = recipeDTOS.getNumber() + 1;
        int nextPage = Math.min(recipeDTOS.getNumber() + 1, recipeDTOS.getTotalPages());
        int lastPage = recipeDTOS.getTotalPages();

        model.addAttribute("recipeDTOS", recipeDTOS);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("curPage", curPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);


        return "/member/mypage";
    }

    @GetMapping("/member/myrecipedetail") public String myrecipedetail(HttpSession session,
                                                                int rid, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");
        RecipeDTO recipeDTO = recipeService.detail(rid);
        List<CommentDTO> commentDTOS = commentService.list(rid);

        model.addAttribute("userEmail", email);
        model.addAttribute("recipeDTO", recipeDTO);
        model.addAttribute("commentDTOS", commentDTOS);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/member/myrecipedetail";
    }
}
