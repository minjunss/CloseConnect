package com.CloseConnect.closeconnect.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Test API", description = "Test API 입니다.")
public class TestController {

    @GetMapping("/test")
    @Tag(name = "Test API")
    @Operation(summary = "테스트 API", description = "Swagger 테스트")
    public String test() {
        System.out.println("1111212122212");
        return "111";
    }

    @GetMapping("/test2")
    @Tag(name = "Test API")
    @Operation(summary = "테스트 API2", description = "Swagger 테스트2")
    public String test2() {
        System.out.println("1111212122212");
        return "111";
    }

}
