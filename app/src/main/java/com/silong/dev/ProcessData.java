package com.silong.dev;

import android.graphics.Bitmap;

public class ProcessData {

    private String processGenderType;
    private Integer processImage;

    public ProcessData(String processGenderType, Integer processImage) {
        this.processGenderType = processGenderType;
        this.processImage = processImage;
    }

    public String getProcessGenderType() {
        return processGenderType;
    }

    public void setProcessGenderType(String processType) {
        this.processGenderType = processGenderType;
    }

    public Integer getProcessImage() {
        return processImage;
    }

    public void setProcessImage(Integer processImage) {
        this.processImage = processImage;
    }
}
