package com.CloseConnect.closeconnect.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class ViewController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @GetMapping("/loginRedirect")
    public String loginRedirect() {
        return "loginRedirect";
    }

    @GetMapping("/chatRoomList")
    public String chatRoomList() {
        return "chatRoomList";
    }

    @GetMapping("/myChatRoomList")
    public String myChatRoomList() {
        return "myChatRoomList";
    }

    @GetMapping("/chatRoom")
    public String chatRoom() {
        return "chatroom";
    }
}
