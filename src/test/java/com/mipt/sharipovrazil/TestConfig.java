package com.mipt.sharipovrazil;

import com.mipt.sharipovrazil.repository.StubTaskRepository;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean(name = "stubTaskRepository")
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }

}