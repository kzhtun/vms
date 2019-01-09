package com.info121.vms.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.EditText;

import com.info121.vms.AbstractActivity;

import com.info121.vms.R;
import com.info121.vms.models.ScanResult;
import com.info121.vms.utilities.PrefDB;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IUScanningActivity extends AbstractActivity {
    private Context mContext;
    PrefDB prefDB;

    private Toolbar mToolbar;
    private ProgressDialog pd;


    @BindView(R.id.et_vehicle_no)
    EditText mVehicleNo;

    @BindView(R.id.et_iu_no)
    EditText mIUNo;

    @BindView(R.id.et_serial_no)
    EditText mSerialNo;

    @BindView(R.id.et_date_time)
    EditText mDateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iuscanning);

        ButterKnife.bind(this);

        initializeControls();

    }

    private void initializeControls() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mContext = IUScanningActivity.this;
        prefDB = new PrefDB(getApplicationContext());

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @OnClick(R.id.btn_scan)
    public void btnScanOnClick() {
        pd = ProgressDialog.show(mContext, "", "Scanning ...", true, false);
     //   APIClient.GetIUScan();
    }


    @Subscribe
    public void onEvent(ScanResult res) {
        if (res != null) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }


            mVehicleNo.setText(res.getCarnumber());
            mIUNo.setText(res.getIunumber());
            mSerialNo.setText(res.getSerialno());
            mDateTime.setText(res.getEntrydatetime());
        }
    }

    @Subscribe
    public void onEvent(Throwable t) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setMessage(t.getMessage())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

}
