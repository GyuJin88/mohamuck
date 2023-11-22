package com.example.enlaco.Controller;

import com.example.enlaco.DTO.StorageDTO;
import com.example.enlaco.Service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;

    //상세보기
    @GetMapping("/detail")
    public String detail(int sid, Model model) throws Exception {
        StorageDTO storageDTO = storageService.detail(sid);

        model.addAttribute("storageDTO", storageDTO);

        return "/storage/detail";
    }
    //입력창
    @GetMapping("/insert")
    public String insertForm(Model model) throws Exception {
        StorageDTO storageDTO = new StorageDTO();
        model.addAttribute("storageDTO", storageDTO);

        return "storage/insert";
    }
    @PostMapping("/insert")
    public String insertProc(@Valid StorageDTO storageDTO, BindingResult bindingResult,
                             MultipartFile imgFile) throws Exception {
        if (bindingResult.hasErrors()) {
            return "storage/insert";
        }

        storageService.insert(storageDTO, imgFile);

        return "redirect:/storage/list";
    }
    //목록
    @GetMapping("/list")
    public String list(Model model) throws Exception {
        List<StorageDTO> storageDTOS = storageService.list();

        model.addAttribute("storageDTOS", storageDTOS);

        return "/storage/list";
    }
    //수정창
    @GetMapping("/modify")
    public String modifyForm() throws Exception {
        return "storage/modify";
    }
    @PostMapping("/modify")
    public String modifyProc() throws Exception {
        return "redirect:/storage/list";
    }
    //삭제
    @GetMapping("/remove")
    public String remove(int sid, Model model) throws Exception {
        storageService.remove(sid);
        return "redirect:/storage/list";
    }
}
