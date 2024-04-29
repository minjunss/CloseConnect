package com.CloseConnect.closeconnect;

import com.CloseConnect.closeconnect.WithMockCustomUser;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.security.oatuh2.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.Map;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    Member member = Member.builder()
            .name("이름")
            .email("test@test.com")
            .oauth2Id("abcd1234")
            .authProvider(AuthProvider.GOOGLE)
            .role(Role.USER)
            .activityYn("Y")
            .build();

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "abcd1234");
        attributes.put("email", "test@test.com");
        attributes.put("name", "이름");
        UserPrincipal principalDetails = UserPrincipal.create(member, attributes);

        Authentication auth = new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
