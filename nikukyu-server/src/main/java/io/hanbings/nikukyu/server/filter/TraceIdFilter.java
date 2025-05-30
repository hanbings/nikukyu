package io.hanbings.nikukyu.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class TraceIdFilter extends OncePerRequestFilter {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String TRACE_ID = "X-NikuKyu-Trace-Id";

    @Override
    public void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);

        response.setHeader(TRACE_ID, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}