package com.mipt.sharipovrazil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для явного создания бинов.
 *
 * <p>Демонстрирует альтернативный способ создания бинов Spring
 * через метод, аннотированный {@link Bean}, в отличие от использования
 * стереотипных аннотаций ({@link org.springframework.stereotype.Component},
 * {@link org.springframework.stereotype.Service}, {@link org.springframework.stereotype.Repository}).</p>
 *
 * <p>В этом классе создается бин {@link StubTaskRepository} с именем "stubTaskRepository",
 * который будет использоваться вместе с {@link org.springframework.beans.factory.annotation.Qualifier}
 * для демонстрации внедрения конкретной реализации.</p>
 */
@Configuration
public class AppConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.api.version}")
    private String apiVersion;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.cors.allowed-origin}")
    private String corsAllowedOrigin;

    // Getters
    public String getAppName() { return appName; }
    public String getAppVersion() { return appVersion; }
    public String getApiVersion() { return apiVersion; }
    public String getUploadDir() { return uploadDir; }
    public String getCorsAllowedOrigin() { return corsAllowedOrigin; }
}