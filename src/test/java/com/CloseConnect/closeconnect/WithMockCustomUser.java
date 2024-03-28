package com.CloseConnect.closeconnect;

import com.CloseConnect.WithMockCustomUserSecurityContextFactory;
import com.CloseConnect.closeconnect.entity.member.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser  {

}
