package com.example.eventor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

public class Event implements Serializable {

    private String id;
    private String name;
    private String location;
    private String date;
    private Map<String, String> productsMap;
    private ArrayList<String> invitedList;

    //Default constructor

    public Event(){}
    public Event(String name, String date, String location){
        this.name = name;
        this.date = date;
        this.location = location;
        this.id = generateIdEvent();
        this.productsMap = new HashMap<>();
        //invitedList = new ArrayList<>();
    }

    //Copy constructor
    public Event(Event mEvent){
        setName(mEvent.getName());
        setLocation(mEvent.getLocation());
        setDate(mEvent.getDate());
        setProductsMap(mEvent.getProductsMap());
        this.id = generateIdEvent();
    }


    /**
     * This method add to the products new product and set how bring this product
     * @param product to add
     * @param howBring to set
     */
    public void addProduct(String product, String howBring){
        if (!productsMap.containsKey(product)) {
            productsMap.put(product, howBring);
        }
    }

    /**
     * This method add product to the products
     * @param product to add
     */
    public void addProduct(String product){
        addProduct(product, "");
    }

    /**
     * This method remov product from the products
     * @param product to remove
     */
    public void removeProduct(String product){

        if (productsMap.containsKey(product)){
            productsMap.remove(product);
        }
    }

    /**
     * This method remove current bring product
     * @param product to remove from
     */

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

    public String getId() {
        return id;
    }


    /**
     * This method vreate ArrayList of all the events
     * @return
     */
    /*public ArrayList<String> productsList(){

        ArrayList<String> products = new ArrayList<>();
        for (String product : productsMap.keySet()){
            products.add(product);
        }
        return products;
    }*/


    /**
     * This method generate for all single event object unique String id
     * @return unique string id
     */
    private String generateIdEvent(){
        return UUID.randomUUID().toString();
    }
}
