package com.info121.vms.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.info121.vms.AbstractActivity;
import com.info121.vms.App;
import com.info121.vms.R;
import com.info121.vms.api.APIClient;
import com.info121.vms.models.ScanResult;
import com.info121.vms.models.Status;
import com.info121.vms.models.VehicleSaveRes;
import com.info121.vms.utilities.PrefDB;
import com.info121.vms.utilities.Utils;
import com.info121.vms.models.Block;
import com.info121.vms.models.Purpose;
import com.info121.vms.models.Storey;
import com.info121.vms.models.UnitNo;
import com.info121.vms.models.Vehicle;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RegisterActivity extends AbstractActivity {

    private Context mContext;
    PrefDB prefDB;

    private ProgressDialog pd;
    private String scanVehiclePhotoURL;
    private String scanVehicleNo;

    final Handler handler = new Handler();
    int delay = 1000;

    public static final String ID = "ID";
    public static final String MODE = "";

    private static final int REQ_VEHICLE_PHOTO = 2010;
    private static final int REQ_DRIVER_PHOTO = 2011;
    private static final int REQ_PHOTO_1 = 2001;
    private static final int REQ_PHOTO_2 = 2002;
    private static final int REQ_PHOTO_3 = 2003;
    private static final int REQ_PHOTO_4 = 2004;
    private static final int REQ_PHOTO_5 = 2005;
    private static final int REQ_PHOTO_6 = 2006;


    private static final int REQ_VEHICLE_NO = 3000;


    String filename;
    Uri imageUri;

    ContentValues values;

    ArrayAdapter<UnitNo> unitAdapter;
    ArrayAdapter<Block> blockAdapter;
    ArrayAdapter<Storey> storeyAdapter;
    ArrayAdapter<Purpose> purposeAdapter;


    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private Integer id;
    private Vehicle vehicle;


    @BindView(R.id.vehicle_photo)
    ImageView mVehiclePhoto;

    @BindView(R.id.driver_photo)
    ImageView mDriverPhoto;


    @BindView(R.id.photo_1)
    ImageView mPhoto1;

    @BindView(R.id.photo_2)
    ImageView mPhoto2;

    @BindView(R.id.photo_3)
    ImageView mPhoto3;

    @BindView(R.id.photo_4)
    ImageView mPhoto4;

    @BindView(R.id.photo_5)
    ImageView mPhoto5;

    @BindView(R.id.photo_6)
    ImageView mPhoto6;

    @BindView(R.id.et_vehicle_no)
    EditText mVehicleNo;

    @BindView(R.id.et_mobile_no)
    EditText mMobileNo;

    @BindView(R.id.et_name)
    EditText mName;

    @BindView(R.id.et_visit_type)
    EditText mVisitType;

    @BindView(R.id.sp_purpose)
    Spinner mPurpose;

    @BindView(R.id.sp_block)
    Spinner mBlock;

    @BindView(R.id.sp_storey)
    Spinner mStorey;

    @BindView(R.id.sp_unit)
    Spinner mUnit;

    @BindView(R.id.et_resident)
    EditText mResident;

    @BindView(R.id.et_remarks)
    EditText mRemarks;

    @BindView(R.id.et_entry_id)
    EditText mEntryID;

    @BindView(R.id.main_scroll_view)
    ScrollView mMainScrollView;

    @BindView(R.id.date_time)
    TextView mDateTime;

    @BindView(R.id.btn_save)
    Button mSave;

    @BindView(R.id.vehicle_delete)
    Button mVehicleDelete;

    @BindView(R.id.driver_delete)
    Button mDriverDelete;

    @BindView(R.id.photo1_delete)
    Button mPhoto1Delete;

    @BindView(R.id.btn_call)
    Button mCall;

    @BindView(R.id.iu_number)
    TextView mIuNumber;

    @BindView(R.id.entry_date_time)
    TextView mEntryDateTime;

    @BindView(R.id.scan_vehicle_photo)
    ImageView mScanVehiclePhoto;

    @BindView(R.id.switch_lane)
    Switch mSwitchLane;

    @BindView(R.id.welcome)
    TextView mWelcomeMsg;


    @BindView(R.id.layout_scan)
    LinearLayout mLayoutScan;

    @BindView(R.id.pg_scan_vehicle_photo)
    ProgressBar mPgScanVehiclePhoto;


    Boolean withPhoto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        initializeControls();
        populatePurpose();
        populateBlock();

        if (!Utils.isNullOrEmpty(getIntent().getStringExtra(ID))) {

            populateVehicle(Integer.parseInt(getIntent().getStringExtra(ID)));
            mCall.setVisibility(View.VISIBLE);
        } else {
            //   mToolbar.setTitle("New Vehicle Registration");
            mWelcomeMsg.setText("New Vehicle Registration");
            vehicle = new Vehicle();

            mCall.setVisibility(View.GONE);
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Handler timer = new Handler(getMainLooper());

        timer.postDelayed(new Runnable() {
            @Override
            public void run() {

                String dateString = Utils.convertDateToString(Calendar.getInstance().getTime(), "dd-MMM-yyyy hh:mm:ss a");

                mDateTime.setText(dateString.toString());
                timer.postDelayed(this, 1000);
            }
        }, 1000);

    }

    private void populateVehicle(int ID) {
        vehicle = Vehicle.findById(Vehicle.class, ID);


        mLayoutScan.setVisibility(View.VISIBLE);

        if (vehicle.getStatus().equals(Status.NOTSEND)) {
            // mToolbar.setTitle("Edit Vehicle Registration");
            mWelcomeMsg.setText("Edit Vehicle Registration");
            mToolbar.setBackgroundColor(getResources().getColor(R.color.edit_titlebar));
            mSave.setText("UPDATE");
            mLayoutScan.setVisibility(View.GONE);

        } else {
            //  mToolbar.setTitle("View Vehicle Registration");
            mWelcomeMsg.setText("View Vehicle Registration");
            mToolbar.setBackgroundColor(getResources().getColor(R.color.view_titlebar));
            mSave.setText("EXIT");
            mLayoutScan.setVisibility(View.GONE);
        }

        id = ID;

        mVehicleNo.setText(vehicle.getVehicleNo());
        mMobileNo.setText(vehicle.getMobileNo());
        mName.setText(vehicle.getName());
        mVisitType.setText(vehicle.getVisitType());
        mResident.setText(vehicle.getResident());
        mRemarks.setText(vehicle.getRemarks());
        mEntryID.setText(vehicle.getEntryId());


        for (int i = 0; i < blockAdapter.getCount(); i++) {
            if (blockAdapter.getItem(i).getBlock().equals(vehicle.getBlock())) {
                mBlock.setSelection(i);
                populateStorey(vehicle.getBlock());
                break;
            }
        }

        for (int i = 0; i < storeyAdapter.getCount(); i++) {
            if (storeyAdapter.getItem(i).getStorey().equals(vehicle.getStorey())) {
                mStorey.setSelection(i);
                populateUnit(vehicle.getBlock(), vehicle.getStorey());
                break;
            }
        }

        for (int i = 0; i < unitAdapter.getCount(); i++) {
            if (unitAdapter.getItem(i).getUnitno().equals(vehicle.getRoomNo())) {
                mUnit.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < purposeAdapter.getCount(); i++) {
            if (purposeAdapter.getItem(i).getPurposeofvisit().equals(vehicle.getPurpose())) {
                mPurpose.setSelection(i);
                break;
            }
        }


        if (!Utils.isNullOrEmpty(vehicle.getPhotoVehicle())) {
            mVehiclePhoto.setImageDrawable(getPhotoUri(vehicle.getPhotoVehicle()));
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhotoDriver())) {
            mDriverPhoto.setImageDrawable(getPhotoUri(vehicle.getPhotoDriver()));
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhoto1())) {
            mPhoto1.setImageDrawable(getPhotoUri(vehicle.getPhoto1()));
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhoto2())) {
            mPhoto2.setImageDrawable(getPhotoUri(vehicle.getPhoto2()));
            mPhoto2.setVisibility(View.VISIBLE);
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhoto3())) {
            mPhoto3.setImageDrawable(getPhotoUri(vehicle.getPhoto3()));
            mPhoto3.setVisibility(View.VISIBLE);
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhoto4())) {
            mPhoto4.setImageDrawable(getPhotoUri(vehicle.getPhoto4()));
            mPhoto4.setVisibility(View.VISIBLE);
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhoto5())) {
            mPhoto5.setImageDrawable(getPhotoUri(vehicle.getPhoto5()));
            mPhoto5.setVisibility(View.VISIBLE);
        }

        if (!Utils.isNullOrEmpty(vehicle.getPhoto6())) {
            mPhoto6.setImageDrawable(getPhotoUri(vehicle.getPhoto6()));
            mPhoto6.setVisibility(View.VISIBLE);
        }

        checkMode();

    }


    private void checkMode() {
        if (getIntent().getStringExtra(MODE).equalsIgnoreCase("VIEW")) {
            mVehiclePhoto.setEnabled(false);
            mDriverPhoto.setEnabled(false);
            mPhoto1.setEnabled(false);

            mVehicleNo.setEnabled(false);
            mMobileNo.setEnabled(false);
            mName.setEnabled(false);

            mBlock.setEnabled(false);
            mStorey.setEnabled(false);
            mUnit.setEnabled(false);

            mResident.setEnabled(false);
            mPurpose.setEnabled(false);
            mRemarks.setEnabled(false);

            mEntryID.setEnabled(false);

        }
    }


    private Drawable getPhotoUri(String photoName) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + photoName);

        return Drawable.createFromPath(file.getAbsolutePath());

    }

    private void initializeControls() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        mContext = RegisterActivity.this;
        prefDB = new PrefDB(getApplicationContext());

        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(RegisterActivity.this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mVehicleNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.hideSoftKeyboard(this);
    }

    private void populatePurpose() {
        List<Purpose> purposes = Purpose.listAll(Purpose.class);

        purposeAdapter = new ArrayAdapter<Purpose>(mContext, R.layout.support_simple_spinner_dropdown_item, purposes);
        mPurpose.setAdapter(purposeAdapter);
    }

    private void populateBlock() {
        List<Block> blocks = Block.listAll(Block.class);

        blockAdapter = new ArrayAdapter<Block>(mContext, R.layout.support_simple_spinner_dropdown_item, blocks);
        mBlock.setAdapter(blockAdapter);
    }

    private void populateStorey(String block) {
        List<Storey> storeys = Select.from(Storey.class)
                .where(Condition.prop("block").eq(block))
                .list();

        storeyAdapter = new ArrayAdapter<Storey>(mContext, R.layout.support_simple_spinner_dropdown_item, storeys);
        mStorey.setAdapter(storeyAdapter);
    }

    private void populateUnit(String block, String storey) {
        List<UnitNo> unitNos = Select.from(UnitNo.class)
                .where(Condition.prop("block").eq(block),
                        Condition.prop("storey").eq(storey))
                .list();

        unitAdapter = new ArrayAdapter<UnitNo>(mContext, R.layout.support_simple_spinner_dropdown_item, unitNos);
        mUnit.setAdapter(unitAdapter);
    }


    @OnClick(R.id.btn_call)
    public void btnCallOnClick() {
        Uri number = Uri.parse("tel:" + mMobileNo.getText());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);

        Toast.makeText(getApplicationContext(), "Phone Call .... to  " + mMobileNo.getText(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.vehicle_delete)
    public void vehicleDeleteOnClick() {
        vehicle.setPhotoVehicle("");
        mVehiclePhoto.setImageResource(R.mipmap.ic_camera_alt_black_48dp);
    }

    @OnClick(R.id.driver_delete)
    public void driverDeleteOnClick() {
        vehicle.setPhotoDriver("");
        mDriverPhoto.setImageResource(R.mipmap.ic_camera_alt_black_48dp);
    }

    @OnClick(R.id.photo1_delete)
    public void photo1DeleteOnClick() {
        vehicle.setPhoto1("");
        mPhoto1.setImageResource(R.mipmap.ic_camera_alt_black_48dp);
    }

    @OnItemSelected(R.id.sp_block)
    public void blockOnItemSelected() {
        populateStorey(mBlock.getSelectedItem().toString());
    }

    @OnItemSelected(R.id.sp_storey)
    public void storeyOnItemSelected() {
        populateUnit(mBlock.getSelectedItem().toString(), mStorey.getSelectedItem().toString());
    }


    @OnClick(R.id.vehicle_photo)
    public void vehiclePhotoOnClick() {
        openCamera(REQ_VEHICLE_PHOTO);
    }

    @OnClick(R.id.driver_photo)
    public void driverPhotoOnClick() {
        openCamera(REQ_DRIVER_PHOTO);
    }

    @OnClick(R.id.photo_1)
    public void photo1onclick() {
        openCamera(REQ_PHOTO_1);
    }

    @OnClick(R.id.photo_2)
    public void photo2onclick() {
        openCamera(REQ_PHOTO_2);
    }

    @OnClick(R.id.photo_3)
    public void photo3onclick() {
        openCamera(REQ_PHOTO_3);
    }

    @OnClick(R.id.photo_4)
    public void photo4onclick() {
        openCamera(REQ_PHOTO_4);
    }

    @OnClick(R.id.photo_5)
    public void photo5onclick() {
        openCamera(REQ_PHOTO_5);
    }

    @OnClick(R.id.photo_6)
    public void photo6onclick() {
        openCamera(REQ_PHOTO_6);
    }


    @OnClick(R.id.btn_cancel)
    public void scanCancel() {
        clearAllFields();
    }

    @OnClick(R.id.scan_vehicle_photo)
    public void scanVehiclePhotoOnClick() {
        Intent intent = new Intent(RegisterActivity.this, VehiclePhotoActivity.class);
        intent.putExtra("SCAN_PHOTO_URL", scanVehiclePhotoURL);
        intent.putExtra("SCAN_VEHICLE_NO", scanVehicleNo);

        startActivityForResult(intent, REQ_VEHICLE_NO);


    }


    @OnClick(R.id.switch_lane)
    public void SwitchLaneOnClick() {
        clearAllFields();
    }


    private void scanVehicleData() {
        if (!isOnline(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unable to connect to server. Please check your internet connection.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })

                    .setTitle("Alert!")
                    .setIcon(R.mipmap.check_circle_correct_mark_success_tick_yes_icon_64);
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        pd = ProgressDialog.show(mContext, "", "Scanning ...", true, false);


        if (withPhoto) {
            mPgScanVehiclePhoto.setVisibility(View.VISIBLE);
            mScanVehiclePhoto.setVisibility(View.GONE);
        }

        String lane;
        if (mSwitchLane.isChecked())
            lane = "resident";
        else
            lane = "visitor";

        APIClient.GetIUScan(lane);
    }


    @OnClick(R.id.btn_scan_no_photo)
    public void btnScanNoPhotoOnClick() {
        withPhoto = false;
        scanVehicleData();
    }

    @OnClick(R.id.btn_scan)
    public void btnScanOnClick() {
        withPhoto = true;
        scanVehicleData();
    }


    @Subscribe
    public void onEvent(ScanResult res) throws IOException {
        if (res != null) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }


            if (res.getIunumber().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("There is no scan vehicle data.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })

                        .setTitle("Scan Result")
                        .setIcon(R.mipmap.check_circle_correct_mark_success_tick_yes_icon_64);
                AlertDialog alert = builder.create();
                alert.show();
                clearAllFields();
                return;
            }

            vehicle = new Vehicle();

            //  mVehicleNo.setText(res.getCarnumber());
            mIuNumber.setText(res.getIunumber());
            //  mSerialNo.setText(res.getSerialno());
            // 8/14/2018 1:01:26 PM
            Date longDate = Utils.convertDateStringToDate(res.getEntrydatetime(), "MM/dd/yyyy hh:mm:ss a");

            mEntryDateTime.setText(Utils.convertDateToString(longDate, "dd/MM/yyyy hh:mm"));
            mName.setText(res.getDrivername());


            mVehicleNo.setText(res.getCarnumber());
            scanVehicleNo = res.getCarnumber();
            //mMobileNo.setText(vehicle.getMobileNo());
            mName.setText(res.getDrivername());
            mVisitType.setText(vehicle.getVisitType());
            mResident.setText(res.getResident());

            mEntryID.setText(res.getSerialno());


            for (int i = 0; i < blockAdapter.getCount(); i++) {
                if (blockAdapter.getItem(i).getBlock().equals(vehicle.getBlock())) {
                    mBlock.setSelection(i);
                    populateStorey(res.getBlockno());
                    break;
                }
            }

            for (int i = 0; i < storeyAdapter.getCount(); i++) {
                if (storeyAdapter.getItem(i).getStorey().equals(vehicle.getStorey())) {
                    mStorey.setSelection(i);
                    populateUnit(res.getBlockno(), res.getStorey());
                    break;
                }
            }

            for (int i = 0; i < unitAdapter.getCount(); i++) {
                if (unitAdapter.getItem(i).getUnitno().equals(res.getRoomno())) {
                    mUnit.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < purposeAdapter.getCount(); i++) {
                if (purposeAdapter.getItem(i).getPurposeofvisit().equals(res.getPurposeofvisit())) {
                    mPurpose.setSelection(i);
                    break;
                }
            }


            if (withPhoto)
                if (!Utils.isNullOrEmpty(res.getCarphoto())) {
                    scanVehiclePhotoURL = App.CONST_PHOTO_DOWNLOAD_URL + "/" + res.getCarphoto();

                    mPgScanVehiclePhoto.setVisibility(View.GONE);
                    mScanVehiclePhoto.setVisibility(View.VISIBLE);

                    Picasso.get().load(scanVehiclePhotoURL).into(mScanVehiclePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVehiclePhoto.setImageDrawable(mScanVehiclePhoto.getDrawable());
                                }
                            }, delay);

                            String fileName = vehicle.getUuid() + "_2010_" + ".jpg";

                            savePhoto(((BitmapDrawable) mScanVehiclePhoto.getDrawable()).getBitmap(), fileName);
                            vehicle.setPhotoVehicle(fileName);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                } else {
                    mPgScanVehiclePhoto.setVisibility(View.GONE);
                    mScanVehiclePhoto.setVisibility(View.VISIBLE);
                    mScanVehiclePhoto.setImageDrawable(getResources().getDrawable(R.mipmap.no_photo));
                }

        }
    }


    void openCamera(final int requestCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, vehicle.getUuid() + requestCode);
                //    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, requestCode);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fileName = vehicle.getUuid() + "_" + requestCode + "_" + ".jpg";

        if (resultCode != Activity.RESULT_OK) return;

        Bitmap photo = getPhoto();

        switch (requestCode) {
            case REQ_VEHICLE_PHOTO:

                mVehiclePhoto.setImageBitmap(photo);
                mVehiclePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                vehicle.setPhotoVehicle(fileName);

                // driverPhotoOnClick();
                break;

            case REQ_DRIVER_PHOTO:
                mDriverPhoto.setImageBitmap(photo);
                mDriverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                vehicle.setPhotoDriver(fileName);
                //  photo1onclick();
                break;

            case REQ_PHOTO_1:
                mPhoto1.setImageBitmap(photo);
                mPhoto1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // mPhoto2.setVisibility(View.VISIBLE);
                vehicle.setPhoto1(fileName);
                break;

            case REQ_VEHICLE_NO:

                mVehicleNo.setText(data.getStringExtra("VEHICLE_NO"));
                return;

            //break;

        }

        savePhoto(photo, fileName);

        File file = new File(getRealPathFromURI(imageUri));
        file.delete();

    }

    public Bitmap getPhoto() {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);


            ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(thumbnail, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(thumbnail, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(thumbnail, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = thumbnail;
            }

            return rotatedBitmap;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void savePhoto(Bitmap photoData, String fileName) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photoData.compress(Bitmap.CompressFormat.JPEG, 10, bytes);

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + fileName);
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);

            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        menu.add(Menu.NONE, 0, Menu.NONE, "Save ")
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        btnSaveOnClick();
//                        return true;
//                    }
//                })
//                // .setIcon(ContextCompat.getDrawable(RegisterActivity.this, R.mipmap.ic_done_white_24dp))
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//
//
//        return super.onCreateOptionsMenu(menu);
//    }


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


    @OnClick(R.id.btn_save)
    public void btnSaveOnClick() {

        if (mSave.getText().equals("EXIT")) {
            finish();
        } else if (validRegister()) {

            Utils.hideSoftKeyboard(this);
            //  FirebaseCrash.report(new Exception("Hide Soft Key"));

            if (!Utils.isNullOrEmpty(getIntent().getStringExtra(ID))) {
                vehicle.setId(Long.parseLong(getIntent().getStringExtra(ID)));
            }

            vehicle.setVehicleNo(mVehicleNo.getText().toString());
            vehicle.setMobileNo(mMobileNo.getText().toString());
            vehicle.setName(mName.getText().toString());
            vehicle.setVisitType(mVisitType.getText().toString());

            vehicle.setBlock(mBlock.getSelectedItem().toString());
            vehicle.setStorey(mStorey.getSelectedItem().toString());
            vehicle.setRoomNo(mUnit.getSelectedItem().toString());
            vehicle.setUnitNo(mBlock.getSelectedItem().toString() + mStorey.getSelectedItem().toString() + mUnit.getSelectedItem().toString());

            vehicle.setPurpose(mPurpose.getSelectedItem().toString());
            vehicle.setResident(mResident.getText().toString());
            vehicle.setRemarks(mRemarks.getText().toString());

            vehicle.setEntryId(mEntryID.getText().toString());


            vehicle.setCreateDate(Calendar.getInstance().getTime());
            vehicle.setCreateBy(prefDB.getString("USER_NAME"));

            vehicle.save();

            if (isNetworkOnline()) {
                sendVehicleInfo(vehicle);
            }

            vehicle = new Vehicle();

            mMainScrollView.fullScroll(View.FOCUS_UP);
            //  FirebaseCrash.report(new Exception("Scroll View Top"));


            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            Toast.makeText(mContext, "Record is successfully save to local.", Toast.LENGTH_SHORT).show();

            clearAllFields();

            if (mSave.getText().equals("UPDATE")) {
                finish();
            }

        }
    }

    private void sendVehicleInfo(Vehicle v) {

        pd = ProgressDialog.show(RegisterActivity.this, "", "Sending...", true, false);

        // update sent date
        final Vehicle vehicle = Vehicle.findById(Vehicle.class, Utils.getVehicleID(v.getUuid()));
        vehicle.setSentDate(Calendar.getInstance().getTime());
        vehicle.save();

        APIClient.SendItem(getTextRequestBody(v.getUuid()),
                getFileRequestBody(v.getPhotoVehicle(), "CarPhoto"),
                getFileRequestBody(v.getPhotoDriver(), "DriverPhoto"),
                getFileRequestBody(v.getPhoto1(), "OtherPhoto1"),
                getTextRequestBody(v.getEntryId()),
                getTextRequestBody(v.getVehicleNo()),
                getTextRequestBody(v.getMobileNo()),
                getTextRequestBody(v.getName()),
                getTextRequestBody(v.getVisitType()),
                getTextRequestBody(v.getPurpose()),
                getTextRequestBody(v.getUnitNo()),
                getTextRequestBody(v.getResident()),
                getTextRequestBody(v.getRemarks()),
                getTextRequestBody(v.getCreateBy()),
                getTextRequestBody(Utils.convertDateToString(v.getCreateDate(), "yyyyMMdd HH:mm:ss"))
        );

    }

    @Subscribe
    public void onEvent(VehicleSaveRes res) {
        if (res.getStatus().equalsIgnoreCase("SUCCESS")) {
            final Vehicle vehicle = Vehicle.findById(Vehicle.class, Utils.getVehicleID(res.getUuid()));
            vehicle.setStatus(Status.SENT);
            vehicle.save();

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Record successfully uploaded to server.")
                    .setCancelable(false)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })

                    .setTitle("Alert!")
                    .setIcon(R.mipmap.check_circle_correct_mark_success_tick_yes_icon_64);
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private MultipartBody.Part getFileRequestBody(String fileName, String tagName) {
        if (Utils.isNullOrEmpty(fileName)) return null;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + fileName);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        return MultipartBody.Part.createFormData(tagName, file.getName(), requestFile);

    }


    private RequestBody getTextRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


    private void clearAllFields() {
        //  FirebaseCrash.report(new Exception("Clear All Field"));

        mScanVehiclePhoto.setImageDrawable(getResources().getDrawable(R.mipmap.no_photo));

        mVehicleNo.setText("");
        mMobileNo.setText("");
        mName.setText("");
        mVisitType.setText("");
        mBlock.setSelection(0);

        mPurpose.setSelection(0);

        mResident.setText("");
        mRemarks.setText("");
        mEntryID.setText("");

        mDriverPhoto.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));
        mVehiclePhoto.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));

        mPhoto1.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));
        mPhoto2.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));
        mPhoto3.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));
        mPhoto4.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));
        mPhoto5.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));
        mPhoto6.setImageDrawable(getResources().getDrawable(R.mipmap.ic_camera_alt_black_48dp));

        mPhoto2.setVisibility(View.GONE);
        mPhoto3.setVisibility(View.GONE);
        mPhoto4.setVisibility(View.GONE);
        mPhoto5.setVisibility(View.GONE);
        mPhoto6.setVisibility(View.GONE);


        mPgScanVehiclePhoto.setVisibility(View.GONE);
        mScanVehiclePhoto.setVisibility(View.VISIBLE);

        mScanVehiclePhoto.setImageDrawable(getResources().getDrawable(R.mipmap.no_photo));
        mIuNumber.setText("-");
        mEntryDateTime.setText("-");

    }

    private boolean validRegister() {

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + vehicle.getPhotoVehicle());

//        if (!file.exists()) {
//            final AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
//                    .setMessage("Please take vehicle photo at least.")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).create();
//            alertDialog.setCancelable(false);
//            alertDialog.show();
//            return false;
//        }

        if (Utils.isNullOrEmpty(mVehicleNo.getText().toString())) {
            mVehicleNo.setError("Vehicle number should not be blank.");
            mVehicleNo.setFocusable(true);
            return false;
        }

//        if (Utils.isNullOrEmpty(mMobileNo.getText().toString())) {
//            mMobileNo.setError("Mobile number should not be blank.");
//            mMobileNo.setFocusable(true);
//            return false;
//        }

        return true;
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    @Subscribe
    public void onEvent(Throwable t) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(t.getMessage())
                .setCancelable(false)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })

                .setTitle("Alert!")
                .setIcon(R.mipmap.check_circle_correct_mark_success_tick_yes_icon_64);
        AlertDialog alert = builder.create();
        alert.show();

        mPgScanVehiclePhoto.setVisibility(View.GONE);
        mScanVehiclePhoto.setVisibility(View.VISIBLE);

        if (pd != null && pd.isShowing()) {
            pd.dismiss();


        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


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
            savePhoto(result, imageType + ".jpg");

        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
