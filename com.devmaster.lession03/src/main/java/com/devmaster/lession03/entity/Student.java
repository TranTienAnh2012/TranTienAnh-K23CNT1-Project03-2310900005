package com.devmaster.lession03.entity;

public class Student {
    Long ttaId;
    String ttaName;
    int ttaAge;
    String ttaGender;
    String ttaAddress;
    String ttaPhone;
    String ttaEmail;

    public Student() {
    }

    public Student(Long ttaId, String ttaName, int ttaAge, String ttaGender, String ttaAddress, String ttaPhone, String ttaEmail)
    {
        this.ttaId = ttaId;
        this.ttaName = ttaName;
        this.ttaAge = ttaAge;
        this.ttaGender = ttaGender;
        this.ttaAddress = ttaAddress;
        this.ttaPhone = ttaPhone;
        this.ttaEmail = ttaEmail;
    }

    public Long getTtaId() {
        return ttaId;
    }

    public void setTtaId(Long ttaId) {
        this.ttaId = ttaId;
    }

    public String getTtaEmail() {
        return ttaEmail;
    }

    public void setTtaEmail(String ttaEmail) {
        this.ttaEmail = ttaEmail;
    }

    public String getTtaPhone() {
        return ttaPhone;
    }

    public void setTtaPhone(String ttaPhone) {
        this.ttaPhone = ttaPhone;
    }

    public String getTtaAddress() {
        return ttaAddress;
    }

    public void setTtaAddress(String ttaAddress) {
        this.ttaAddress = ttaAddress;
    }

    public String getTtaGender() {
        return ttaGender;
    }

    public void setTtaGender(String ttaGender) {
        this.ttaGender = ttaGender;
    }

    public int getTtaAge() {
        return ttaAge;
    }

    public void setTtaAge(int ttaAge) {
        this.ttaAge = ttaAge;
    }

    public String getTtaName() {
        return ttaName;
    }

    public void setTtaName(String ttaName) {
        this.ttaName = ttaName;
    }
}
