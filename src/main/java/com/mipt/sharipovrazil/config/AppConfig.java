package com.mipt.sharipovrazil.config;

import com.mipt.sharipovrazil.repository.StubTaskRepository;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
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

    @Bean(name = "stubTaskRepository")
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}