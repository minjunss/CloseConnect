package com.CloseConnect.closeconnect.entity.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatRoomType {
    PUBLIC("PUBLIC", "오픈채팅방"),
    PRIVATE("PRIVATE", "1:1 채팅방");

    private final String key;
    private final String title;
}
