package com.example.enlaco.Controller;

import com.example.enlaco.DTO.MemberDTO;
import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Service.MemberService;
import com.example.enlaco.Service.StorageService;
import com.example.enlaco.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;
    private final MemberService memberService;
    private final UserService userService;

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    //상세보기
    @GetMapping("/detail")
    public String detail(int sid, Model model) throws Exception {
        StorageDTO storageDTO = storageService.detail(sid);

        model.addAttribute("storageDTO", storageDTO);
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
    public String insertForm(Model model) throws Exception {
        StorageDTO storageDTO = new StorageDTO();

        model.addAttribute("storageDTO", storageDTO);

        return "/storage/insert";
    }
    @PostMapping("/insert")
    public String insertProc(@Valid StorageDTO storageDTO, BindingResult bindingResult,
                             @RequestParam("mid") int mid,
                             @RequestParam(value = "image", required = false, defaultValue = "null") MultipartFile multipartFile) throws Exception {
        if (bindingResult.hasErrors()) {
            return "storage/insert";
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            storageService.insert(mid, storageDTO, multipartFile);
        } else {
            storageService.insert(mid, storageDTO, null); // 파일이 없는 경우에도 처리 가능하도록 null 전달
        }



        return "redirect:/storage/list";
    }

    //목록
    @GetMapping("/list")
    public String list(Principal principal, Model model, OAuth2UserRequest userRequest) throws Exception{
        int mid = memberService.findByMemail1(principal.getName());
        OAuth2User id = userService.loadUser(userRequest);

        MemberDTO memberDTO = memberService.detail(mid);
        List<StorageDTO> storageDTOS = storageService.list(mid);



        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate now = LocalDate.now();

            for (StorageDTO storageDTO : storageDTOS) {
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

            model.addAttribute("storageDTOS", storageDTOS);

        } catch (Exception e) {
            log.error("Error occurred while processing storage list: {}", e.getMessage());
            // 예외 처리 로직 추가

        }

        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("storageDTOS", storageDTOS);
        model.addAttribute("mid", mid);

        //s3 이미지 전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/storage/list";
    }


    //수정창
    @GetMapping("/modify")
    public String modifyForm(Principal principal, int sid, Model model) throws Exception {
        String writer = principal.getName();
        int mid = memberService.findByMemail1(writer);

        StorageDTO storageDTO = storageService.detail(sid);

        model.addAttribute("writer", writer);
        model.addAttribute("mid", mid);
        model.addAttribute("storageDTO", storageDTO);
        //S3 이미지정보전달
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "/storage/modify";
    }
    @PostMapping("/modify")
    public String modifyProc(@Valid StorageDTO storageDTO,
                             BindingResult bindingResult,
                             Principal principal,
                             MultipartFile imgFile, Model model) throws Exception {
        String memail = principal.getName();

        model.addAttribute("mid", memail);
        storageService.modify(storageDTO, memail, imgFile);
        return "redirect:/storage/list";
    }

    //삭제
    @GetMapping("/remove")
    public String remove(int sid, MultipartFile imgFile, Model model) throws Exception {
        storageService.remove(sid, imgFile);
        return "redirect:/storage/list";
    }
}
