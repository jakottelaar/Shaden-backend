package com.example.shaden.exception;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Set the response status to UNAUTHORIZED
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create an ErrorMessage instance
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorMessage.setMessage("You are not authorized to access this resource");
        errorMessage.setTimestamp(new Date());
        errorMessage.setErrors(Collections.singletonList(authException.getMessage()));

        // Write the error response as JSON to the response body
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), errorMessage);
    }
}
