package com.example.eventor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Event implements Serializable {

    private String id;
    private String name;
    private String location;
    private String date;
    private boolean hasImage;
    private ArrayList<Product> products;
    private ArrayList<Contact> contacts;
    //private Map<String, Contact> invitedMap;

    //Default constructor

    public Event() {}

    public Event(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.id = generateIdEvent();
        setHasImage(false);
        this.products = new ArrayList<>();
        this.contacts = new ArrayList<>();
    }

    //Copy constructor
    public Event(Event mEvent) {
        setName(mEvent.getName());
        setLocation(mEvent.getLocation());
        setDate(mEvent.getDate());
        setProducts(mEvent.getProducts());
        setContacts(mEvent.getContacts());
        this.id = generateIdEvent();
    }


    /**
     * This method add to the products new product and set how bring this product
     * @param product  to add
     */
    public boolean addProduct(Product product) {
        boolean isProductAdded = false;
        Iterator<Product> iterator = products.iterator();
        while (iterator.hasNext() && !isProductAdded){
            Product itProd = iterator.next();
            if (itProd.getName().equals(product.getName())){
                products.get(products.indexOf(itProd)).setHowBring(product.getHowBring());
                isProductAdded = true;
            }
        }
        if (!isProductAdded) {
            products.add(product);
            isProductAdded = true;
        }
        return isProductAdded;
    }


    /**
     * This method remov product from the products
     *
     * @param product to remove
     */
    public boolean removeProduct(String product) {
        boolean isRemove = false;
        if (products.contains(product)) {
            products.remove(product);
            isRemove = true;
        }
        return isRemove;
    }

    /**
     * This method remove current bring product
     * @param product to remove from
     */
    public boolean removeBringProduct(Product product) {
        boolean isRemoveBring = false;
        if (products.contains(product)) {
            products.get(products.indexOf(product)).setHowBring("");
            isRemoveBring = true;
        }
        return isRemoveBring;
    }


    /*setters*/
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    /*getter*/
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getId() {
        return id;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    /**
     * This method generate for all single event object unique String id
     *
     * @return unique string id
     */
    private String generateIdEvent() {
        return UUID.randomUUID().toString();
    }
}
