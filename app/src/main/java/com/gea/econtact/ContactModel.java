package com.gea.econtact;

public class ContactModel {
    private String Id;
    private String Name, Email, Phone, DateBirth,Gender, Description;
    private byte[] Image;


    public ContactModel(String id, String name, String email, String phone, String dateBirth, String gender, String description, byte[] image) {
        Id = id;
        Name = name;
        Email = email;
        Phone = phone;
        DateBirth = dateBirth;
        Gender = gender;
        Description = description;
        Image = image;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getDateBirth() {
        return DateBirth;
    }

    public void setDateBirth(String dateBirth) {
        DateBirth = dateBirth;
    }
    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }
}
