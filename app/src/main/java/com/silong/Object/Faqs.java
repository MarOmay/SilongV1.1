package com.silong.Object;

import com.silong.Operation.Utility;

public class Faqs {

    String heading;
    String body;
    String[] tags;
    boolean visibility;

    public Faqs(String heading, String body, String[] tags) {
        this.heading = heading;
        this.body = body;
        this.tags = tags;
        this.visibility = false;
    }

    public boolean isInTags(String s){
        for (String st : tags){
            if (st.toLowerCase().contains(s.toLowerCase()))
                return true;
        }
        return false;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}