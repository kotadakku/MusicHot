package com.example.musichot.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {
    String id, title, image, icon;

    public Playlist(String id, String title, String image, String icon) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.icon = icon;
    }

    protected Playlist(Parcel in) {
        id = in.readString();
        title = in.readString();
        image = in.readString();
        icon = in.readString();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(icon);
    }
}
