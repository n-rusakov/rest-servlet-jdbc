package org.example.restservlet.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restservlet.AbstractTest;
import org.example.restservlet.db.HikariDataSourceProvider;
import org.example.restservlet.web.dto.GameUpsertRequest;
import org.example.restservlet.web.dto.GameUsersResponse;
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

class GameServletTest extends AbstractTest {

    private GameServlet gameServlet;

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws ServletException {
        ScriptUtils.runInitScript(jdbcDelegate, "db/drop_create_schema.sql");
        ScriptUtils.runInitScript(jdbcDelegate, "db/fill_database.sql");

        gameServlet = new GameServlet();
        gameServlet.init(null);
    }

    @Test
    void whenDoGetFindAll_thenReturnCorrectData() throws IOException, ServletException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        gameServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        ObjectReader objectReader = objectMapper.readerForListOf(GameUsersResponse.class);
        List<GameUsersResponse> gameUsersResponseList = objectReader.readValue(result);

        Assertions.assertEquals(4, gameUsersResponseList.size());

        GameUsersResponse gameUsersResponse = gameUsersResponseList.get(1);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(2, gameUsersResponse.getId());
        Assertions.assertEquals("Tetris", gameUsersResponse.getTitle());
        Assertions.assertEquals(2, gameUsersResponse.getPublisher().getId());

    }

    @Test
    void whenDoGetById_thenReturnCorrectData() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        gameServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        GameUsersResponse gameUsersResponse = objectMapper.readValue(result,
                GameUsersResponse.class);

        Assertions.assertEquals(1, gameUsersResponse.getId());
        Assertions.assertEquals("Contra", gameUsersResponse.getTitle());
        Assertions.assertEquals(1, gameUsersResponse.getPublisher().getId());
    }

    @Test
    void whenDoInsert_thenCreatingNewGame() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        GameUpsertRequest upsertRequest = new GameUpsertRequest("TEST_GAME", 1L);
        StringReader stringReader = new StringReader(
                objectMapper.writeValueAsString(upsertRequest));
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        gameServlet.doPost(request, response);
        String result = sw.getBuffer().toString();

        GameUsersResponse gameUsersResponse = objectMapper.readValue(result,
                GameUsersResponse.class);

        Assertions.assertEquals(HttpStatus.SC_CREATED, statusCapture.getValue());
        Assertions.assertEquals(5, gameUsersResponse.getId());
        Assertions.assertEquals("TEST_GAME", gameUsersResponse.getTitle());
        Assertions.assertEquals(1, gameUsersResponse.getPublisher().getId());

    }

    @Test
    void whenDoUpdate_thenChangeData() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/2");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        GameUpsertRequest upsertRequest = new GameUpsertRequest("TEST_UPDATE", 3L);
        StringReader stringReader = new StringReader(
                objectMapper.writeValueAsString(upsertRequest));
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));

        gameServlet.doPut(request, response);
        String result = sw.getBuffer().toString();

        GameUsersResponse gameUsersResponse = objectMapper.readValue(result,
                GameUsersResponse.class);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(2, gameUsersResponse.getId());
        Assertions.assertEquals("TEST_UPDATE", gameUsersResponse.getTitle());
        Assertions.assertEquals(3, gameUsersResponse.getPublisher().getId());
    }

    @Test
    void whenDelete() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/4");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        gameServlet.doDelete(request, response);

        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, statusCapture.getValue());
    }

}
