package com.tickettech.administrationservice.util;

import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

public class EntityChangeEvent extends ApplicationEvent {

    private final Object entity;
    private final List<Map<String, Object>> changes;
    private final Long idUser;

    public EntityChangeEvent(Object entity, List<Map<String, Object>> changes, Long idUser) {
        super(entity);
        this.entity = entity;
        this.changes = changes;
        this.idUser = idUser;
    }

    public Object getEntity() {
        return entity;
    }

    public List<Map<String, Object>> getChanges() {
        return changes;
    }
    public Long getIdUser() {
        return idUser;
    }
}