package com.devmaster.ttaKhoa.entity;

public class Khoa {
    private long ttaMakh;
    private String ttaTenkh;

    public Khoa() {
    }

    public Khoa(long ttaMakh, String ttaTenkh) {
        this.ttaMakh = ttaMakh;
        this.ttaTenkh = ttaTenkh;
    }

    public long getTtaMakh() {
        return ttaMakh;
    }

    public void setTtaMakh(long ttaMakh) {
        this.ttaMakh = ttaMakh;
    }

    public String getTtaTenkh() {
        return ttaTenkh;
    }

    public void setTtaTenkh(String ttaTenkh) {
        this.ttaTenkh = ttaTenkh;
    }
}
