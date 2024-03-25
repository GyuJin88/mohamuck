package com.example.enlaco.Controller;

import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Service.CommentService;
import com.example.enlaco.Service.MemberService;
import com.example.enlaco.Service.RecipeService;
import com.example.enlaco.Service.UsersService;
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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final UsersService usersService;
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

    /*
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");

        List<RecipeDTO> recipeDTOSForm = recipeService.listFormLoginMypage(email);
        List<RecipeDTO> recipeDTOSToken = recipeService.listTokenLoginMypage(email);

        List<RecipeDTO> combinedStorageDTOS = new ArrayList<>();
        combinedStorageDTOS.addAll(recipeDTOSForm);
        combinedStorageDTOS.addAll(recipeDTOSToken);

        //MemberDTO memberDTO = memberService.detail(mid);
        //List<RecipeDTO> recipeDTOS = memberService.list(mid);

        //model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("recipeDTOS", combinedStorageDTOS);
        //model.addAttribute("mid", mid);
        return "/member/mypage";
    }

     */



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

