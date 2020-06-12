package com.example.musichot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lincoln on 18/05/16.
 */
public class Album implements Parcelable {
    private String name;
    private String artist;
    private String thumbnail;
    private String id;
    private String status;

    public Album() {
    }

    public Album(String name, String artist, String thumbnail, String id, String status) {
        this.name = name;
        this.artist = artist;
        this.thumbnail = thumbnail;
        this.id = id;
        this.status = status;
    }

    protected Album(Parcel in) {
        name = in.readString();
        artist = in.readString();
        thumbnail = in.readString();
        id = in.readString();
        status = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(artist);
        dest.writeString(thumbnail);
        dest.writeString(id);
        dest.writeString(status);
    }
}
