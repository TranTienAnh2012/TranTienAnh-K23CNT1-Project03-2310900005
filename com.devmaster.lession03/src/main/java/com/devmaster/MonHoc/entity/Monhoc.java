package com.devmaster.MonHoc.entity;

public class Monhoc {
    private long ttaId;
    private String ttaFullname;
    private String ttaGender;
    private int ttaAge;
    private double ttaSalary;

    public Monhoc() {
    }

    public Monhoc(long ttaId, String ttaFullname, String ttaGender, int ttaAge, double ttaSalary) {
        this.ttaId = ttaId;
        this.ttaFullname = ttaFullname;
        this.ttaGender = ttaGender;
        this.ttaAge = ttaAge;
        this.ttaSalary = ttaSalary;
    }

    public long getTtaId() {
        return ttaId;
    }

    public void setTtaId(long ttaId) {
        this.ttaId = ttaId;
    }

    public String getTtaFullname() {
        return ttaFullname;
    }

    public void setTtaFullname(String ttaFullname) {
        this.ttaFullname = ttaFullname;
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

    public double getTtaSalary() {
        return ttaSalary;
    }

    public void setTtaSalary(double ttaSalary) {
        this.ttaSalary = ttaSalary;
    }
}
