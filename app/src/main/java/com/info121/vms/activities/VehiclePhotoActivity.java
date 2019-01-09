package com.info121.vms.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.EditText;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.info121.vms.R;
import com.info121.vms.utilities.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VehiclePhotoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Context mContext;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

//    @BindView(R.id.scan_vehicle_photo)
//    ImageView mScanVehiclePhoto;


    @BindView(R.id.et_vehicle_no)
    EditText mVehicleNo;


    @BindView(R.id.imageView)
    SubsamplingScaleImageView imageView;


//    public boolean onTouchEvent(MotionEvent motionEvent) {
////        mScaleGestureDetector.onTouchEvent(motionEvent);
////        return true;
////    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_photo);

        ButterKnife.bind(this);

        initializeControls();

     //   mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


      //  SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(R.id.imageView);

    }


    private void initializeControls() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = VehiclePhotoActivity.this;

        mToolbar.setTitle("Vehicle Photo");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String scanVehiclePhotoURL = getIntent().getStringExtra("SCAN_PHOTO_URL");
        String scanVehicleNo = getIntent().getStringExtra("SCAN_VEHICLE_NO");

        if (!Utils.isNullOrEmpty(scanVehiclePhotoURL)){

            Picasso.get().load(scanVehiclePhotoURL).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImage(ImageSource.bitmap(bitmap));

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });


            mVehicleNo.setText(scanVehicleNo);



        }

    }

    @OnClick(R.id.btn_ok)
    public void btnOKOnClick(){
        closePhotoActivity();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            closePhotoActivity();


//            if (!Utils.isNullOrEmpty(getIntent().getStringExtra(ID))) {
//                EventBus.getDefault().post("VEHICLE_DATA_UPDATE");
//            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private  void closePhotoActivity(){
        Intent intent = new Intent();
        intent.putExtra("VEHICLE_NO", mVehicleNo.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//            mScaleFactor = Math.max(0.1f,
//                    Math.min(mScaleFactor, 10.0f));
//
//            mScanVehiclePhoto.setScaleX(mScaleFactor);
//            mScanVehiclePhoto.setScaleY(mScaleFactor);
//            return true;
//        }
//    }
}
