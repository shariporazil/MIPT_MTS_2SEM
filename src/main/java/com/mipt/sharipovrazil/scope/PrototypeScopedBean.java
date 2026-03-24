package com.mipt.sharipovrazil.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeScopedBean {
    private final UUID instanceId;

    public PrototypeScopedBean() {
        this.instanceId = UUID.randomUUID();
    }

    public Long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    public UUID getInstanceId() {
        return instanceId;
    }
}