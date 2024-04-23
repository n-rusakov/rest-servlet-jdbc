package org.example.restservlet.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restservlet.db.HikariDataSourceProvider;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.mapper.PublisherMapper;
import org.example.restservlet.repository.PublisherRepository;
import org.example.restservlet.repository.impl.PublisherRepositoryImpl;
import org.example.restservlet.service.PublisherService;
import org.example.restservlet.service.impl.PublisherServiceImpl;
import org.example.restservlet.util.ServletUtils;
import org.example.restservlet.web.dto.PublisherResponse;
import org.example.restservlet.web.dto.PublisherUpsertRequest;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PublisherServlet", urlPatterns = {"/api/publisher/*"})
public class PublisherServlet extends HttpServlet {

    private transient PublisherService publisherService;

    private transient PublisherMapper publisherMapper;

    private transient ObjectMapper objectMapper;

    private static final String BAD_REQUEST_MESSAGE = "BAD REQUEST";

    private static final String JSON_CONTENT = "application/json; charset=UTF-8";

    @Override
    public void init(ServletConfig config) throws ServletException {

        DataSource ds = HikariDataSourceProvider.getDataSource();
        PublisherRepository publisherRepository = new PublisherRepositoryImpl(ds);

        publisherService = new PublisherServiceImpl(publisherRepository);
        publisherMapper = Mappers.getMapper(PublisherMapper.class);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String responseBody;

        try {

            if (pathInfo == null || "/".equals(pathInfo)) {
                List<PublisherResponse> responseList = publisherMapper
                        .toListResponse(publisherService.findAll());
                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(responseList);

            } else {
                String[] parts = pathInfo.split("/");
                Long id = Long.valueOf(parts[1]);
                Publisher publisher = publisherService.findById(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(
                        publisherMapper.toResponse(publisher));
            }
        } catch (EntityNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, BAD_REQUEST_MESSAGE);
        }

        resp.setContentType(JSON_CONTENT);
        ServletUtils.writeToResponse(resp, responseBody);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String responseBody;
        try {
            String bodyString = ServletUtils.getRequestBody(req);
            PublisherUpsertRequest upsertRequest =
                    objectMapper.readValue(bodyString, PublisherUpsertRequest.class);

            Publisher publisher = publisherService.insert(publisherMapper.toEntity(upsertRequest));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            responseBody = objectMapper.writeValueAsString(publisherMapper.toResponse(publisher));

        } catch (EntityNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, BAD_REQUEST_MESSAGE);
        }

        resp.setContentType(JSON_CONTENT);
        ServletUtils.writeToResponse(resp, responseBody);

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String responseBody;

        try {
            String pathInfo = req.getPathInfo();
            String[] parts = pathInfo.split("/");

            Long publisherId = Long.valueOf(parts[1]);
            String bodyString = ServletUtils.getRequestBody(req);

            PublisherUpsertRequest upsertRequest =
                    objectMapper.readValue(bodyString, PublisherUpsertRequest.class);

            Publisher publisher = publisherService.update(publisherId,
                    publisherMapper.toEntity(upsertRequest));

            resp.setStatus(HttpServletResponse.SC_OK);
            responseBody = objectMapper.writeValueAsString(publisherMapper.toResponse(publisher));

        } catch (EntityNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, BAD_REQUEST_MESSAGE);
        }

        resp.setContentType(JSON_CONTENT);
        ServletUtils.writeToResponse(resp, responseBody);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String responseBody;

        try {
            String pathInfo = req.getPathInfo();
            String[] parts = pathInfo.split("/");

            Long publisherId = Long.valueOf(parts[1]);

            publisherService.deleteById(publisherId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            responseBody = "";

        } catch (EntityNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody = ServletUtils.jsonErrorMessage(objectMapper, BAD_REQUEST_MESSAGE);
        }

        resp.setContentType(JSON_CONTENT);
        ServletUtils.writeToResponse(resp, responseBody);
    }
}
