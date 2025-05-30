package io.hanbings.nikukyu.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long duration = System.currentTimeMillis() - startTime;

        String method = wrappedRequest.getMethod();
        String requestURI = wrappedRequest.getRequestURI();
        String queryString = wrappedRequest.getQueryString();
        String requestURL = queryString == null ? requestURI : requestURI + "?" + queryString;

        String requestHeaders = Collections.list(wrappedRequest.getHeaderNames()).stream()
                .map(name -> name + ": " + Collections.list(wrappedRequest.getHeaders(name)))
                .collect(Collectors.joining(", "));

        String requestBody;
        try {
            byte[] requestContent = wrappedRequest.getContentAsByteArray();
            if (requestContent.length > 0) {
                wrappedRequest.getCharacterEncoding();
                requestBody = new String(requestContent, wrappedRequest.getCharacterEncoding());
            } else {
                requestBody = "";
            }
        } catch (Exception e) {
            requestBody = "无法解析请求体";
        }

        int status = wrappedResponse.getStatus();

        String responseHeaders = wrappedResponse.getHeaderNames().stream()
                .map(name -> name + ": " + wrappedResponse.getHeaders(name))
                .collect(Collectors.joining(", "));

        String responseBody;
        try {
            byte[] responseContent = wrappedResponse.getContentAsByteArray();
            responseBody = responseContent.length > 0 ?
                    new String(responseContent, wrappedResponse.getCharacterEncoding() != null
                            ? wrappedResponse.getCharacterEncoding()
                            : StandardCharsets.UTF_8.name()) : "";
        } catch (Exception e) {
            responseBody = "无法解析响应体";
        }

        log.info("HTTP {} {} - {} ms", method, requestURL, duration);
        log.info("Request Headers: {}", requestHeaders);
        log.info("Request Body: {}", requestBody);
        log.info("Response Status: {}", status);
        log.info("Response Headers: {}", responseHeaders);
        log.info("Response Body: {}", responseBody);

        wrappedResponse.copyBodyToResponse();
    }
}
