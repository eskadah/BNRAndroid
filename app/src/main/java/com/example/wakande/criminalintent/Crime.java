package com.example.wakande.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by wakande on 3/18/16.
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mPhoneNumber;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPhotoFIlename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public Date getDate() {

        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public UUID getId() {

        return mId;
    }
}
