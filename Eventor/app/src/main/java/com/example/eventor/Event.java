package com.example.eventor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Event implements Serializable {

    private String id;
    private String name;
    private String location;
    private String date;
    //private ArrayList<String> productsList;
    private Map<String, String> productsMap;
    private ArrayList<String> invitedList;


    public Event(){}
    public Event(String name, String date, String location){
        this.name = name;
        this.date = date;
        this.location = location;
        this.id = generateIdEvent();
        this.productsMap = new HashMap<>();
        //invitedList = new ArrayList<>();

    }



    public void addProduct(String product, String howGet){
        if (!productsMap.containsKey(product)) {
            productsMap.put(product, howGet);
        }
    }
    public void addProduct(String product){
        addProduct(product, "");
    }
    public void removeProducts(String product){
        if (productsMap.containsKey(product)){
            productsMap.remove(product);
        }
    }
    public void removeBringProduct(String product){
        if (productsMap.containsKey(product)){
            productsMap.put(product, "");
        }
    }






    /*setters*/
    public void setName(String name){
        this.name = name;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProductsMap(Map<String, String> productsMap){
        this.productsMap = productsMap;
    }

    /*
    public void setInvitedList(ArrayList<String> invitedList) {
        this.invitedList = invitedList;
    }
    */
    /*public void setEventId(String eventId) {
        this.eventId = eventId;
    }*/

    /*getter*/
    public String getName(){
        return name;
    }

    public String getDate(){
        return date;
    }

    public String getLocation(){
        return location;
    }

    public Map<String, String> getProductsMap(){
        return productsMap;
    }

    /**
     * This method vreate ArrayList of all the events
     * @return
     */
    public ArrayList<String> getProductsList(){
        ArrayList<String> products = new ArrayList<>();
        for (String product : productsMap.keySet()){
            products.add(product);
        }
        return products;
    }

    public String getId() {
        return id;
    }
    /*
    public ArrayList<String> getInvitedList() {
        return invitedList;
    }
    */


    /**
     * This method generate for all single event object unique String id
     * @return unique string id
     */
    private String generateIdEvent(){
        return UUID.randomUUID().toString();
    }


}
