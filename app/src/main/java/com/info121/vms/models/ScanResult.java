
package com.info121.vms.models;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ScanResult {

    @SerializedName("blockno")
    private String mBlockno;
    @SerializedName("carnumber")
    private String mCarnumber;
    @SerializedName("carphoto")
    private String mCarphoto;
    @SerializedName("drivername")
    private String mDrivername;
    @SerializedName("driverphone")
    private String mDriverphone;
    @SerializedName("entrydatetime")
    private String mEntrydatetime;
    @SerializedName("entryfrom")
    private String mEntryfrom;
    @SerializedName("iunumber")
    private String mIunumber;
    @SerializedName("purposeofvisit")
    private String mPurposeofvisit;
    @SerializedName("resident")
    private String mResident;
    @SerializedName("roomno")
    private String mRoomno;
    @SerializedName("serialno")
    private String mSerialno;
    @SerializedName("storey")
    private String mStorey;

    public String getBlockno() {
        return mBlockno;
    }

    public void setBlockno(String blockno) {
        mBlockno = blockno;
    }

    public String getCarnumber() {
        return mCarnumber;
    }

    public void setCarnumber(String carnumber) {
        mCarnumber = carnumber;
    }

    public String getCarphoto() {
        return mCarphoto;
    }

    public void setCarphoto(String carphoto) {
        mCarphoto = carphoto;
    }

    public String getDrivername() {
        return mDrivername;
    }

    public void setDrivername(String drivername) {
        mDrivername = drivername;
    }

    public String getDriverphone() {
        return mDriverphone;
    }

    public void setDriverphone(String driverphone) {
        mDriverphone = driverphone;
    }

    public String getEntrydatetime() {
        return mEntrydatetime;
    }

    public void setEntrydatetime(String entrydatetime) {
        mEntrydatetime = entrydatetime;
    }

    public String getEntryfrom() {
        return mEntryfrom;
    }

    public void setEntryfrom(String entryfrom) {
        mEntryfrom = entryfrom;
    }

    public String getIunumber() {
        return mIunumber;
    }

    public void setIunumber(String iunumber) {
        mIunumber = iunumber;
    }

    public String getPurposeofvisit() {
        return mPurposeofvisit;
    }

    public void setPurposeofvisit(String purposeofvisit) {
        mPurposeofvisit = purposeofvisit;
    }

    public String getResident() {
        return mResident;
    }

    public void setResident(String resident) {
        mResident = resident;
    }

    public String getRoomno() {
        return mRoomno;
    }

    public void setRoomno(String roomno) {
        mRoomno = roomno;
    }

    public String getSerialno() {
        return mSerialno;
    }

    public void setSerialno(String serialno) {
        mSerialno = serialno;
    }

    public String getStorey() {
        return mStorey;
    }

    public void setStorey(String storey) {
        mStorey = storey;
    }

}
