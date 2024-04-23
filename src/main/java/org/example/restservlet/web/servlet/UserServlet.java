package org.example.restservlet.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restservlet.db.HikariDataSourceProvider;
import org.example.restservlet.entity.User;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.mapper.UserMapper;
import org.example.restservlet.repository.UserRepository;
import org.example.restservlet.repository.impl.UserRepositoryImpl;
import org.example.restservlet.service.UserService;
import org.example.restservlet.service.impl.UserServiceImpl;
import org.example.restservlet.util.ServletUtils;
import org.example.restservlet.web.dto.*;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = {"/api/user/*"})
public class UserServlet extends HttpServlet {

    private transient UserService userService;

    private transient UserMapper userMapper;

    private transient ObjectMapper objectMapper;

    private static final String BAD_REQUEST_MESSAGE = "BAD REQUEST";

    private static final String JSON_CONTENT = "application/json; charset=UTF-8";

    @Override
    public void init(ServletConfig config) throws ServletException {

        DataSource ds = HikariDataSourceProvider.getDataSource();

        UserRepository userRepository = new UserRepositoryImpl(ds);

        userService = new UserServiceImpl(userRepository);
        userMapper = Mappers.getMapper(UserMapper.class);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String pathInfo = req.getPathInfo();
        String responseBody;

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                List<UserGamesResponse> responseList = userMapper
                        .toUserGamesRresponseList(userService.findAll());

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(responseList);
            } else {
                String[] parts = pathInfo.split("/");
                Long id = Long.valueOf(parts[1]);
                User user = userService.findById(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(
                        userMapper.toUserGamesResponse(user));
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
            UserUpsertRequest userUpsertRequest =
                    objectMapper.readValue(bodyString, UserUpsertRequest.class);

            User user = userService.insert(
                    userMapper.toEntity(userUpsertRequest));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            responseBody = objectMapper.writeValueAsString(userMapper.toUserResponse(user));

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
        String pathInfo = req.getPathInfo();

        try {
            String[] parts = pathInfo.split("/");
            Long userId = Long.valueOf(parts[1]);

            if (parts.length == 2) {
                String bodyString = ServletUtils.getRequestBody(req);

                UserUpsertRequest upsertRequest =
                        objectMapper.readValue(bodyString, UserUpsertRequest.class);
                User user = userService.update(userId,
                        userMapper.toEntity(upsertRequest));

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(userMapper.toUserGamesResponse(user));
            } else {
                Long gameId = Long.valueOf(parts[2]);

                User user = userService.addGame(userId, gameId);

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(userMapper.toUserGamesResponse(user));

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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String responseBody;
        String pathInfo = req.getPathInfo();

        try {
            String[] parts = pathInfo.split("/");
            Long userId = Long.valueOf(parts[1]);

            if (parts.length == 2) {
                userService.deleteById(userId);

                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                responseBody = "";
            } else {
                Long gameId = Long.valueOf(parts[2]);

                User user = userService.deleteGame(userId, gameId);

                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                responseBody = objectMapper.writeValueAsString(userMapper.toUserGamesResponse(user));
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


}
