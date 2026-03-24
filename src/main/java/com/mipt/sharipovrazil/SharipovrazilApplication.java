package com.mipt.sharipovrazil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SharipovrazilApplication {
    /**
     * Точка входа в приложение.
     *
     * <p>Запускает Spring Boot приложение, создавая и конфигурируя
     * контекст Spring, встроенный веб-сервер и все бины.</p>
     *
     * @param args аргументы командной строки, переданные при запуске
     */
    public static void main(String[] args) {
        SpringApplication.run(SharipovrazilApplication.class, args);
    }
}