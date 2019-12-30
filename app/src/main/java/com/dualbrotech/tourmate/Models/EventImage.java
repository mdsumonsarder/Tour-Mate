package com.dualbrotech.tourmate.Models;

/**
 * Created by Arif Rahman on 2/13/2018.
 */

public class EventImage {
    private String eventId;
    private String downloadUrl;
    private String imageName;

    public EventImage(){

    }

    public EventImage(String eventId, String downloadUrl, String imageName) {
        this.eventId = eventId;
        this.downloadUrl = downloadUrl;
        this.imageName = imageName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
