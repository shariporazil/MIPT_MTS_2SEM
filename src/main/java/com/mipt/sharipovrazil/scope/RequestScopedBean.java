package com.mipt.sharipovrazil.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Бин с областью видимости "request".
 *
 * <p>Демонстрирует использование scope {@link WebApplicationContext#SCOPE_REQUEST}.
 * Для каждого HTTP-запроса создается новый экземпляр этого бина,
 * который живет только в течение обработки запроса.</p>
 *
 * <p>Бин хранит уникальный идентификатор запроса и время его начала,
 * что позволяет отслеживать информацию о каждом запросе.</p>
 */

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
    private final String requestId;
    private final LocalDateTime startTime;
    private final String formattedStartTime;

    public RequestScopedBean() {
        this.requestId = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public long getProcessingTime() {
        return java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
    }

    @Override
    public String toString() {
        return "RequestScopedBean{" +
                "requestId='" + requestId + '\'' +
                ", startTime=" + formattedStartTime +
                '}';
    }
}