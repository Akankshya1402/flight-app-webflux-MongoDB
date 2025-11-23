package com.flightapp.webfluxmongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "airline")
public class Airline {

    @Id
    private String id;

    private String name;
    private String logoUrl;
    private boolean active;

    public Airline() {
    }

    public Airline(String id, String name, String logoUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
