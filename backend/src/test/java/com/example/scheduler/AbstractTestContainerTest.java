package com.example.scheduler;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Для использования в тестах приложений, запущенных в контейнере, унаследуйтесь от данного класса
 * <p>
 * Обновление от 29.06.2025: Указал нужную версию PostgreSQL в application-test.yaml, тесты с доступом
 * к БД должны работать и без использования этого класса.
 */
public abstract class AbstractTestContainerTest {

    public static final PostgreSQLContainer postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.5"));

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void getProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", AbstractTestContainerTest::getPostgresUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    static String getPostgresUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s",
                postgres.getHost(),
                postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                postgres.getDatabaseName()
        );
    }
}
