package com.example.lights;

public class NetworkHandler{

    public NetworkHandler(){
    }

    public String makeUrl(String header, String... values) {
        String tempUrl = "";
        //kacper ip
        //String restIp = "http://192.168.0.116:8080";
        //magda ip
        String restIp = "http://192.168.8.117:8080";
        //michal ip
        //String restIp = "http://192.168.1.215:8080";

        tempUrl += restIp + header + "?";
        for (String value : values)
            tempUrl += value + "&";
        return tempUrl.substring(0, tempUrl.length() - 1);
    }

}
