package com.back;

import com.back.global.security.SecurityUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WithSecurityUserFactory implements WithSecurityContextFactory<WithSecurityUser> {

    @Override
    public SecurityContext createSecurityContext(WithSecurityUser annotation) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.roles())
                                                         .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                                         .collect(Collectors.toList());

        SecurityUser securityUser = new SecurityUser(
                annotation.id(),
                annotation.email(),
                annotation.password(),
                annotation.nickname(),
                authorities
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(securityUser, null, authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        return context;
    }
}
