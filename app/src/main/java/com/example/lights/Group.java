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


}
