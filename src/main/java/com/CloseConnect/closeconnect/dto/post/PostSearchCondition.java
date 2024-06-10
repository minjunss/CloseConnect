package com.CloseConnect.closeconnect.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchCondition {
    private String title;
    private String content;
    private String author;
}
