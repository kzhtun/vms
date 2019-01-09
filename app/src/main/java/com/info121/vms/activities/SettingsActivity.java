package com.info121.vms.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.info121.vms.R;
import com.info121.vms.utilities.PrefDB;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.et_current_ip)
    EditText mCurrentIP;

    @BindView(R.id.et_new_ip)
    EditText mNewIP;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    PrefDB prefDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        prefDB = new PrefDB(getApplicationContext());

        mToolbar.setTitle("Settings");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentIP.setText(prefDB.getString("CURRENT_IP"));
    }



    @OnClick(R.id.btn_update)
    public void BtnUpdateOnClick(){
        prefDB.putString("CURRENT_IP", mNewIP.getText().toString());


        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setMessage("New IP has been saved. Data synchronization process may take some time.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    startActivity(new Intent(SettingsActivity.this, SplashActivity.class));

                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

//            if (!Utils.isNullOrEmpty(getIntent().getStringExtra(ID))) {
//                EventBus.getDefault().post("VEHICLE_DATA_UPDATE");
//            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
