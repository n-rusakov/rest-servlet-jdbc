package org.example.restservlet.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restservlet.db.HikariDataSourceProvider;
import org.example.restservlet.entity.Game;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.mapper.GameMapper;
import org.example.restservlet.repository.GameRepository;
import org.example.restservlet.repository.PublisherRepository;
import org.example.restservlet.repository.impl.GameRepositoryImpl;
import org.example.restservlet.repository.impl.PublisherRepositoryImpl;
import org.example.restservlet.service.GameService;
import org.example.restservlet.service.PublisherService;
import org.example.restservlet.service.impl.GameServiceImpl;
import org.example.restservlet.service.impl.PublisherServiceImpl;
import org.example.restservlet.util.ServletUtils;
import org.example.restservlet.web.dto.*;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GameServlet", urlPatterns = {"/api/game/*"})
public class GameServlet extends HttpServlet {

    private transient GameService gameService;

    private transient PublisherService publisherService;

    private transient GameMapper gameMapper;

    private ObjectMapper objectMapper;

    private static final String BAD_REQUEST_MESSAGE = "BAD REQUEST";

    private static final String JSON_CONTENT = "application/json; charset=UTF-8";

    @Override
    public void init(ServletConfig config) throws ServletException {

        DataSource ds = HikariDataSourceProvider.getDataSource();
        PublisherRepository publisherRepository = new PublisherRepositoryImpl(ds);
        GameRepository gameRepository = new GameRepositoryImpl(ds);

        publisherService = new PublisherServiceImpl(publisherRepository);
        gameService = new GameServiceImpl(gameRepository);
        gameMapper = Mappers.getMapper(GameMapper.class);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String pathInfo = req.getPathInfo();
        String responseBody;

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                List<GameUsersResponse> responseList = gameMapper
                        .toGameUsersResponseList(gameService.findAll());

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(responseList);
            } else {
                String[] parts = pathInfo.split("/");
                Long id = Long.valueOf(parts[1]);
                Game game = gameService.findById(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                responseBody = objectMapper.writeValueAsString(
                        gameMapper.toGameUsersResponse(game));
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
            GameUpsertRequest gameUpsertRequest =
                    objectMapper.readValue(bodyString, GameUpsertRequest.class);

            Game game = gameService.insert(
                    gameMapper.toEntity(gameUpsertRequest, publisherService));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            responseBody = objectMapper.writeValueAsString(gameMapper.toGameResponse(game));

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

            Long gameId = Long.valueOf(parts[1]);
            String bodyString = ServletUtils.getRequestBody(req);

            GameUpsertRequest upsertRequest =
                    objectMapper.readValue(bodyString, GameUpsertRequest.class);

            Game game = gameService.update(gameId,
                    gameMapper.toEntity(upsertRequest, publisherService));

            resp.setStatus(HttpServletResponse.SC_OK);
            responseBody = objectMapper.writeValueAsString(gameMapper.toGameResponse(game));

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

            Long gameId = Long.valueOf(parts[1]);

            gameService.deleteById(gameId);

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
