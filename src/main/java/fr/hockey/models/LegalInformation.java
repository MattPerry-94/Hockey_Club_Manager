package fr.hockey.models;

import java.time.LocalDateTime;

public class LegalInformation {
    private int id;
    private String name;
    private String address;
    private String regNo;
    private String publisher;
    private String hosting;
    private String contact;
    private String privacy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getHosting() { return hosting; }
    public void setHosting(String hosting) { this.hosting = hosting; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getPrivacy() { return privacy; }
    public void setPrivacy(String privacy) { this.privacy = privacy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}