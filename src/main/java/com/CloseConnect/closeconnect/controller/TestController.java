package com.CloseConnect.closeconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test1")
    public String test() {
        System.out.println("1111212122212");
        return "111";
    }

}
