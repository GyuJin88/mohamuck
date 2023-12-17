package com.example.enlaco.Controller;

import com.example.enlaco.DTO.CommentDTO;
import com.example.enlaco.Service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment/register")
    public String registerProc(@RequestParam("rid") Integer rid, CommentDTO commentDTO,
                               RedirectAttributes redirectAttributes) throws Exception {


        commentService.create(rid, commentDTO);

        redirectAttributes.addAttribute("rid", rid);
        return "redirect:/recipe/detail";
    }

    @PostMapping("/comment/modify")
    public String modifyProc() throws Exception {



        return "redirect:/recipe/detail";
    }

    @GetMapping("/comment/remove")
    public String removeProc(int no, int id, RedirectAttributes redirectAttributes) throws Exception {
        commentService.remove(id);

        redirectAttributes.addAttribute("rid", no);
        return "redirect:/recipe/detail";
    }
}
