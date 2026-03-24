package com.mipt.sharipovrazil.config;

import com.mipt.sharipovrazil.repository.TaskRepository;
import com.mipt.sharipovrazil.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            logger.info("[BEFORE INIT] {} (bean name: {}) - подготовка к инициализации",
                    bean.getClass().getSimpleName(), beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            logger.info("[AFTER INIT] {} (bean name: {}) - инициализация завершена",
                    bean.getClass().getSimpleName(), beanName);
        }
        return bean;
    }
}