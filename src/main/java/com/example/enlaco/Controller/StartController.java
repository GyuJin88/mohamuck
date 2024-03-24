package com.example.enlaco.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartController {
    @GetMapping({"/start","/"})
    public String start() throws Exception {
        return "/gyuindex";
    }




}
