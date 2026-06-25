package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервис для сбора и сравнения статистики из разных репозиториев.
 *
 * <p>Демонстрирует использование аннотаций {@link Primary} и {@link Qualifier}
 * для внедрения конкретных реализаций бинов. Сервис одновременно внедряет
 * основной репозиторий (помеченный {@link Primary}) и заглушечный репозиторий
 * (созданный через {@link org.springframework.context.annotation.Bean} с именем).</p>
 */

@Service
public class TaskStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(TaskStatisticsService.class);

    private final TaskRepository primaryRepository;
    private final TaskRepository stubRepository;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Autowired
    public TaskStatisticsService(
            TaskRepository primaryRepository,
            @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.primaryRepository = primaryRepository;
        this.stubRepository = stubRepository;
    }

    public void compareRepositories() {
        logger.info("Основной репозиторий (@Primary) содержит {} задач",
                primaryRepository.findAll().size());
        logger.info("Stub репозиторий (@Qualifier) содержит {} задач",
                stubRepository.findAll().size());
        logger.info("Задачи из основного репозитория: {}", primaryRepository.findAll());
        logger.info("Задачи из stub репозитория: {}", stubRepository.findAll());
    }
}