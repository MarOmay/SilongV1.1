package com.silong.dev;

public class Adoption {

    private int id;
    private String dateRequested;
    private String appointmentDate;
    private int status;
    private String dateReleased;

    public Adoption(int id, String dateRequested, String appointmentDate, int status, String dateReleased) {
        this.id = id;
        this.dateRequested = dateRequested;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.dateReleased = dateReleased;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateReleased() {
        return dateReleased;
    }

    public void setDateReleased(String dateReleased) {
        this.dateReleased = dateReleased;
    }
}
