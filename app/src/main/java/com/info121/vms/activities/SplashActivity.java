package com.info121.vms.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.info121.vms.AbstractActivity;
import com.info121.vms.App;
import com.info121.vms.R;
import com.info121.vms.utilities.PrefDB;
import com.info121.vms.utilities.Utils;
import com.info121.vms.api.APIClient;
import com.info121.vms.models.Block;
import com.info121.vms.models.CondoInfo;
import com.info121.vms.models.Purpose;
import com.info121.vms.models.Storey;
import com.info121.vms.models.UnitNo;
import com.info121.vms.models.UserRes;
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;



import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AbstractActivity {
    private static final int CAMERA_PERMISSION_ID = 1004;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_ID = 1005;


    private boolean allLoaded = false;

    private boolean userLoaded = false;
    private boolean purposeLoaded = false;
    private boolean blockLoaded = false;
    private boolean storeyLoaded = false;
    private boolean unitLoaded = false;
    private  boolean codoInfoLoaded = false;
    private  boolean loginPhotoLoaded = false;

    private String CONST_CONDO_NAME = "";
    private String CONST_HOME_PHOTO_PATH = "";

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE};

    ImageView mCondoPhoto, mLoginPhoto;

    @BindView(R.id.tv_status_message)
    TextView mStatus;

    PrefDB prefDB;
    final Handler handler = new Handler();
    int delay = 3000;



    @Override
    protected void onResume() {
        super.onResume();

    //    checkForCrashes();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // unregisterManagers();
    }

//    private void unregisterManagers() {
//        UpdateManager.unregister();
//    }
//
//    private void checkForCrashes() {
//        CrashManager.register(this);
//    }
//
//    private void checkForUpdates() {
//        // Remove this for store builds!
//        UpdateManager.register(this);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        prefDB = new PrefDB(getApplicationContext());

        mCondoPhoto = (ImageView) findViewById(R.id.condo_photo);
        mLoginPhoto = (ImageView) findViewById(R.id.login_photo);

      //  checkForUpdates();

        if (Utils.isNullOrEmpty(prefDB.getString("CURRENT_IP"))) {
            finish();
            startActivity(new Intent(SplashActivity.this, SettingsActivity.class));
            return;
        }else{
            App.CONST_REST_API_URL = App.CONST_REST_API_URL_TEMPLATE.replace("{IPADDRESS}", prefDB.getString("CURRENT_IP"));
            App.CONST_PHOTO_DOWNLOAD_URL = App.CONST_PHOTO_DOWNLOAD_URL.replace("{IPADDRESS}", prefDB.getString("CURRENT_IP"));
        }


        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

//        SugarContext.terminate();
//        SchemaGenerator schemaGenerator = new SchemaGenerator(getApplicationContext());
//        schemaGenerator.deleteTables(new SugarDb(getApplicationContext()).getDB());
//        SugarContext.init(getApplicationContext());
//        schemaGenerator.createDatabase(new SugarDb(getApplicationContext()).getDB());

        if (prefDB.getBoolean("SYNCHONIZED")) {
            App.Condo_Name = prefDB.getString(CONST_CONDO_NAME);
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        } else {
            SugarContext.terminate();
            SchemaGenerator schemaGenerator = new SchemaGenerator(getApplicationContext());
            schemaGenerator.deleteTables(new SugarDb(getApplicationContext()).getDB());
            SugarContext.init(getApplicationContext());
            schemaGenerator.createDatabase(new SugarDb(getApplicationContext()).getDB());

            mStatus.setText("User Information is Loading ...");
            APIClient.GetUserList();

           // APIClient.GetAllBlocks();
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if (requestCode == CAMERA_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            finish();
//            startActivity(getIntent());
//        }
//
//        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            finish();
//            startActivity(getIntent());
//        }
//    }

    @Subscribe
    public void onEvent(List<?> res) {
        if (res.size() == 0) return;

        if (res.get(0) instanceof UserRes) {

            for (int i = 0; i < res.size(); i++) {
                UserRes u = (UserRes) res.get(i);

                u.save();
            }

            userLoaded = true;
            mStatus.setText("Purpose is Loading ...");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    APIClient.GetAllPurpose();
                }
            }, delay);
        }


        if (res.get(0) instanceof Purpose) {
            for (int i = 0; i < res.size(); i++) {
                Purpose p = (Purpose) res.get(i);
                p.save();
            }

            purposeLoaded = true;
            mStatus.setText("Block Information is Loading ...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    APIClient.GetAllBlocks();
                }
            }, delay);

        }


        if (res.get(0) instanceof Block) {
            for (int i = 0; i < res.size(); i++) {
                Block b = (Block) res.get(i);
                b.save();
            }

            blockLoaded = true;
            mStatus.setText("Storey Information is Loading ...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    APIClient.GetAllStoreys();
                }
            }, delay);

        }

        if (res.get(0) instanceof Storey) {
            for (int i = 0; i < res.size(); i++) {
                Storey s = (Storey) res.get(i);
                s.save();
            }

            storeyLoaded = true;
            mStatus.setText("Unit No Information is Loading ...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    APIClient.GetAllUnitNos();
                }
            }, delay);

        }

        if (res.get(0) instanceof UnitNo) {
            for (int i = 0; i < res.size(); i++) {
                UnitNo u = (UnitNo) res.get(i);
                u.save();
            }

            unitLoaded = true;
            mStatus.setText("Condo Information is Loading ...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    APIClient.GetCondoInformation();
                }
            }, delay);

        }



    }


//    @OnClick(R.id.tv_system_information)
//    public void systemInformationOnClick() {
//        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String imageType;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            imageType = urls[1];

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            savePhoto(result, imageType +   ".jpg");

        }
    }


    @Subscribe
    public void onEvent(CondoInfo c) throws IOException {
        prefDB.putString(CONST_CONDO_NAME, c.getCondoName());


//        URL url = new URL(c.getHomePagePhoto());
//        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

//        mCondoPhoto.setImageBitmap(bmp);

//        new DownloadImageTask(mCondoPhoto)
//                .execute(c.getHomePagePhoto(), "HomePhoto");
//
//        new DownloadImageTask(mLoginPhoto)
//                .execute(c.getLogInPhoto(), "LoginPhoto");


        //TODO: to delete ...
        codoInfoLoaded = true;
        loginPhotoLoaded = true;
        startLogin();

    }

    private void savePhoto(Bitmap photoData, String fileName) {

        File f = new File(Environment.getExternalStorageDirectory(), "vms");
        if (!f.exists()) {
            f.mkdirs();
        }


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photoData.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + fileName);
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);

            fo.write(bytes.toByteArray());
            fo.close();

            if(fileName.contains("HomePhoto")){
                codoInfoLoaded = true;
            }

            if(fileName.contains("LoginPhoto")){
                loginPhotoLoaded = true;
            }

            startLogin();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    private void startLogin(){
        allLoaded = userLoaded & purposeLoaded & blockLoaded & storeyLoaded & unitLoaded & codoInfoLoaded & loginPhotoLoaded;

        if(allLoaded){
            prefDB.putBoolean("SYNCHONIZED", true);
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

}





