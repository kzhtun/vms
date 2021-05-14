package com.info121.vms.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.info121.vms.R;
import com.info121.vms.models.Vehicle;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by KZHTUN on 2/22/2018.
 */

public class Utils {
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    public static void setButtonEnable(Context context, Button button, boolean flag){
        final int sdk = android.os.Build.VERSION.SDK_INT;

        button.setEnabled(flag);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if(flag)
                button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_green) );
            else
                button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_grey) );
        } else {
            if(flag)
                button.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_green));
            else
                button.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_grey));
        }

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.length() == 0 || string.trim().equals(""));
    }

    public static String trimSpaces(String text) {
        return text.replaceAll("\\s+","");
    }

    public static long convertDateStringToInt(String dateString) {
        long dateLong = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);

            dateLong = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateLong;
    }

    public static Date convertDateStringToDate(String dateString, String inputDateFormat) {

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(inputDateFormat);
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    public static String convertLongDateToString(long date, String outputDateFormat) {
        return DateFormat.format(outputDateFormat, new Date(date)).toString();
    }

    public static String convertDateToString(Date date, String outputDateFormat) {
        return new SimpleDateFormat(outputDateFormat).format(date).toString();
    }

    public static void hideSoftKeyboard(AppCompatActivity activity) {
        View view = activity.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Drawable getDrawable(String photoName){
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + photoName);

        return Drawable.createFromPath(file.getAbsolutePath());
    }

    public static RequestBody getPhotoRequestBody(String fileName) {

        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("image/*");

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + fileName);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return RequestBodyUtils.create(MEDIA_TYPE_MARKDOWN, inputStream);
    }

    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public static String getVersionName(Context context) {
        String versionName ="";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static long getVehicleID(String uuid){
        List<Vehicle> vList;
        vList = Select.from(Vehicle.class)
                .where(Condition.prop("UUID").eq(uuid))
                .list();

        return vList.get(0).getId();

    }



}
