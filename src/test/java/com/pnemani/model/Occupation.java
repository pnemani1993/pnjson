package com.pnemani.model;

public class Occupation {
    private String organization;
    private String position;

    public Occupation(){}

    public Occupation(String organization, String position){
        this.organization = organization;
        this.position = position;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
