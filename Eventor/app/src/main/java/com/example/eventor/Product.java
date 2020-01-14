package com.example.eventor;

import java.io.Serializable;

public class Product implements Serializable {

    private String name;
    private String howBring;



    public Product(){
        new Product("");
    }

    public Product(String n) {
        this.name = n;
        this.howBring = "";
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHowBring(String howBring) {
        this.howBring = howBring;
    }

    public String getHowBring() {
        return this.howBring;
    }
}
