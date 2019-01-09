package com.info121.vms.activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info121.vms.App;

import com.info121.vms.R;
import com.info121.vms.utilities.PrefDB;
import com.info121.vms.utilities.Utils;
import com.info121.vms.models.UserRes;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.et_mobile_no)
    EditText mMobileNo;

    @BindView(R.id.et_password)
    EditText mPassword;

    @BindView(R.id.login_background)
    LinearLayout mLoginBackground;

    @BindView(R.id.ui_version)
    TextView mVersion;

    PrefDB prefDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        prefDB = new PrefDB(getApplicationContext());

        //TODO: remove after testing
        mMobileNo.setText("Online User 1");
        mPassword.setText("111");


        Drawable loginDrawable = Utils.getDrawable("LoginPhoto.jpg");
        if (loginDrawable != null) {
            mLoginBackground.setBackground(loginDrawable);
        }


 //       checkForUpdates();

        mVersion.setText("Ver " + Utils.getVersionCode(LoginActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();

   //     checkForCrashes();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterManagers();
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


    @OnClick(R.id.btn_login)
    public void btn_login_onClick() {
        Log.e("Login : ", "Login");

        List<UserRes> userList = Select.from(UserRes.class)
                .where(Condition.prop("USERNAME").eq(mMobileNo.getText().toString().trim()),
                        Condition.prop("USERPASSWORD").eq(mPassword.getText().toString().trim()))
                .list();

        if (userList.size() > 0) {
            prefDB.putString("USER_NAME", mMobileNo.getText().toString().trim());
            App.User_Name = mMobileNo.getText().toString().trim();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }

    }

    @OnClick(R.id.btn_setting)
    public void btn_setting_onClick() {
        startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
    }

    @OnClick(R.id.btn_register)
    public void btn_register_onClick() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
