package com.CloseConnect.closeconnect.controller.member;

import com.CloseConnect.closeconnect.dto.member.LocationDto;
import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@Tag(name = "Member API", description = "회원 관련 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/myInfo")
    @Operation(summary = "내 정보 조회 API", description = "토큰으로 내 정보 조회")
    @ApiResponse(
            responseCode = "200",
            description = "내 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberResponseDto.ResponseDto.class)
            )
    )
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String token,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memberService.getMyInfo(userDetails.getUsername()));
    }

    @PostMapping("/updateCoordinate")
    @Operation(summary = "좌표 업데이트 API", description = "사용자 위치 좌표 업데이트")
    @ApiResponse(
            responseCode = "200",
            description = "좌표 업데이트 성공"
    )
    public ResponseEntity<?> updateCoordinate(@RequestHeader("Authorization") String token,
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody LocationDto locationDto) {
        memberService.updateCoordinate(userDetails.getUsername(), locationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    @Operation(summary = "내 근처 회원 조회 API", description = "사용자 근처 ?km 회원들 조회")
    @ApiResponse(
            responseCode = "200",
            description = "근처 회원들 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)
            )
    )
    public ResponseEntity<?> getNearbyMembers(@RequestHeader("Authorization") String token,
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              LocationDto locationDto,
                                              Pageable pageable) {
        Page<MemberResponseDto.ResponseDto> nearbyMemberList = memberService.getNearbyMemberList(userDetails.getUsername(), locationDto, pageable);

        return ResponseEntity.ok(nearbyMemberList);
    }
}
