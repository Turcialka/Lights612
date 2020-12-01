package com.example.lights;

public class Light {

    private int id;
    private String name;
    private String serial;

    public Light(int id, String name, String serial) {
        this.id = id;
        this.name = name;
        this.serial = serial;
    }

    public String getName() {
        return name;
    }

    public String getSerial() {
        return serial;
    }
}
