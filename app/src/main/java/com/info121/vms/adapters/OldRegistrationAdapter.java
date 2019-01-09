package com.info121.vms.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.vms.App;
import com.info121.vms.R;
import com.info121.vms.utilities.FTPHelper;
import com.info121.vms.utilities.Utils;
import com.info121.vms.activities.RegisterActivity;
import com.info121.vms.api.APIClient;
import com.info121.vms.models.Vehicle;
import com.info121.vms.models.VehicleSaveReq;

import java.io.File;
import java.util.List;

/**
 * Created by KZHTUN on 2/22/2018.
 */

public class OldRegistrationAdapter extends RecyclerView.Adapter<OldRegistrationAdapter.ViewHolder> {

    private static final String TAG = "Registration Adapter";

    private FTPHelper ftpclient = null;
    private ProgressDialog pd;
    private String[] fileList;

    private static final int REQ_VEHICLE_PHOTO = 2010;
    private static final int REQ_DRIVER_PHOTO = 2011;
    private static final int REQ_PHOTO_1 = 2001;
    private static final int REQ_PHOTO_2 = 2002;
    private static final int REQ_PHOTO_3 = 2003;
    private static final int REQ_PHOTO_4 = 2004;
    private static final int REQ_PHOTO_5 = 2005;
    private static final int REQ_PHOTO_6 = 2006;

    boolean ftpConnect = false;
    String mType = "";

    static Context mContext;
    List<Vehicle> mVehicles;

    Boolean vehicleUploaded = false;
    Boolean driverUploaded = false;
    Boolean photo1Uploaded = false;


    Vehicle tempVehicle = new Vehicle();


    public OldRegistrationAdapter(List<Vehicle> vehicles, Context context, String type) {
        //   EventBus.getDefault().register(this);
        mVehicles = vehicles;
        mContext = context;
        mType = type;

        ftpclient = new FTPHelper();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View promotionView = inflater.inflate(R.layout.card_vehicle, parent, false);

        checkFTPConnection();

        // Return a new holder instance
        return new ViewHolder(promotionView);
    }

    private void checkFTPConnection() {
        if (isOnline(mContext)) {
            if (!ftpConnect)
                connectToFTPAddress();
        } else {
            Toast.makeText(mContext,
                    "FTP server is not connect.\nPlease check your internet connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {

        holder.mobileNo.setText(mVehicles.get(i).getMobileNo());
        holder.vehicleNo.setText(mVehicles.get(i).getVehicleNo());
        holder.status.setText(mVehicles.get(i).getStatus().toString());

        holder.datetime.setText(Utils.convertDateToString(mVehicles.get(i).getCreateDate(), "dd-MMM-yyyy hh:mm a"));
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + mVehicles.get(i).getPhotoVehicle());
        Uri imageUri = Uri.fromFile(file);

        holder.vehiclePhoto.setImageDrawable(getPhotoUri(mVehicles.get(i).getPhotoVehicle()));

        final Vehicle v = mVehicles.get(i);
        tempVehicle = v;

        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ftpConnect)
                    connectToFTPAddress();


                if (ftpConnect) {
                    if (Utils.isNullOrEmpty(v.getPhotoVehicle())) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

                        alertDialog.setTitle("Info");
                        alertDialog.setMessage("Please take vehicle photos and try again.");

                        // On pressing Settings button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    } else {
                        if (!Utils.isNullOrEmpty(v.getPhotoVehicle()))
                            uploadPhoto(tempVehicle.getPhotoVehicle());
                    }
                }

            }
        });

        final Intent intent = new Intent(mContext, RegisterActivity.class);
        intent.putExtra(RegisterActivity.ID, v.getId().toString());

        if (mType.equalsIgnoreCase("SENT")) {
            holder.edit.setText("VIEW");
            holder.send.setVisibility(View.GONE);
            intent.putExtra(RegisterActivity.MODE, "VIEW");
        } else {
            intent.putExtra(RegisterActivity.MODE, "EDIT");

        }


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mContext.startActivity(intent);
            }
        });
    }


    private void registerVehicle(Vehicle v) {

        VehicleSaveReq request = new VehicleSaveReq();

      //  pd = ProgressDialog.show(mContext, "", "Registering Vehicle ...", true, false);

        request.setCarinoutsrNo(v.getEntryId().toString());
        request.setUuid(v.getId().toString());
        request.setCarno(v.getVehicleNo());
        request.setMobileno(v.getMobileNo());
        request.setName(v.getName());
        request.setVisittype(v.getVisitType());
        request.setPurpose(v.getPurpose());
        request.setUnitno(v.getUnitNo());
        request.setResident(v.getResident());
        request.setEntrydatetime(Utils.convertDateToString(v.getCreateDate(), "yyyyMMdd HH:mm:ss"));
        request.setRmks(v.getRemarks());
        request.setUsername(v.getCreateBy());

        request.setCarphoto(v.getPhotoVehicle());
        request.setDrvrphoto(v.getPhotoDriver());
        request.setOtherphoto1(v.getPhoto1());
        request.setOtherphoto2(v.getPhoto2());
        request.setOtherphoto3(v.getPhoto3());
        request.setOtherphoto4(v.getPhoto4());
        request.setOtherphoto5(v.getPhoto5());
        request.setOtherphoto6(v.getPhoto6());

        APIClient.SaveVehicle(request);

    }


    private Drawable getPhotoUri(String photoName) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + photoName);

        return Drawable.createFromPath(file.getAbsolutePath());

    }

    @Override
    public int getItemCount() {
        return mVehicles.size();
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        // EventBus.getDefault().unregister(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mobileNo, vehicleNo, status, datetime;
        ImageView vehiclePhoto;

        Button edit, send;

        public ViewHolder(View itemView) {
            super(itemView);

            mobileNo = (TextView) itemView.findViewById(R.id.tv_mobile_no);
            vehicleNo = (TextView) itemView.findViewById(R.id.tv_vehicle_no);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            datetime = (TextView) itemView.findViewById(R.id.tv_date);

            edit = (Button) itemView.findViewById(R.id.btn_edit);
            send = (Button) itemView.findViewById(R.id.btn_send);

            vehiclePhoto = (ImageView) itemView.findViewById(R.id.vehicle_photo);

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

    private void uploadPhoto(final String fileName) {
        final int fileType = Integer.parseInt(fileName.split("_")[1]);

        String photoTypeMsg = "";

        if (fileType == REQ_VEHICLE_PHOTO)
            photoTypeMsg = "Vehicle Photo Uploading ...";

        if (fileType == REQ_DRIVER_PHOTO)
            photoTypeMsg = "Driver Photo Uploading ...";

        if (fileType == REQ_PHOTO_1)
            photoTypeMsg = "Additional Photo Uploading ...";


        pd = ProgressDialog.show(mContext, "", photoTypeMsg, true, false);

        new Thread(new Runnable() {
            public void run() {
                boolean status = false;

                ftpclient.ftpRemoveFile("/" + App.CONST_FTP_DIR + "/" + fileName);

                status = ftpclient.ftpUpload(
                        Environment.getExternalStorageDirectory()
                                + "/vcms/" + fileName, "/" + App.CONST_FTP_DIR + "/" + fileName, "/", mContext);

                if (status) {
                    Log.d(TAG, "Upload success");

                    handler.sendEmptyMessage(fileType);
                } else {
                    Log.d(TAG, "Upload failed");
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }


    

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }


            switch (msg.what) {
                case 0:

                    getFTPFileList();
                    break;
                case 1:

                    Toast.makeText(mContext, "FTP Connect Successfully!", Toast.LENGTH_LONG).show();

                    break;
                case 3:
                    Toast.makeText(mContext, "Disconnected Successfully!", Toast.LENGTH_LONG).show();
                    break;

                case REQ_VEHICLE_PHOTO:
                    if (!Utils.isNullOrEmpty(tempVehicle.getPhotoDriver()))
                        uploadPhoto(tempVehicle.getPhotoDriver());
                    else if (!Utils.isNullOrEmpty(tempVehicle.getPhoto1()))
                        uploadPhoto(tempVehicle.getPhoto1());
                    else
                        registerVehicle(tempVehicle);
                    break;

                case REQ_DRIVER_PHOTO:
                    if (!Utils.isNullOrEmpty(tempVehicle.getPhoto1()))
                        uploadPhoto(tempVehicle.getPhoto1());
                    else
                        registerVehicle(tempVehicle);

                    break;

                case REQ_PHOTO_1:

                    registerVehicle(tempVehicle);
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    }, 3000);

                    break;

                default:
                    Toast.makeText(mContext, "FTP server is not connect.\nPlease check your internet connection!", Toast.LENGTH_LONG).show();

            }

//            if (msg.what == 0) {
//                getFTPFileList();
//            } else if (msg.what == 1) {
//                // showCustomDialog(fileList);
//                Toast.makeText(mContext, "FTP Connect Successfully!",
//                        Toast.LENGTH_LONG).show();
//            } else if (msg.what == 2) {
//                Toast.makeText(mContext, "Uploaded Successfully!",
//                        Toast.LENGTH_LONG).show();
//            } else if (msg.what == 3) {
//                Toast.makeText(mContext, "Disconnected Successfully!",
//                        Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(mContext, "Unable to Perform Action!",
//                        Toast.LENGTH_LONG).show();
//            }

        }

    };

//    private void showCustomDialog(String[] fileList) {
//        // custom dialog
//        final Dialog dialog = new Dialog(mContext);
//        dialog.setContentView(R.layout.custom);
//        dialog.setTitle("/ Directory File List");
//
//        TextView tvHeading = (TextView) dialog.findViewById(R.id.tvListHeading);
//        tvHeading.setText(":: File List ::");
//
//        if (fileList != null && fileList.length > 0) {
//            ListView listView = (ListView) dialog
//                    .findViewById(R.id.lstItemList);
//            ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(
//                    this, android.R.layout.simple_list_item_1, fileList);
//            listView.setAdapter(fileListAdapter);
//        } else {
//            tvHeading.setText(":: No Files ::");
//        }
//
//        Button dialogButton = (Button) dialog.findViewById(R.id.btnOK);
//        // if button is clicked, close the custom dialog
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

    private void getFTPFileList() {
        // pd = ProgressDialog.show(mContext, "", "Getting Files...",                true, false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                fileList = ftpclient.ftpPrintFilesList("/");
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void connectToFTPAddress() {

        final String host = App.CONST_FTP_URL;
        final String username = App.CONST_FTP_USER;
        final String password = App.CONST_FTP_PASSWORD;

        if (host.length() < 1) {
            Toast.makeText(mContext, "Please Enter Host Address!",
                    Toast.LENGTH_LONG).show();
        } else if (username.length() < 1) {
            Toast.makeText(mContext, "Please Enter User Name!",
                    Toast.LENGTH_LONG).show();
        } else if (password.length() < 1) {
            Toast.makeText(mContext, "Please Enter Password!",
                    Toast.LENGTH_LONG).show();
        } else {

            // pd = ProgressDialog.show(mContext, "", "Connecting...",                    true, false);

            new Thread(new Runnable() {
                public void run() {

                    ftpConnect = ftpclient.ftpConnect(host, username, password, 21);
                    if (ftpConnect == true) {
                        Log.d(TAG, "Connection Success");
                        handler.sendEmptyMessage(0);
                    } else {
                        Log.d(TAG, "Connection failed");
                        handler.sendEmptyMessage(-1);
                    }
                }
            }).start();
        }
    }

}
