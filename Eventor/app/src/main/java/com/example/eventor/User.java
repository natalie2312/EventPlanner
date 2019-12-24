package com.example.eventor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User implements Serializable {

    private String phoneNumber;
    private String name;
    private Map<String, Boolean> eventsMap;

    //default constructor (for firebase)
    public User(){}

    public User(String userName, String userPhoneNumber){
        if (userPhoneIsValid(userPhoneNumber)){
            setPhoneNumber(userPhoneNumber);
            setName(userName);
            setEventsMap(new HashMap());
        } else {
            //TODO: throw exception
        }
    }

    //constructor without user name - the user name will be same the phone number
    public User(String userPhoneNumber){
        new User(phoneNumber, phoneNumber);
    }

    //copy constructor
    public User(User mUser){
        setPhoneNumber(mUser.getPhoneNumber());
        setName(mUser.getName());
        setEventsMap(mUser.getEventsMap());
    }

    //getters
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Map<String, Boolean> getEventsMap() {
        return eventsMap;
    }

    /**
     * This method add event to this current user events list
     * @param eventId to add
     * @param isManager true if this current is manager of this event, ekse false
     * @return true if the added succeed, else return false
     */
    public boolean putEvent(String eventId, boolean isManager){
        return eventsMap.put(eventId, isManager);
    }

    /**
     * This method add event to this current user events list, by default the manager id false
     * @param eventId to add
     * @return true if the added succeed, else return false
     */
    public boolean putEvent(String eventId){
        return eventsMap.put(eventId, false);
    }

    /**
     * This method set isManager to given event
     * @param eventId to check
     * @param isManager to set
     * @return true if the set succeed, else return false
     */
    public boolean setIsManager(String eventId, boolean isManager){
        if (eventsMap.containsKey(eventId)){
            return eventsMap.put(eventId, isManager);
        }
        return false;
    }

    //setters
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEventsMap(Map<String, Boolean> userEventsMap) {
        this.eventsMap = userEventsMap;
    }



    /**
     * This method check if the phone number is valid
     * @param userPhoneNumber to check
     * @return true if it's valid phone number, else return false
     */
    private boolean userPhoneIsValid(String userPhoneNumber){
        boolean isValid = true;
        //TODO: check if the user phone is valid
        return isValid;
    }

}
