package com.CloseConnect.closeconnect.controller.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.security.oatuh2.UserPrincipal;
import com.CloseConnect.closeconnect.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post API", description = "글 관련 API")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "글 등록 API")
    public ResponseEntity<?> createPost(@RequestBody PostDto.Request request,
                                        @AuthenticationPrincipal UserDetails UserDetails) {
        return ResponseEntity.ok(postService.save(request, UserDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "글 조회 API", description = "id로 글 조회")
    public ResponseEntity<?> readPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }
}
