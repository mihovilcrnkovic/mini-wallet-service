package com.example.demo.container;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedPostgresContainer {

    private static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres"));

    static {
        postgres.start();
    }

    public static PostgreSQLContainer getInstance() {
        return postgres;
    }
}
