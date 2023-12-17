package com.example.enlaco.Controller;

import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Service.CommentService;
import com.example.enlaco.Service.MemberService;
import com.example.enlaco.Service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final RecipeService recipeService;
    private final CommentService commentService;

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    @GetMapping("/insert")
    public String insertForm(Model model) throws Exception {
        MemberDTO memberDTO = new MemberDTO();

        model.addAttribute("memberDTO", memberDTO);
        return "/member/insert";
    }
    @PostMapping("/insert")
    public String insertProc(@Valid MemberDTO memberDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()) {
            return "member/insert";
        }

        try {
            memberService.saveMember(memberDTO);
        } catch (IllegalStateException e) {
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
            return "member/insert";
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("errorMessage", "이메일이나 닉네임 중 이미 사용중인 값이 있습니다.");
            return "member/insert";
        }
        return "redirect:/";
    }
    //수정창
    @GetMapping("/modify")
    public String modifyForm(Principal principal, Model model) throws Exception {
        String memail = principal.getName(); // 현재 로그인한 사용자의 이메일
        int mid = memberService.findByMemail1(memail); // 이메일로 회원 정보 조회
        model.addAttribute("mid", mid);

        MemberDTO memberDTO = memberService.read(mid);

        model.addAttribute("memberDTO", memberDTO);

        return "/member/modify";
    }


    @PostMapping("/modify")
    public String modfiyProc(MemberDTO memberDTO, Model model) throws Exception {
        String memail = memberDTO.getMemail();

        //model.addAttribute("role", c)
        memberService.modifyMember(memberDTO, memail);
        return "redirect:/member/mypage";
    }


    @GetMapping("/login")
    public String login() throws Exception {

        return "/member/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) throws Exception {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해 주세요.");
        return "/member/login";
    }

    @GetMapping("/mypage")
    public String mypage(Principal principal,
                         Model model) throws Exception {
        int mid = memberService.findByMemail1(principal.getName());

        MemberDTO memberDTO = memberService.detail(mid);
        List<RecipeDTO> recipeDTOS = memberService.list(mid);

        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("recipeDTOS", recipeDTOS);
        model.addAttribute("mid", mid);
        return "/member/mypage";
    }
    /*public String mypage(@PageableDefault(page = 1) Pageable pageable,
                         Model model) throws Exception {
        int mid = 1;
        Page<RecipeDTO> recipeDTOS = memberService.myList(mid, pageable);

        //페이지 정보
        int blockLimit = 5;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1) * blockLimit+1;
        int endPage = ((startPage+blockLimit-1)<recipeDTOS.getTotalPages())? startPage+blockLimit-1:recipeDTOS.getTotalPages();

        int prevPage = recipeDTOS.getNumber();
        int curPage = recipeDTOS.getNumber()+1;
        int nextPage = recipeDTOS.getNumber()+2;
        int lastPage = recipeDTOS.getTotalPages();

        model.addAttribute("recipeDTOS", recipeDTOS);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("curPage", curPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        return "member/mypage";
    }*/

    @GetMapping("/myrecipedetail")
    public String myrecipedetail(Principal principal,
            int rid, Model model) throws Exception {
        int mid = memberService.findByMemail1(principal.getName());
        RecipeDTO recipeDTO = recipeService.detail(rid);
        List<CommentDTO> commentDTOS = commentService.list(rid);

        model.addAttribute("mid", mid);
        model.addAttribute("recipeDTO", recipeDTO);
        model.addAttribute("commentDTOS", commentDTOS);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/member/myrecipedetail";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expiredCookie(response, "memberId");
        return "redirect:/";
    }

    private void expiredCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

