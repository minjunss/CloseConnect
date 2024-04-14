package com.CloseConnect.closeconnect.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    ALREADY_SIGNED_UP(404, "이미 가입된 이메일입니다.", null),
    NOT_EXIST_MEMBER(404, "존재하지 않는 회원입니다.", null),
    NOT_EXIST_CHATROOM(404, "존재하지 않는 채팅방입니다.", null),
    NOT_EXIST_POST(404, "존재하지 않는 글입니다.", null),
    OAUTH2_EMAIL_NOT_FOUND(404, "OAuth2 제공자에서 이메일을 찾을 수 없습니다.", null),
    ALREADY_JOINED_ROOM(409, "이미 참여중인 방입니다.", null);

    private final int status;
    private final String message;
    private final String field;

}
