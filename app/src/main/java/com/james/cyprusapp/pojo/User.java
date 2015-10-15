package com.james.cyprusapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {
    private long id;
    private String name;
    private String photo;
    private int age;

    public User(){

    }

    protected User(Parcel in) {
        id = in.readLong();
        name = in.readString();
        photo = in.readString();
        age = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(photo);
        dest.writeInt(age);
    }
}
