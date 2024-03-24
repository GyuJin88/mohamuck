package com.example.enlaco.Controller;

import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
//@RequestMapping("/manager")
public class ManagerController {
    private final RecipeService recipeService;
    private final CommentService commentService;
    private final StorageService storageService;
    private final MemberService memberService;
    private final AdminService adminService;

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;



    @GetMapping("/manager/list")
    public String list(@PageableDefault(page = 1) Pageable pageable, String keyword, Model model, HttpSession session) throws Exception {
        /*
        if (page < 0) {
            page = 0; // 페이지 번호가 0보다 작을 경우 0으로 설정
        }
        Pageable pageable = PageRequest.of(page, 10); // PAGE_SIZE에는 한 페이지당 아이템 수가 들어가야 합니다.

         */

        // 관리자로 로그인되었는지 확인
        boolean isAdminLoggedIn = adminService.isAdminLoggedIn(session);
        if (!isAdminLoggedIn) {
            // 만약 관리자가 아니라면 로그인 페이지로 리다이렉트 또는 다른 처리a
            return "redirect:/login";
        } else {
            String userEmail = (String) session.getAttribute("userEmail");
            adminService.grantAdminRole(userEmail); // 관리자로 로그인한 경우 관리자 권한 부여
        }

        Page<RecipeDTO> recipeDTOS = recipeService.list(keyword, pageable);
        Page<MemberDTO> memberDTOS = memberService.managerList(pageable);

        //페이지 정보
        int blockLimit = 5;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1) * blockLimit+1;
        int endPage = ((startPage+blockLimit-1)<recipeDTOS.getTotalPages())? startPage+blockLimit-1:recipeDTOS.getTotalPages();

        int prevPage = recipeDTOS.getNumber();
        int curPage = recipeDTOS.getNumber()+1;
        int nextPage = recipeDTOS.getNumber()+2;
        int lastPage = recipeDTOS.getTotalPages();

        model.addAttribute("adminEmail", adminService.getAdminEmail());

        model.addAttribute("recipeDTOS", recipeDTOS);
        model.addAttribute("memberDTOS", memberDTOS);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("curPage", curPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/manager/list";
    }

    //레시피 상세보기
    @GetMapping("/manager/detail")
    public String detail(Principal principal, int rid, Model model) throws Exception {
        String writer = "";
        if (principal == null) {
            writer = "";
        } else {
            writer = principal.getName();
        }

        recipeService.viewcnt(rid);


        RecipeDTO recipeDTO = recipeService.detail(rid);

        List<CommentDTO> commentDTOS = commentService.list(rid);

        model.addAttribute("recipeDTO", recipeDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("writer", writer);

        //S3 이미지정보전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/manager/recipeDetail";
    }

    //레시피 삭제
    @GetMapping("/manager/remove")
    public String remove(int rid) throws Exception {
        recipeService.remove(rid);
        return "redirect:/manager/list";
    }

    //회원삭제
    @GetMapping("/manager/mremove")
    public String mremove(Integer mid, Integer rid, Integer sid, MultipartFile imgFile) throws Exception {
        if (rid != null) {
            recipeService.remove(rid);
        } else {
            // rid가 null인 경우에 대한 추가 작업 수행 (예: 로깅)
        }

        if (sid != null) {
            storageService.remove(sid, imgFile);
        } else {
            // sid가 null인 경우에 대한 추가 작업 수행 (예: 로깅)
        }

        if (mid != null) {
            memberService.remove(mid);
        } else {
            // mid가 null인 경우에 대한 추가 작업 수행 (예: 로깅)
        }

        return "redirect:/manager/list";
    }

    @GetMapping("/fragments/header")
    public String fragmentHeader(Model model, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");

        model.addAttribute("adminEmail", adminService.getAdminEmail());
        model.addAttribute("userEmail", email);
        return "/fragments/header";
    }




}
