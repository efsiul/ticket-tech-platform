package com.tickettech.administrationservice.util;

import org.springframework.context.ApplicationEvent;

public class EntityErrorEvent extends ApplicationEvent {

    private final String function;
    private final String message;
    private final String idUser;
    private final Boolean isError;

    public EntityErrorEvent( String function, String message, String idUser,Boolean isError) {
        super(function);
        this.function = function;
        this.message = message;
        this.isError = isError;
        this.idUser = idUser;
    }


    public String getIdUser() {
        return idUser;
    }
    public String getFunction() {
        return function;
    }
    public String getMessage() {
        return message;
    }
    public Boolean getIsError() {
        return isError;
    }
}