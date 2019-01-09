package com.info121.vms.models;

/**
 * Created by KZHTUN on 3/27/2018.
 */

public class SaveAll {
    String CarPhotoStatus;
    String DriverPhotoStatus;
    String Photo1Status;

    public SaveAll(String carPhotoStatus, String driverPhotoStatus, String photo1Status) {
        CarPhotoStatus = carPhotoStatus;
        DriverPhotoStatus = driverPhotoStatus;
        Photo1Status = photo1Status;
    }

    public String getCarPhotoStatus() {
        return CarPhotoStatus;
    }

    public void setCarPhotoStatus(String carPhotoStatus) {
        CarPhotoStatus = carPhotoStatus;
    }

    public String getDriverPhotoStatus() {
        return DriverPhotoStatus;
    }

    public void setDriverPhotoStatus(String driverPhotoStatus) {
        DriverPhotoStatus = driverPhotoStatus;
    }

    public String getPhoto1Status() {
        return Photo1Status;
    }

    public void setPhoto1Status(String photo1Status) {
        Photo1Status = photo1Status;
    }
}
