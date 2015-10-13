package com.james.cyprusapp;

/**
 * Created by fappsilya on 13.10.15.
 */
public class User {
    private long id;
    private String name;
    private String photo;
    private int age;

    public User(){

    }


    public User(long id, String name, String photo){
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
