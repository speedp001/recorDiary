package com.gachon.recordiary;

import android.os.Parcel;
import android.os.Parcelable;

public class datalist implements Parcelable {
    String writeUser;
    String titletext;
    String datetext;
    String diarytext;
    String imagepath;
    String writeUID;

    public datalist() {
    }

    public datalist(String writeUser, String datetext, String titletext, String diarytext, String imagepath, String writeUID) {
        this.writeUser = writeUser; //작성한 유저
        this.datetext = datetext; //일기 날짜
        this.titletext = titletext; //일기 제목
        this.diarytext = diarytext; //일기 내용
        this.imagepath = imagepath;//사진
        this.writeUID = writeUID; //작성한 유저 UID
    }

    protected datalist(Parcel in) {
        this.writeUser =in.readString();
        this.datetext=in.readString();
        this.titletext=in.readString();
        this.diarytext=in.readString();
        this.imagepath=in.readString();
        this.writeUID=in.readString();
    }

    //Alt+insert를 누르면 get/set 자동으로 생성된다

    public String getWriteUser() {return writeUser; }

    public String getDatetext() {
        return datetext;
    }

    public String getTitletext() {
        return titletext;
    }

    public String getDiarytext() {
        return diarytext;
    }

    public String getImagepath() { return imagepath; }

    public String getWriteUID() { return writeUID; }


    public void setDatetext(String date) {
        this.datetext = date;
    }

    public void setTitletext(String titletext) {
        this.titletext = titletext;
    }

    public void setDiarytext(String diarytext) {
        this.diarytext = diarytext;
    }

    public void setImagepath(String imagepath) { this.imagepath = imagepath; }

    public void setWriteUser(String writeUser) {
        this.writeUser = writeUser;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.writeUser);
        parcel.writeString(this.datetext);
        parcel.writeString(this.titletext );
        parcel.writeString(this.diarytext );
        parcel.writeString(this.imagepath) ;
        parcel.writeString(this.writeUID);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public datalist createFromParcel(Parcel in) {
            return new datalist(in);
        }

        @Override
        public datalist[] newArray(int size) {
            // TODO Auto-generated method stub
            return new datalist[size];
        }


    };
}

