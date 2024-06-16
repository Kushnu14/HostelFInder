package com.example.hostelfinder.Recycler;

public class Model {
    String id;
    String Name_Of_Hostel,Address,Gender;
    Integer Capacity;

    Double Rent;

    String Contact;
    String img;

    public Model() {
    }

    public Model(String id, String name_Of_Hostel, String address, String gender, Integer capacity, Double rent, String contact) {
        this.id = id;
        this.Name_Of_Hostel = name_Of_Hostel;
        this.Address = address;
        this.Gender = gender;
        this.Capacity = capacity;
        this.Rent = rent;
        this.Contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName_Of_Hostel() {
        return Name_Of_Hostel;
    }

    public void setName_Of_Hostel(String name_Of_Hostel) {
        this.Name_Of_Hostel = name_Of_Hostel;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public Integer getCapacity() {
        return Capacity;
    }

    public void setCapacity(Integer capacity) {
        this.Capacity = capacity;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        this.Contact = contact;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Double getRent() {
        return Rent;
    }

    public void setRent(Double rent) {
        this.Rent = rent;
    }
}
