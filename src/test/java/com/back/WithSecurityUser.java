package com.back;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithSecurityUserFactory.class)
public @interface WithSecurityUser {
    long id() default 1L;
    String email() default "test@example.com";
    String password() default "password";
    String nickname() default "testUser";
    String[] roles() default { "USER" };
}