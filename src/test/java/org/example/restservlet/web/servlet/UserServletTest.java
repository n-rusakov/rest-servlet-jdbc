package org.example.restservlet.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restservlet.AbstractTest;
import org.example.restservlet.db.HikariDataSourceProvider;
import org.example.restservlet.web.dto.UserGamesResponse;
import org.example.restservlet.web.dto.UserUpsertRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testcontainers.ext.ScriptUtils;

import java.io.*;
import java.util.List;

class UserServletTest extends AbstractTest {
    private UserServlet userServlet;

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws ServletException {
        ScriptUtils.runInitScript(jdbcDelegate, "db/drop_create_schema.sql");
        ScriptUtils.runInitScript(jdbcDelegate, "db/fill_database.sql");

        userServlet = new UserServlet();
        userServlet.init(null);
    }

    @Test
    void whenDoGetFindAll_thenReturnCorrectData() throws IOException, ServletException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        userServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        ObjectReader objectReader = objectMapper.readerForListOf(UserGamesResponse.class);
        List<UserGamesResponse> userGamesResponseList = objectReader.readValue(result);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(4, userGamesResponseList.size());

        UserGamesResponse userGamesResponse = userGamesResponseList.get(1);

        Assertions.assertEquals(2, userGamesResponse.getId());
        Assertions.assertEquals("Petr", userGamesResponse.getName());
        Assertions.assertEquals("petr@yandex.ru", userGamesResponse.getEmail());

    }

    @Test
    void whenDoGetById_thenReturnCorrectData() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        userServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        UserGamesResponse userGamesResponse = objectMapper.readValue(result,
                UserGamesResponse.class);

        Assertions.assertEquals(1, userGamesResponse.getId());
        Assertions.assertEquals("Ivan", userGamesResponse.getName());
        Assertions.assertEquals("ivan@mail.ru", userGamesResponse.getEmail());
    }

    @Test
    void whenDoInsert_thenCreatingNewUser() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        UserUpsertRequest upsertRequest = new UserUpsertRequest("TEST_USER", "TEST_MAIL");
        StringReader stringReader = new StringReader(
                objectMapper.writeValueAsString(upsertRequest));
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        userServlet.doPost(request, response);
        String result = sw.getBuffer().toString();

        UserGamesResponse userGamesResponse = objectMapper.readValue(result,
                UserGamesResponse.class);

        Assertions.assertEquals(HttpStatus.SC_CREATED, statusCapture.getValue());
        Assertions.assertEquals(5, userGamesResponse.getId());
        Assertions.assertEquals("TEST_USER", userGamesResponse.getName());
        Assertions.assertEquals("TEST_MAIL", userGamesResponse.getEmail());

    }

    @Test
    void whenDoUpdate_thenChangeData() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/2");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        UserUpsertRequest upsertRequest = new UserUpsertRequest("TEST_NAME", "TEST_MAIL");
        StringReader stringReader = new StringReader(
                objectMapper.writeValueAsString(upsertRequest));
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));

        userServlet.doPut(request, response);
        String result = sw.getBuffer().toString();

        UserGamesResponse userGamesResponse = objectMapper.readValue(result,
                UserGamesResponse.class);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(2, userGamesResponse.getId());
        Assertions.assertEquals("TEST_NAME", userGamesResponse.getName());
        Assertions.assertEquals("TEST_MAIL", userGamesResponse.getEmail());
    }

    @Test
    void whenDelete() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/4");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        userServlet.doDelete(request, response);

        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, statusCapture.getValue());
    }

    @Test
    void whenAddSubscription() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/4/2");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        userServlet.doPut(request, response);
        String result = sw.getBuffer().toString();

        UserGamesResponse userGamesResponse= objectMapper.readValue(result,
                UserGamesResponse.class);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(1, userGamesResponse.getGames().size());
        Assertions.assertEquals(2, userGamesResponse.getGames().get(0).getId());
        Assertions.assertEquals("Tetris", userGamesResponse.getGames().get(0).getTitle());
    }

    @Test
    void whenDeleteSubscription() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/1/2");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        userServlet.doDelete(request, response);
        String result = sw.getBuffer().toString();

        UserGamesResponse userGamesResponse= objectMapper.readValue(result,
                UserGamesResponse.class);

        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, statusCapture.getValue());
        Assertions.assertEquals(1, userGamesResponse.getGames().size());
        Assertions.assertEquals(4, userGamesResponse.getGames().get(0).getId());
        Assertions.assertEquals("Sonic", userGamesResponse.getGames().get(0).getTitle());
    }


}
