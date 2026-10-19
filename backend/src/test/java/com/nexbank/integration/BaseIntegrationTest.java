package com.nexbank.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({PostgresTestContainerConfig.class, RabbitMqTestContainerConfig.class, TestJwtConfig.class})
public abstract class BaseIntegrationTest {}
