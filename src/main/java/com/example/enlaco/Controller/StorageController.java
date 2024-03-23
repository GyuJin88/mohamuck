package com.example.enlaco.Controller;

import com.example.enlaco.Config.oauth.OAuthLoginSuccessHandler;
import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Entity.MemberEntity;
import com.example.enlaco.Entity.UsersEntity;
import com.example.enlaco.Service.MemberService;
import com.example.enlaco.Service.StorageService;
import com.example.enlaco.Service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.sql.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;
    private final MemberService memberService;
    private final UsersService usersService;

    @Autowired
    private OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(StorageController.class);

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    //상세보기
    @GetMapping("/detail")
    public String detail(String email, int sid, Model model) throws Exception {

        //List<StorageDTO> combinedStorageDTOS = new ArrayList<>();
        StorageDTO storageDTOForm = storageService.detailFormLogin(sid);
        //StorageDTO storageDTOToken = storageService.detailTokenLogin(email);

        //combinedStorageDTOS.add(storageDTOForm);
        //combinedStorageDTOS.add(storageDTOToken);

        model.addAttribute("storageDTO", storageDTOForm);
        //S3 이미지정보전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        //S3 이미지정보전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/storage/detail";
    }
    //입력창
    @GetMapping("/insert")
    public String insertForm(Model model, HttpSession session) throws Exception {
        StorageDTO storageDTO = new StorageDTO();

        // 세션에서 이메일 가져오기
        String email = (String) session.getAttribute("userEmail");
        if (memberService.getUserByEmail(email) != null) {
            model.addAttribute("email", email);
        } else if (usersService.getUserByEmail(email) != null) {
            model.addAttribute("email", email);
        }
            // 모델에 추가
            model.addAttribute("storageDTO", storageDTO);

            return "/storage/insert";

    }

    @PostMapping("/insert")
    public String insertProc(@Valid StorageDTO storageDTO, BindingResult bindingResult,
                             @RequestParam("email") String userEmail,
                             @RequestParam(value = "image", required = false, defaultValue = "null")
                                    MultipartFile multipartFile, Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "storage/insert";
        }

        if (memberService.getUserByEmail(userEmail) != null) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                storageService.insertFormLogin(userEmail, storageDTO, multipartFile); // 폼 로그인된 사용자로 처리
            } else {
                storageService.insertFormLogin(userEmail, storageDTO, null); // 폼 로그인된 사용자로 처리, 파일이 없는 경우에도 처리 가능하도록 null 전달
            }
        } else if (usersService.getUserByEmail(userEmail) != null) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                storageService.insertTokenLogin(userEmail, storageDTO, multipartFile); // 구글 로그인된 사용자로 처리
            } else {
                storageService.insertTokenLogin(userEmail, storageDTO, null); // 구글 로그인된 사용자로 처리, 파일이 없는 경우에도 처리 가능하도록 null 전달
            }
        }

        return "redirect:/storage/list";

    }

    //목록
    @GetMapping("/list")
    public String list(HttpSession session, Model model) throws Exception {
        String email = (String) session.getAttribute("userEmail");

        List<StorageDTO> storageDTOSForm = storageService.listFormLogin(email);
        List<StorageDTO> storageDTOSToken = storageService.listTokenLogin(email);

        List<StorageDTO> combinedStorageDTOS = new ArrayList<>();
        combinedStorageDTOS.addAll(storageDTOSForm);
        combinedStorageDTOS.addAll(storageDTOSToken);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate now = LocalDate.now();

            for (StorageDTO storageDTO : combinedStorageDTOS) {
                String syutong = storageDTO.getSyutong();

                if (syutong == null || syutong.isEmpty()) {
                    String dDay = "D-9999";
                    storageDTO.setDDay(dDay);

                    // 오류 처리 또는 기본값 설정 등을 수행
                    // 예: throw new IllegalArgumentException("날짜 문자열이 비어있습니다.");
                    // 또는 기본값을 설정하거나 다른 처리를 수행할 수 있음
                    continue; // 빈 문자열이면 다음 반복으로 건너뜀
                }

                LocalDate endDate = LocalDate.parse(storageDTO.getSyutong(), formatter);
                long daysUntilEnd = ChronoUnit.DAYS.between(now, endDate);
                String dDay;
                if (now.isBefore(endDate)) {
                    dDay = "D-" + daysUntilEnd;
                } else if (now.equals(endDate)) {
                    dDay = "D-DAY";
                } else {
                    dDay = "D+" + Math.abs(daysUntilEnd);
                    String dDayOver = "유통기한 지남";

                    storageDTO.setDDay(dDay); // StorageDTO에 D-Day 값을 저장
                    storageDTO.setDDayOver(dDayOver);
                    continue;
                }
                storageDTO.setDDay(dDay);
            }

            model.addAttribute("storageDTOS", combinedStorageDTOS);

        } catch (Exception e) {
            log.error("Error occurred while processing storage list: {}", e.getMessage());
            // 예외 처리 로직 추가

        }
        model.addAttribute("storageDTOS", combinedStorageDTOS);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/storage/list";
    }


    //수정창
    @GetMapping("/modify")
    public String modifyForm(HttpSession session, Principal principal, int sid, Model model) throws Exception {

        String email = (String) session.getAttribute("userEmail");

        if(memberService.getUserOptionalEmail(email) != null) {
            model.addAttribute("email", email);
        } else if (usersService.getUserByEmail(email) != null) {
            model.addAttribute("email", email);
        }

        //List<StorageDTO> combinedStorageDTOS = new ArrayList<>();
        StorageDTO storageDTOForm = storageService.detailFormLogin(sid);
        //StorageDTO storageDTOToken = storageService.detailTokenLogin(email);

        //combinedStorageDTOS.add(storageDTOForm);
        //combinedStorageDTOS.add(storageDTOToken);

        //model.addAttribute("mid", mid);
        //model.addAttribute("userid", userid);
        model.addAttribute("storageDTO", storageDTOForm);

        //S3 이미지정보전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/storage/modify";
    }
    @PostMapping("/modify")
    public String modifyProc(@Valid StorageDTO storageDTO,
                             BindingResult bindingResult,
                             @RequestParam("email") String userEmail,
                             Principal principal,
                             MultipartFile imgFile, Model model) throws Exception {
        //String email = principal.getName();


        if (imgFile != null && !imgFile.isEmpty()) {
            storageService.modify( storageDTO, userEmail, imgFile); // 폼 로그인된 사용자로 처리
        } else {
            storageService.modify( storageDTO, userEmail, null);// 폼 로그인된 사용자로 처리, 파일이 없는 경우에도 처리 가능하도록 null 전달
        }

        //model.addAttribute("email", email);
        //model.addAttribute("userid", email);
        //storageService.modify(storageDTO, userEmail, multipartFile);
        return "redirect:/storage/list";
    }

    //삭제
    @GetMapping("/remove")
    public String remove(int sid, MultipartFile imgFile, Model model) throws Exception {
        storageService.remove(sid, imgFile);
        return "redirect:/storage/list";
    }
}
