package org.example.restservlet.repository.impl;

import org.example.restservlet.entity.Game;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.entity.User;
import org.example.restservlet.exception.SqlPerformingException;
import org.example.restservlet.repository.GameRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameRepositoryImpl implements GameRepository {

    private final DataSource dataSource;

    private static final String QUERY_SELECT_ALL = """
            SELECT g.id as id, g.title as title, g.publisher_id as pid,
                p.company_name as company
            FROM games_schema.games as g
            LEFT JOIN games_schema.publishers as p ON g.publisher_id = p.id
            """;

    private static final String QUERY_SELECT_BY_ID = """
            SELECT g.id as id, g.title as title, g.publisher_id as pid,
                p.company_name as company
            FROM games_schema.games as g
            LEFT JOIN games_schema.publishers as p ON g.publisher_id = p.id
            WHERE g.id = ?
            """;

    private static final String QUERY_INSERT = """
            INSERT INTO games_schema.games (title, publisher_id)
            VALUES (?, ?)
            """;

    private static final String QUERY_UPDATE = """
            UPDATE games_schema.games
            SET title = ?, publisher_id = ?
            WHERE id = ?
            """;

    private static final String QUERY_DELETE = """
            DELETE FROM games_schema.games
            WHERE id = ?
            """;

    private static final String QUERY_FIND_USERS_BY_GAME_ID = """
            SELECT u.id as id, u.nickname as nickname, u.email as email
            FROM games_schema.users as u
            LEFT JOIN games_schema.subscriptions as s ON u.id = s.user_id
            WHERE s.game_id = ?
            """;

    private static final String SQL_ERROR = "Error when performing SQL";

    public GameRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Game getGameFromResultSet(ResultSet resultSet) throws SQLException {
        Game game = new Game();
        game.setId(resultSet.getLong("id"));
        game.setTitle(resultSet.getString("title"));

        Publisher publisher = new Publisher(resultSet.getLong("pid"),
                resultSet.getString("company"));
        game.setPublisher(publisher);

        game.setUsers(findUsersByGameId(game.getId()));

        return game;
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        return new User(resultSet.getLong("id"),
                resultSet.getString("nickname"),
                resultSet.getString("email"));
    }

    private List<User> findUsersByGameId(Long gameId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_FIND_USERS_BY_GAME_ID)) {

            statement.setLong(1, gameId);
            ResultSet resultSet = statement.executeQuery();

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet));
            }

            return users;

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public List<Game> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_ALL)) {

            ResultSet resultSet = statement.executeQuery();

            List<Game> gameList = new ArrayList<>();
            while (resultSet.next()) {
                gameList.add(getGameFromResultSet(resultSet));
            }

            return gameList;
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Optional<Game> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getGameFromResultSet(resultSet));
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Game insert(Game game) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, game.getTitle());
            statement.setLong(2, game.getPublisher().getId());
            statement.executeUpdate();

            ResultSet generatedKeysSet = statement.getGeneratedKeys();
            generatedKeysSet.next();

            return new Game(generatedKeysSet.getLong("id"),
                    game.getTitle(),
                    game.getPublisher());

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Game update(Game game) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE)) {

            statement.setString(1, game.getTitle());
            statement.setLong(2, game.getPublisher().getId());
            statement.setLong(3, game.getId());

            statement.executeUpdate();

            return new Game(game.getId(), game.getTitle(), game.getPublisher());

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_DELETE)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }
}
