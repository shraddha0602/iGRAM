package com.example.shraddha.igram;

public class newComplaint {
    private String dateOfComplaint;
    private String handyman;
    private String datepref1;
    private String timepref1;
    private String description;
    private String status;
    private String complaintId;
    private String studentId;
    private String hostel;
    private String room;
    private String forOrder;

    public newComplaint() {
    }

    public newComplaint(String dateOfComplaint, String handyman, String datepref1, String timepref1, String description, String status, String complaintId, String studentId, String hostel, String room, String forOrder) {
        this.dateOfComplaint = dateOfComplaint;
        this.handyman = handyman;
        this.datepref1 = datepref1;
        this.timepref1 = timepref1;
        this.description = description;
        this.status = status;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.hostel = hostel;
        this.room = room;
        this.forOrder = forOrder;
    }

    public String getHostel() {
        return hostel;
    }

    public String getRoom() {
        return room;
    }

    public String getDateOfComplaint() {
        return dateOfComplaint;
    }

    public String getHandyman() {
        return handyman;
    }

    public String getDatepref1() {
        return datepref1;
    }

    public String getTimepref1() {
        return timepref1;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getForOrder() {
        return forOrder;
    }
}
