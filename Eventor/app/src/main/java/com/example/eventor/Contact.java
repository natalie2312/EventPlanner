package com.example.eventor;


import java.io.Serializable;

public class Contact implements Serializable {

    private String name;
    private String phoneNumber;
    private boolean isSelected;


    public Contact(){}
    public Contact(String name, String phoneNumber){
        setName(name);
        setPhoneNumber(phoneNumber);
        isSelected = true;
    }

    /*setters*/
    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /*getters*/
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
