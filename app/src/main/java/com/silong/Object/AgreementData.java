package com.silong.Object;

import java.io.Serializable;

public class AgreementData implements Serializable {

    private String agreementDate;
    private String agreementTitle;
    private String agreementBody;

    public AgreementData(){

    }

    public AgreementData(String agreementTitle, String agreementBody){
        this.agreementTitle = agreementTitle;
        this.agreementBody = agreementBody;
    }

    public String getAgreementTitle() {
        return agreementTitle;
    }

    public void setAgreementTitle(String agreementTitle) {
        this.agreementTitle = agreementTitle;
    }

    public String getAgreementBody() {
        return agreementBody;
    }

    public void setAgreementBody(String agreementBody) {
        this.agreementBody = agreementBody;
    }

    public String getAgreementDate() {
        return agreementDate;
    }

    public void setAgreementDate(String agreementDate) {
        this.agreementDate = agreementDate;
    }
}
