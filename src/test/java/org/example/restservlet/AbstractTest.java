package org.example.restservlet;


import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.restservlet.db.HikariDataSourceProvider;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractTest {

    @Container
    protected static final PostgreSQLContainer postgreSQLContainer;

    protected static final JdbcDatabaseDelegate jdbcDelegate;

    protected static final HikariDataSource dataSource;

    private static MockedStatic<HikariDataSourceProvider> hikariDataSourceProviderMockedStatic;

    private static final int CONTAINER_PORT = 5432;
    private static final int LOCAL_PORT = 5432;

    private static final String DB_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String DB_DATABASE = "games";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final String DB_JDBC_URL = "jdbc:postgresql://localhost:" + LOCAL_PORT +
            "/" + DB_DATABASE;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:14");

        postgreSQLContainer = new PostgreSQLContainer<>(postgres)
                .withDatabaseName(DB_DATABASE)
                .withUsername(DB_USERNAME)
                .withPassword(DB_PASSWORD)
                .withExposedPorts(LOCAL_PORT)
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(LOCAL_PORT),
                                    new ExposedPort(CONTAINER_PORT)))))
                .withReuse(true);


        postgreSQLContainer.start();

        jdbcDelegate = new JdbcDatabaseDelegate(postgreSQLContainer, "");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(DB_DRIVER_CLASS_NAME);
        hikariConfig.setJdbcUrl(DB_JDBC_URL);
        hikariConfig.setUsername(DB_USERNAME);
        hikariConfig.setPassword(DB_PASSWORD);
        dataSource = new HikariDataSource(hikariConfig);
    }

    @BeforeAll
    public static void beforeAll() {
        hikariDataSourceProviderMockedStatic =
                Mockito.mockStatic(HikariDataSourceProvider.class);
        hikariDataSourceProviderMockedStatic
                .when(HikariDataSourceProvider::getDataSource).thenReturn(dataSource);
    }

    @AfterAll
    public static void afterAll() {
        hikariDataSourceProviderMockedStatic.close();
    }

}
