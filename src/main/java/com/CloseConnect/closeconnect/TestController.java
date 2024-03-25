package com.CloseConnect.closeconnect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/test1")
    public ResponseEntity<?> test() {
        log.info("test");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
