package com.info121.vms;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;

import com.info121.vms.utilities.PrefDB;
import com.info121.vms.utilities.Utils;
import com.info121.vms.models.Vehicle;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orm.SugarApp;

import java.io.File;

/**
 * Created by KZHTUN on 2/19/2018.
 */

public class App extends SugarApp {
    public static String CONST_REST_API_URL_TEMPLATE = "http://{IPADDRESS}/restVMS/VMS.svc/";
    public static String CONST_REST_API_URL = "http://{IPADDRESS}/restVMS/VMS.svc/";

    public static String CONST_PHOTO_UPLOAD_TEMPLATE = "http://{IPADDRESS}/VCMS_FileUpload/api/";
    public static String CONST_PHOTO_UPLOAD_URL = "http://{IPADDRESS}/VCMS_FileUpload/api/";

    public static String CONST_PHOTO_DOWNLOAD_TEMPLATE = "http://{IPADDRESS}/VCMS_Photo";
    public static String CONST_PHOTO_DOWNLOAD_URL = "http://{IPADDRESS}/VCMS_Photo";

//    public static String CONST_PHOTO_UPLOAD_TEMPLATE = "http://{IPADDRESS}/FileUpload_Test/api/";
//    public static String CONST_PHOTO_UPLOAD_URL = "http://{IPADDRESS}/FileUpload_Test/api/";


   // public static String CONST_PHOTO_URL = "http://118.200.199.248:81/VCMS_Photo/";
    public static String CONST_FTP_URL = "118.200.199.248";
    public static final String CONST_FTP_USER = "ipos";
    public static final String CONST_FTP_PASSWORD = "iposftp";
    public static final String CONST_FTP_DIR = "vmspics";

    public static String Condo_Name = "";
    public static String User_Name = "";


    // public static final String CONST_WEBSITE_URL = "http://titaniumlimos.com/iops_portal/";

    public static Vehicle objVehicle;
//    private FirebaseAnalytics mFirebaseAnalytics;


    public static DisplayImageOptions ILOption;
    public static ImageLoaderConfiguration ILConfig;
    public static final ImageLoader UIL = ImageLoader.getInstance();




    @Override
    public void onCreate() {
        super.onCreate();

        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Utils.getDeviceID(this));
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Utils.getDeviceName());
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        PrefDB prefDB = new PrefDB(getApplicationContext());

        CONST_REST_API_URL = CONST_REST_API_URL_TEMPLATE.replace("{IPADDRESS}", prefDB.getString("CURRENT_IP"));
        CONST_PHOTO_UPLOAD_URL = CONST_PHOTO_UPLOAD_TEMPLATE.replace("{IPADDRESS}", prefDB.getString("CURRENT_IP"));

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .showImageOnLoading(R.mipmap.no_photo)
                .build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        UIL.init(config);

        File f = new File(Environment.getExternalStorageDirectory(), "vms");
        if (!f.exists()) {
            f.mkdirs();
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //builder.detectFileUriExposure();
    }
}
