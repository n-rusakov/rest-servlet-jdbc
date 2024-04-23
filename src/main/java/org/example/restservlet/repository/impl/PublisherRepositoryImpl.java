package org.example.restservlet.repository.impl;

import org.example.restservlet.entity.Publisher;
import org.example.restservlet.exception.SqlPerformingException;
import org.example.restservlet.repository.PublisherRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PublisherRepositoryImpl implements PublisherRepository {

    private final DataSource dataSource;

    private static final String QUERY_SELECT_ALL = """
            SELECT id, company_name FROM games_schema.publishers
            """;

    private static final String QUERY_SELECT_BY_ID = """
            SELECT id, company_name FROM games_schema.publishers
            WHERE id = ?
            """;

    private static final String QUERY_INSERT = """
            INSERT INTO games_schema.publishers (company_name)
            VALUES (?)
            """;

    private static final String QUERY_UPDATE = """
            UPDATE games_schema.publishers
            SET company_name = ?
            WHERE id = ?
            """;

    private static final String QUERY_DELETE = """
            DELETE FROM games_schema.publishers
            WHERE id = ?
            """;

    private static final String SQL_ERROR = "Error when performing SQL";

    public PublisherRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    Publisher getFromResultSet(ResultSet resultSet) throws SQLException {
        return new Publisher(resultSet.getLong("id"),
                resultSet.getString("company_name"));
    }


    @Override
    public List<Publisher> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_ALL)) {

            ResultSet resultSet = statement.executeQuery();

            List<Publisher> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(getFromResultSet(resultSet));
            }

            return resultList;
        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Optional<Publisher> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_ID)) {

            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getFromResultSet(resultSet));
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Publisher insert(Publisher publisher) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_INSERT,
                Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, publisher.getName());
            statement.executeUpdate();

            ResultSet generatedKeysSet = statement.getGeneratedKeys();
            generatedKeysSet.next();

            return new Publisher(generatedKeysSet.getLong("id"), publisher.getName());

        } catch (Exception e) {
            throw new SqlPerformingException(SQL_ERROR);
        }
    }

    @Override
    public Publisher update(Publisher publisher) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE)) {

            statement.setString(1, publisher.getName());
            statement.setLong(2, publisher.getId());

            statement.executeUpdate();

            return new Publisher(publisher.getId(), publisher.getName());

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
