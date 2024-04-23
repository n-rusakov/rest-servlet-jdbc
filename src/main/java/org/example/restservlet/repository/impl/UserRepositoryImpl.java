package org.example.restservlet.repository.impl;

import org.example.restservlet.entity.Game;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.entity.User;
import org.example.restservlet.exception.SqlPerformingException;
import org.example.restservlet.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;

    private static final String QUERY_SELECT_ALL = """
            SELECT id, nickname, email FROM games_schema.users
            """;

    private static final String QUERY_SELECT_BY_ID = """
            SELECT id, nickname, email FROM games_schema.users
            WHERE id = ?
            """;

    private static final String QUERY_INSERT = """
            INSERT INTO games_schema.users (nickname, email)
            VALUES (?, ?)
            """;

    private static final String QUERY_UPDATE = """
            UPDATE games_schema.users
            SET nickname = ?, email = ?
            WHERE id = ?
            """;

    private static final String QUERY_DELETE = """
            DELETE FROM games_schema.users
            WHERE id = ?
            """;

    private static final String QUERY_FIND_GAMES_BY_USER_ID = """
            SELECT g.id as id, g.title as title, g.publisher_id as publisher_id,
                p.company_name as publisher_name
            FROM games_schema.games as g
            LEFT JOIN games_schema.subscriptions as s ON g.id = s.game_id
            LEFT JOIN games_schema.publishers as p ON p.id = g.publisher_id
            WHERE s.user_id = ?
            """;

    private static final String QUERY_ADD_SUBSCRIPTION = """
            INSERT INTO games_schema.subscriptions (user_id, game_id)
            VALUES (?, ?)
            """;

    private static final String QUERY_DELETE_SUBSCRIPTION = """
            DELETE FROM games_schema.subscriptions
            WHERE user_id = ? AND game_id = ?
            """;

    private static final String SQL_ERROR = "Error when performing SQL";

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("nickname"));
        user.setEmail(resultSet.getString("email"));

        user.setGames(findGamesByUserId(user.getId()));

        return user;
    }

    private Game getGameFromResultSet(ResultSet resultSet) throws SQLException {
        return new Game(resultSet.getLong("id"),
                resultSet.getString("title"),
                new Publisher(resultSet.getLong("publisher_id"),
                        resultSet.getString("publisher_name")));
    }

    private List<Game> findGamesByUserId(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_FIND_GAMES_BY_USER_ID)) {

            statement.setLong(1, userId);

            List<Game> games = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                games.add(getGameFromResultSet(resultSet));
            }

            return games;
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_ALL)) {

            ResultSet resultSet = statement.executeQuery();

            List<User> userList = new ArrayList<>();
            while (resultSet.next()) {
                userList.add(getUserFromResultSet(resultSet));
            }

            return userList;

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_ID)){

            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getUserFromResultSet(resultSet));
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }

    }

    @Override
    public User insert(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_INSERT,
                Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.executeUpdate();

            ResultSet generatedKeysSet = statement.getGeneratedKeys();
            generatedKeysSet.next();

            return new User(generatedKeysSet.getLong("id"),
                    user.getName(), user.getEmail());

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public User update(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setLong(3, user.getId());

            statement.executeUpdate();

            return new User(user.getId(), user.getName(), user.getEmail());

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

    @Override
    public void addSubscription(Long userId, Long gameId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_ADD_SUBSCRIPTION)) {

            statement.setLong(1, userId);
            statement.setLong(2, gameId);

            statement.executeUpdate();
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public void deleteSubscription(Long userId, Long gameId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_DELETE_SUBSCRIPTION)) {

            statement.setLong(1, userId);
            statement.setLong(2, gameId);

            statement.executeUpdate();
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }
}
