package com.example.akshi.cgcradio;
public class EventDetailsBean {
    private String event,org,venue,desc;

    EventDetailsBean(){}
    EventDetailsBean(String event, String org, String venue, String desc) {
        this.event = event;
        this.org = org;
        this.venue = venue;
        this.desc = desc;
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
}
