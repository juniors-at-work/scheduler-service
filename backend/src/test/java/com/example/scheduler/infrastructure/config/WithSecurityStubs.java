package com.example.scheduler.infrastructure.config;

import com.example.scheduler.infrastructure.security.JwtTokenProvider;
import com.example.scheduler.infrastructure.stub.UserDetailsServiceStub;
import com.example.scheduler.infrastructure.util.JwtUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SecurityConfig.class, JwtTokenProvider.class, JwtUtil.class, UserDetailsServiceStub.class,
        TestClockConfig.class})
public @interface WithSecurityStubs {

}
