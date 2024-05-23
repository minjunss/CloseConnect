package com.CloseConnect.closeconnect.controller.comment;

import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.service.comment.CommentService;
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
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "Comment API", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    @Operation(summary = "댓글 등록 API")
    @ApiResponse(
            responseCode = "200",
            description = "글 등록 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.Response.class))
    )
    public ResponseEntity<?> createComment(@RequestHeader("Authorization") String token,
                                           @RequestBody CommentDto.Request request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.save(request, userDetails.getUsername()));
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "댓글 수정 API", description = "댓글 수정")
    @ApiResponse(
            responseCode = "200",
            description = "댓글 수정 성공"
    )
    public ResponseEntity<?> updatePost(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @RequestBody CommentDto.Request request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        commentService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/delete/{id}")
    @Operation(summary = "댓글 삭제 API", description = "댓글 삭제")
    @ApiResponse(
            responseCode = "200",
            description = "댓글 삭제 성공"
    )
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listByPost/{postId}")
    @Operation(summary = "글에 대한 댓글 리스트 조회 API", description = "글id로 리스트 조회")
    @ApiResponse(
            responseCode = "200",
            description = "글에 대한 댓글 리스트 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)
            )
    )
    public Page<CommentDto.Response> readCommentsByPostId(@RequestHeader("Authorization") String token,
                                                          @PathVariable Long postId,
                                                          @AuthenticationPrincipal UserDetails userDetails,
                                                          Pageable pageable) {
        return commentService.getCommentsByPostId(postId, pageable);
    }

    @GetMapping("/myList")
    @Operation(summary = "내 댓글 리스트 조회 API", description = "내 댓글 리스트 조회")
    @ApiResponse(
            responseCode = "200",
            description = "내 댓글 리스트 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)
            )
    )
    public Page<CommentDto.Response> readCommentsByEmail(@RequestHeader("Authorization") String token,
                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                         Pageable pageable) {
        return commentService.getCommentsByEmail(userDetails.getUsername(), pageable);
    }
}
