package com.example.akshi.cgcradio;

import android.support.annotation.NonNull;

public class EventDetailsBean {
    private String event,org,venue,desc,url;

    EventDetailsBean(){}
    EventDetailsBean(String event, String org, String venue, String desc, String url) {
        this.event = event;
        this.org = org;
        this.venue = venue;
        this.desc = desc;
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @NonNull
    @Override
    public String toString() {
        return getEvent()+getDesc()+getOrg()+getUrl()+getVenue();
    }
}
