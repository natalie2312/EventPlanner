package com.example.eventor;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Event {

    private String idEvent;
    private String nameEvent;
    private String locationEvent;
    private Date dateEvent;
    private ArrayList<String> productsEventList;
    private ArrayList<String> invitedList;


    public Event(String nameEvent, Date dateEvent, String locationEvent){
        this.nameEvent = nameEvent;
        this.dateEvent = dateEvent;
        this.locationEvent = locationEvent;


        idEvent = generateStringId();
        productsEventList = new ArrayList<>();
        invitedList = new ArrayList<>();
    }



    public boolean addProduct(String product){
        return productsEventList.add(product);
    }





    /*setters*/
    public void setEventName(String nameEvent){
        this.nameEvent = nameEvent;
    }

    public void setDate(Date dateEvent){
        this.dateEvent = dateEvent;
    }

    public void setLocationEvent(String locationEvent) {
        this.locationEvent = locationEvent;
    }

    public boolean setProductsEventList(ArrayList<String> productsList){
        return productsEventList.addAll(productsList);
    }




    /*getter*/
    public String getNameEvent(){
        return nameEvent;
    }

    public Date getDate(){
        return dateEvent;
    }

    public String getLocationEvent(){
        return locationEvent;
    }

    public ArrayList<String> getProductList(){
        return productsEventList;
    }

    public String getIdEvent() {
        return idEvent;
    }

    private String generateStringId(){
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }
}
