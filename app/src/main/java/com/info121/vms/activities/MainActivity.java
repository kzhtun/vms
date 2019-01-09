package com.info121.vms.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.info121.vms.App;

import com.info121.vms.R;
import com.info121.vms.utilities.Utils;
import com.info121.vms.models.Status;
import com.info121.vms.models.Vehicle;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.main_background)
    ImageView mHomeBackground;

    TextView mCondoName, mNavWelcome, mNavDateTime;

    List<Vehicle> notSendList;
    List<Vehicle> sentList;

    @BindView(R.id.date_time)
    TextView mDateTime;


    @BindView(R.id.welcome)
    TextView mWelcomeMsg;

    MenuItem mHome, mNotSendCar, mProfile, mSentCar, mRegister, mLogout, mScanIU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   FirebaseCrash.report(new Exception("My first Android non-fatal error"));

        ButterKnife.bind(this);

//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        finish();

        // mNavigationView = findViewById(R.id.navigation_view);
        mHome = mNavigationView.getMenu().findItem(R.id.home);
        mNotSendCar = mNavigationView.getMenu().findItem(R.id.not_send_car);
        mSentCar = mNavigationView.getMenu().findItem(R.id.sent_car);
        mProfile = mNavigationView.getMenu().findItem(R.id.my_profile);

        mScanIU = mNavigationView.getMenu().findItem(R.id.scan_iu);
        mRegister = mNavigationView.getMenu().findItem(R.id.register_car);
        mLogout = mNavigationView.getMenu().findItem(R.id.logout);
        mNavWelcome = mNavigationView.getHeaderView(0).findViewById(R.id.welcome);
        mCondoName =  mNavigationView.getHeaderView(0).findViewById(R.id.condo_name);
        mNavDateTime = mNavigationView.getHeaderView(0).findViewById(R.id.date_time);


        mCondoName.setText(App.Condo_Name);
        mWelcomeMsg.setText("Welcome " + App.User_Name );

       // mToolbar.setTitle("HOME");
        setSupportActionBar(mToolbar);

        initializeEvents();

        Drawable mainDrawable = Utils.getDrawable("HomePhoto.jpg");
        if(mainDrawable != null){
            mHomeBackground.setBackground(mainDrawable);
        }

        final Handler timer = new Handler(getMainLooper());
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {

                String dateString = Utils.convertDateToString(Calendar.getInstance().getTime(), "dd-MMM-yyyy hh:mm:ss a");

                mDateTime.setText(dateString.toString());
                mNavDateTime.setText(dateString.toString());
                timer.postDelayed(this, 1000);
            }
        }, 1000);



// --- Retrofit test --------------------

//        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("image/*");
//
//        InputStream inputStream = null;
//        try {
//            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + "pexelsphoto.jpg");
//             inputStream = new FileInputStream(file);
//          //  inputStream = getAssets().open(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + "pexelsphoto.jpg");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody requestBody = RequestBodyUtils.create(MEDIA_TYPE_MARKDOWN, inputStream);
//
//        APIClient.UploadPhoto1(requestBody);


    }

    @Subscribe
    public void onEvent(String s){
        Log.e("Upload Photo : ", s);
    }


    @OnClick(R.id.btn_register_car)
    public void RegisterCarOnClick(){
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        sentList = Select.from(Vehicle.class)
                .where(Condition.prop("STATUS").eq(Status.SENT))
                .list();

        notSendList = Select.from(Vehicle.class)
                .where(Condition.prop("STATUS").eq(Status.NOTSEND))
                .list();

        mNotSendCar.setTitle("Not send vehicle list (" + notSendList.size() + ")");
        mSentCar.setTitle("Sent vehicle list (" + sentList.size() + ")");
    }

    private void initializeEvents() {
        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        // Home
        mHome.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });


        // Scan IU
        mScanIU.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, IUScanningActivity.class);
                startActivity(intent);
                return false;
            }
        });

        // View Registration
        mNotSendCar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, ViewRegistrationActivity.class);
                intent.putExtra(ViewRegistrationActivity.VIEW_TYPE, "NOTSEND");
                startActivity(intent);
                return false;
            }
        });

        mSentCar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, ViewRegistrationActivity.class);
                intent.putExtra(ViewRegistrationActivity.VIEW_TYPE, "SENT");
                startActivity(intent);
                return false;
            }
        });

        // My Profile
        mProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, MyProfileActivity.class));
                return false;
            }
        });


        // Register Car
        mRegister.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                return false;
            }
        });


        // Log out
        mLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return false;
            }
        });


    }

}
