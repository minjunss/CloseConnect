package com.CloseConnect.closeconnect.controller.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post API", description = "글 관련 API")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "글 등록 API")
    @ApiResponse(
            responseCode = "200",
            description = "글 등록 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.Response.class))
    )
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
                                        @RequestBody PostDto.Request request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.save(request, userDetails.getUsername()));
    }
    @PatchMapping("/update/{id}")
    @Operation(summary = "글 수정 API", description = "글 내용 수정")
    @ApiResponse(
            responseCode = "200",
            description = "글 내용 수정 성공"
    )
    public ResponseEntity<?> updatePost(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @RequestBody PostDto.Request request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        postService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/delete/{id}")
    @Operation(summary = "글 삭제 API", description = "글 삭제")
    @ApiResponse(
            responseCode = "200",
            description = "글 삭제 성공"
    )
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "글 조회 API", description = "id로 글 조회")
    @ApiResponse(
            responseCode = "200",
            description = "글 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.Response.class))
    )
    public ResponseEntity<?> readPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping
    @Operation(summary = "글 리스트 조회 API", description = "제목, 내용, 작성자로 글 리스트 조회")
    @ApiResponse(
            responseCode = "200",
            description = "글 리스트 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)
            )
    )
    public Page<?> readPostList(@RequestBody PostSearchCondition postSearchCondition, Pageable pageable) {
        return postService.getPostList(postSearchCondition, pageable);
    }
}
