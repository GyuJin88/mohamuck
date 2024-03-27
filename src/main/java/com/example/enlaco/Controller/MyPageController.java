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

    //마이페이지 내 레시피 관리
    @GetMapping("/member/mypage")
    public String mypageRecipeList(@PageableDefault(page = 1) Pageable pageable,
                                HttpSession session, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");

        Page<RecipeDTO> recipeDTOS = myPageService.recieListTokenLogin(email, pageable);
        setRecipeInfo(recipeDTOS, model);

        Page<CommentDTO> commentDTOS = myPageService.commentListTokenLogin(email, pageable);
        setCommentInfo(commentDTOS, model);

        return "/member/mypageRecipe";
    }

    /*
    //마이페이지 내 댓글 관리
    @GetMapping("/member/mypage")
    public String myCommentPage(@PageableDefault(page = 1) Pageable pageable, HttpSession session, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");

        // 댓글 페이징 처리
        Page<CommentDTO> commentDTOS = myPageService.commentListTokenLogin(email, pageable);

        // 페이징 정보 설정
        setCommentInfo(commentDTOS, model);

        return "/member/mypage";
    }

     */

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

    // 페이지 정보 설정 메소드
    private void setRecipeInfo(Page<RecipeDTO> recipeDTOS, Model model) {
        int blockLimit = 5;
        // 페이지 정보
        int rstartPage = Math.max(1, recipeDTOS.getNumber() / 5 * 5 + 1);
        int rendPage = Math.min(rstartPage + 4, recipeDTOS.getTotalPages());
        int rprevPage = Math.max(1, recipeDTOS.getNumber() - 1);
        int rcurPage = recipeDTOS.getNumber() + 1;
        int rnextPage = Math.min(recipeDTOS.getNumber() + 1, recipeDTOS.getTotalPages());
        int rlastPage = recipeDTOS.getTotalPages();

        model.addAttribute("recipeDTOS", recipeDTOS);
        model.addAttribute("rstartPage", rstartPage);
        model.addAttribute("rendPage", rendPage);
        model.addAttribute("rprevPage", rprevPage);
        model.addAttribute("rcurPage", rcurPage);
        model.addAttribute("rnextPage", rnextPage);
        model.addAttribute("rlastPage", rlastPage);
    }

    private void setCommentInfo(Page<CommentDTO> commentDTOS, Model model) {

        int cstartPage = Math.max(1, commentDTOS.getNumber() / 5 * 5 + 1);
        int cendPage = Math.min(cstartPage + 4, commentDTOS.getTotalPages());
        int cprevPage = Math.max(1, commentDTOS.getNumber() - 1);
        int ccurPage = commentDTOS.getNumber() + 1;
        int cnextPage = Math.min(commentDTOS.getNumber() + 1, commentDTOS.getTotalPages());
        int clastPage = commentDTOS.getTotalPages();

        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("cstartPage", cstartPage);
        model.addAttribute("cendPage", cendPage);
        model.addAttribute("cprevPage", cprevPage);
        model.addAttribute("ccurPage", ccurPage);
        model.addAttribute("cnextPage", cnextPage);
        model.addAttribute("clastPage", clastPage);

    }


}
