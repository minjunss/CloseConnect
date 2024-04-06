package com.CloseConnect.closeconnect.entity.token;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Token {
    @Id
    private String id;
    private String email;
    private String token;
    private Date expirationTime;
    private boolean isBlacklisted;

    public void registerBlacklist() {
        this.isBlacklisted = true;
    }

    @Builder
    public Token(String email, String token, Date expirationTime) {
        this.email = email;
        this.token = token;
        this.expirationTime = expirationTime;
    }
}
