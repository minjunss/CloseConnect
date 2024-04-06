package com.CloseConnect.closeconnect.entity.member;

import com.CloseConnect.closeconnect.security.oatuh2.OAuth2UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String oauth2Id;
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isLoggedIn;
    private Double latitude;
    private Double longitude;
    private String activityYn;

    @Builder
    public Member(String name, String email, String oauth2Id, AuthProvider authProvider, Role role, String activityYn) {
        this.name = name;
        this.email = email;
        this.oauth2Id = oauth2Id;
        this.authProvider = authProvider;
        this.role = role;
        this.activityYn = activityYn;
    }

    public void update(OAuth2UserInfo userInfo) {
        this.name = userInfo.getName();
        this.oauth2Id = userInfo.getOAuth2Id();
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    public void login() {
        this.isLoggedIn = true;
    }

    public void updateCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
