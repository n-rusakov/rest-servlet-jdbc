package org.example.restservlet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restservlet.web.dto.ErrorResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class ServletUtils {

    private ServletUtils() {throw new IllegalArgumentException();}

    public static String getRequestBody(HttpServletRequest request) {
        try (BufferedReader reader =  request.getReader()){
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            return null;
        }
    }

    public static String jsonErrorMessage(ObjectMapper objectMapper, String message) {
        try {
            return objectMapper.writeValueAsString(new ErrorResponse(message));
        } catch (Exception e) {
            return message;
        }
    }

    public static void writeToResponse(HttpServletResponse response, String data) {
        try {
            response.getWriter().write(data);
        } catch (Exception e) {
            //
        }
    }
}
