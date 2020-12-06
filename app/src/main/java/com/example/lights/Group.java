package com.example.lights;

import java.util.List;

public class Group {

    private String name;
    private int id;
    private int userId;
    private List<Light> lights;

    public Group(String name, int id, int userId, List<Light> lights) {
        this.name = name;
        this.id = id;
        this.userId = userId;
        this.lights = lights;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }
}