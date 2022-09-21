package com.sohnyi.bookshelf.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RepoSubjectSuggest implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("pic")
    @Expose
    private String pic;

    @SerializedName("author_name")
    @Expose
    private String authorName;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("id")
    @Expose
    private String id;

    private final static long serialVersionUID = 8629072631099126766L;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.pic);
        dest.writeString(this.authorName);
        dest.writeString(this.year);
        dest.writeString(this.type);
        dest.writeString(this.id);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.url = source.readString();
        this.pic = source.readString();
        this.authorName = source.readString();
        this.year = source.readString();
        this.type = source.readString();
        this.id = source.readString();
    }

    public RepoSubjectSuggest() {
    }

    protected RepoSubjectSuggest(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.pic = in.readString();
        this.authorName = in.readString();
        this.year = in.readString();
        this.type = in.readString();
        this.id = in.readString();
    }

    public static final Creator<RepoSubjectSuggest> CREATOR = new Creator<>() {
        @Override
        public RepoSubjectSuggest createFromParcel(Parcel source) {
            return new RepoSubjectSuggest(source);
        }

        @Override
        public RepoSubjectSuggest[] newArray(int size) {
            return new RepoSubjectSuggest[size];
        }
    };
}
