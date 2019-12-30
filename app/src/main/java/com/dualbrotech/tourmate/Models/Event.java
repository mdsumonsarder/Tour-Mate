package com.dualbrotech.tourmate.Models;

import java.io.Serializable;

/**
 * Created by Arif Rahman on 1/23/2018.
 */

public class Event implements Serializable{
    private String eventName;
    private String eventDate;
    private String eventBudget;
    private String nodeKey;
    private String user_id;



    public Event(String eventName, String eventDate, String eventBudget, String nodeKey, String user_id) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventBudget = eventBudget;
        this.nodeKey = nodeKey;
        this.user_id = user_id;
    }

    //    public Event(String eventName, String eventDate, String eventBudget, String nodeKey) {
//
//        this.eventName = eventName;
//        this.eventDate = eventDate;
//        this.eventBudget = eventBudget;
//        this.nodeKey = nodeKey;
//    }
//
//    public Event(String eventName, String eventDate, String eventBudget) {
//
//        this.eventName = eventName;
//        this.eventDate = eventDate;
//        this.eventBudget = eventBudget;
//    }

    public Event(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventBudget() {
        return eventBudget;
    }

    public void setEventBudget(String eventBudget) {
        this.eventBudget = eventBudget;
    }


}
