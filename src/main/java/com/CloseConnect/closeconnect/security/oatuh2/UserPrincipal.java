package com.CloseConnect.closeconnect.security.oatuh2;

import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id; // 사용자 식별자
    private String email; // 사용자 이메일
    private Collection<? extends GrantedAuthority> authorities; // 사용자 권한
    @Setter
    private Map<String, Object> attributes; // OAuth2 사용자의 추가 속성

    public UserPrincipal(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    // Member 엔티티와 OAuth2 사용자 속성을 사용하여 UserPrincipal 객체를 생성하는 메서드
    public static UserPrincipal create(Member member, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name()));
        UserPrincipal userPrincipal = new UserPrincipal(member.getId(), member.getEmail(), authorities);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }

    // UserDetails 인터페이스 메서드 구현

    @Override
    public String getPassword() {
        return null; // 비밀번호 필드는 여기서 사용하지 않음
    }

    @Override
    public String getUsername() {
        return email; // 사용자 이름은 이메일 주소로 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호) 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }

    // OAuth2User 인터페이스 메서드 구현

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // OAuth2 사용자의 추가 속성 반환
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // 사용자 권한 반환
    }

    @Override
    public String getName() {
        return String.valueOf(id); // 사용자 식별자 반환
    }
}
