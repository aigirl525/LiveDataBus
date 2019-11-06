package com.example.livedatabus.databinding;

import java.util.Observable;

public class User extends Observable {
    private String name;
    private  String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

