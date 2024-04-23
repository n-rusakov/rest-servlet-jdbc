package org.example.restservlet.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import org.example.restservlet.AbstractTest;
import org.example.restservlet.web.dto.ErrorResponse;
import org.example.restservlet.web.dto.PublisherResponse;
import org.example.restservlet.web.dto.PublisherUpsertRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testcontainers.ext.ScriptUtils;


import java.io.*;
import java.util.List;

class PublisherServletTest extends AbstractTest {

    private PublisherServlet publisherServlet;

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws ServletException {
        ScriptUtils.runInitScript(jdbcDelegate, "db/drop_create_schema.sql");
        ScriptUtils.runInitScript(jdbcDelegate, "db/fill_database.sql");

        publisherServlet = new PublisherServlet();
        publisherServlet.init(null);
    }

    @Test
    void whenDoGetFindAll_thenReturnCorrectData() throws IOException  {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        publisherServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        ObjectReader objectReader = objectMapper.readerForListOf(PublisherResponse.class);
        List<PublisherResponse> publisherResponseList = objectReader.readValue(result);

        Assertions.assertEquals(4, publisherResponseList.size());

        PublisherResponse publisherResponse = publisherResponseList.get(1);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(2, publisherResponse.getId());
        Assertions.assertEquals("NINTENDO", publisherResponse.getName());

    }

    @Test
    void whenDoGetById_thenReturnCorrectData() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        publisherServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        PublisherResponse publisherResponse = objectMapper.readValue(result,
                PublisherResponse.class);

        Assertions.assertEquals(1,publisherResponse.getId());
        Assertions.assertEquals("EA Sports", publisherResponse.getName());
    }

    @Test
    void whenDoGet_givenIncorrectId_thenErrorResponse() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/5");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        publisherServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        ErrorResponse errorResponse = objectMapper.readValue(result,
                ErrorResponse.class);

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCapture.getValue());
        Assertions.assertEquals(ErrorResponse.class, errorResponse.getClass());
    }

    @Test
    void whenDoInsert_thenCreatingNewPublisher() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        PublisherUpsertRequest upsertRequest = new PublisherUpsertRequest("TESTNAME");
        StringReader stringReader = new StringReader(
                objectMapper.writeValueAsString(upsertRequest));
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        publisherServlet.doPost(request, response);
        String result = sw.getBuffer().toString();

        PublisherResponse publisherResponse = objectMapper.readValue(result,
                PublisherResponse.class);

        Assertions.assertEquals(HttpStatus.SC_CREATED, statusCapture.getValue());
        Assertions.assertEquals(5, publisherResponse.getId());
        Assertions.assertEquals("TESTNAME", publisherResponse.getName());

    }

    @Test
    void whenDoUpdate_thenChangeData() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/2");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        PublisherUpsertRequest upsertRequest = new PublisherUpsertRequest("TEST_UPDATE");
        StringReader stringReader = new StringReader(
                objectMapper.writeValueAsString(upsertRequest));
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));

        publisherServlet.doPut(request, response);
        String result = sw.getBuffer().toString();

        PublisherResponse publisherResponse = objectMapper.readValue(result,
                PublisherResponse.class);

        Assertions.assertEquals(HttpStatus.SC_OK, statusCapture.getValue());
        Assertions.assertEquals(2, publisherResponse.getId());
        Assertions.assertEquals("TEST_UPDATE", publisherResponse.getName());
    }

    @Test
    void whenDelete() throws IOException {
        StringWriter sw = new StringWriter();
        Mockito.when(request.getPathInfo()).thenReturn("/4");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(sw));

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(response).setStatus(statusCapture.capture());

        publisherServlet.doDelete(request, response);

        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, statusCapture.getValue());
    }


}
