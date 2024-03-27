package com.example.enlaco.Controller;

import com.example.enlaco.Config.oauth.OAuthLoginSuccessHandler;
import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.DTO.RecipeDTO;
import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final UsersService usersService;
    private final StorageService storageService;

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    //상세페이지
    @GetMapping("/detail")
    public String detail(Principal principal, HttpSession session, int rid, Model model) throws Exception {
        /*
        String writer = "";
        if (principal == null) {
            writer = "";
        } else {
            writer = principal.getName();
        }a
         */
        String email = (String) session.getAttribute("userEmail");
        if (memberService.getUserByEmail(email) != null) {
            model.addAttribute("writer", email);
        } else if (usersService.getUserByEmail(email) != null) {
            model.addAttribute("writer", email);
        }

        recipeService.viewcnt(rid);
        recipeService.rgoodcnt(rid);

        RecipeDTO recipeDTO = recipeService.detail(rid);

        List<CommentDTO> commentDTOS = commentService.list(rid);

        model.addAttribute("recipeDTO", recipeDTO);
        model.addAttribute("commentDTOS", commentDTOS);



        //S3 이미지정보전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/recipe/detail";
    }



    //목록
    @GetMapping("/list")
    public String list(@PageableDefault(page = 1) Pageable pageable,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       Model model, /*Principal principal,*/ HttpSession session) throws Exception {
        /*String email = principal.getName();
        String username =  email.substring(0,email.lastIndexOf('@'));
        session.setAttribute("username",username);*/
        Page<RecipeDTO> recipeDTOS = recipeService.list(keyword, pageable);


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

        model.addAttribute("keyword", keyword);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
//
        /*model.addAttribute("username",username);*/

        return "/recipe/list";
    }

    //자세히 보기 리스트
    @GetMapping("/listClass")
    public String recipeClass(@RequestParam(value = "rtime", defaultValue = "") String rtime,
                              @RequestParam(value = "rclass", defaultValue = "") String rclass,
                              @PageableDefault(page = 1) Pageable pageable,
                              Model model) throws Exception {

        Page<RecipeDTO> recipeDTOS = recipeService.listClass(rtime, rclass, pageable);
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

        model.addAttribute("rtime", rtime);
        model.addAttribute("rclass", rclass);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/recipe/listrclass";
    }

    //입력
    @GetMapping("/insert")
    public String insertForm(HttpSession session, Model model) throws Exception {
        RecipeDTO recipeDTO = new RecipeDTO();

        String email = (String) session.getAttribute("userEmail");
        if (memberService.getUserByEmail(email) != null) {
            model.addAttribute("email", email);
        } else if (usersService.getUserByEmail(email) != null) {
            model.addAttribute("email", email);
        }

        model.addAttribute("recipeDTO", recipeDTO);
        return "/recipe/insert";
    }
    @PostMapping("/insert")
    public String insertProc( @Valid RecipeDTO recipeDTO,
                              BindingResult bindingResult,
                              Model model,
                              @RequestParam("email") String userEmail,
                              @RequestParam(value = "image") MultipartFile multipartFile) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("email", userEmail);
            String rrsel = new RecipeDTO().getRselect(); // 검증 오류 뜰 때 재료창에 쉼표 제거하기 위해 초기화
            //model.addAttribute("rselect", rrsel);
            return "recipe/insert";
        }
        /*
        // rselect에 입력 안 했을 때 다시 입력하게
        if (recipeDTO.getRselect().trim().equals(",") || recipeDTO.getRselect().trim().isEmpty()) {
            model.addAttribute("mid", mid);
            return "recipe/insert";
        }
         */
        if (memberService.getUserByEmail(userEmail) != null) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                recipeService.insertFormLogin(userEmail, recipeDTO, multipartFile);
            } else {
                recipeService.insertFormLogin(userEmail, recipeDTO, null);
            }
        } else if (usersService.getUserByEmail(userEmail) != null) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                recipeService.insertTokenLogin(userEmail, recipeDTO, multipartFile);
            } else {
                recipeService.insertTokenLogin(userEmail, recipeDTO, null);
            }
            //String rrsel = new RecipeDTO().getRselect(); // 검증 오류 뜰 때 재료창에 쉼표 제거하기 위해 초기화
            //model.addAttribute("rselect", rrsel);

        }

        //recipeService.insert(mid, recipeDTO, null); //파일이 없는 경우에도 처리
        return "redirect:/recipe/list";
    }

    //수정
    @GetMapping("/modify")
    public String modifyForm(HttpSession session, int rid, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");

        RecipeDTO recipeDTO = recipeService.detail(rid);
        String select = recipeDTO.getRselect();
        System.out.println("recipeDTO에서 가져온 rselect : " + select);

        String list[] = select.split(",");
        for (int i=0; i<list.length; i++) {
            System.out.println(list[i]);
        }

        //model.addAttribute("writer", writer);
        //model.addAttribute("mid", mid);
        model.addAttribute("email", email);
        model.addAttribute("recipeDTO", recipeDTO);
        model.addAttribute("list", list);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/recipe/modify";
    }
    @PostMapping("/modify")
    public String modifyProc(@Valid RecipeDTO recipeDTO,
                             BindingResult bindingResult,
                             Principal principal,
                             @RequestParam("email") String userEmail,
                             MultipartFile imgFile,
                              Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            return "recipe/modify";
        }
        //model.addAttribute("mid", memail);
        recipeService.modify(recipeDTO, userEmail, imgFile);

        return "redirect:/member/mypage";
    }

    //삭제
    @GetMapping("/remove")
    public String remove(int rid) throws Exception {
        recipeService.remove(rid);
        return "redirect:/member/mypage";
    }

    /*
    //레시피 추천
    @GetMapping("/recom")
    public String recom(HttpSession session, Model model) throws Exception {
        int mid = (int) session.getAttribute("mid");
        int userid = (int) session.getAttribute("userid");
        String email = (String) session.getAttribute("email");
        List<StorageDTO> storageDTOSForm = storageService.listForm(mid);
        List<StorageDTO> storageDTOSToken = storageService.listForm(userid);

        List<StorageDTO> combinedStorageDTOS = new ArrayList<>();
        combinedStorageDTOS.addAll(storageDTOSForm);
        combinedStorageDTOS.addAll(storageDTOSToken);
        List<RecipeDTO> recipeDTO = recipeService.recipeRecom(mid, email);


        model.addAttribute("storageDTOS", combinedStorageDTOS);
        model.addAttribute("recipeDTO", recipeDTO);
        return "/recipe/recommend";
    }

     */

    @GetMapping("/rgoodcnt")
    public String recipeRgoodcnt(int rid, RedirectAttributes redirectAttributes) throws Exception {
        recipeService.rgoodcnt(rid);

        redirectAttributes.addAttribute("rid", rid);

        return "redirect:/recipe/detail";
    }
}
