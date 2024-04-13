package com.CloseConnect.closeconnect.controller.member;

import com.CloseConnect.closeconnect.dto.member.LocationDto;
import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/myInfo")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memberService.getMyInfo(userDetails.getUsername()));
    }

    @PostMapping("/updateCoordinate")
    public ResponseEntity<?> updateCoordinate(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody LocationDto locationDto) {
        memberService.updateCoordinate(userDetails.getUsername(), locationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyMembers(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody LocationDto locationDto,
                                              Pageable pageable) {
        Page<MemberResponseDto.ResponseDto> nearbyMemberList = memberService.getNearbyMemberList(userDetails.getUsername(), locationDto, pageable);

        return ResponseEntity.ok(nearbyMemberList);
    }
}
