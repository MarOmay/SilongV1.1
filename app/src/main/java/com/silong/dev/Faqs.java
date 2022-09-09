package com.silong.dev;

public class Faqs {

    String heading;
    String body;
    boolean visibility;

    public Faqs(String heading, String body) {
        this.heading = heading;
        this.body = body;
        this.visibility = false;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}