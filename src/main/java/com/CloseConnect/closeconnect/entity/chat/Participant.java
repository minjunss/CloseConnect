package com.CloseConnect.closeconnect.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Participant {
    private String email;
    private String userName;
}
