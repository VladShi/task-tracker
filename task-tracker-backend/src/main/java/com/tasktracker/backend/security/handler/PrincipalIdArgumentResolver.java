package com.tasktracker.backend.security.handler;

import com.tasktracker.backend.security.annotation.PrincipalId;
import com.tasktracker.backend.security.service.JwtService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class PrincipalIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PrincipalId.class) != null &&
                parameter.getParameterType().equals(Long.class) || parameter.getParameterType().equals(long.class);
    }

    @Override
    public Object resolveArgument(@Nonnull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @Nonnull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("JWT token is missing or invalid");
        }

        return jwtService.extractUserId(jwt);
    }
}
