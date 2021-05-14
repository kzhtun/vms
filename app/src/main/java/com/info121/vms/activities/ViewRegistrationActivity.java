package com.info121.vms.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.info121.vms.AbstractActivity;
import com.info121.vms.R;
import com.info121.vms.adapters.RegistrationAdapter;
import com.info121.vms.api.APIClient;
import com.info121.vms.models.Status;
import com.info121.vms.models.Vehicle;
import com.info121.vms.models.VehicleSaveRes;
import com.info121.vms.utilities.Utils;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ViewRegistrationActivity extends AbstractActivity {

    public static final String VIEW_TYPE = "VIEW_TYPE";

    private ProgressDialog pd;

    Boolean sendAll = false;
    int vehicleIndex = 0;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_registration)
    RecyclerView mRecyclerView;

    List<Vehicle> notSendList;
    List<Vehicle> sentList;

    RegistrationAdapter notSendAdapter;
    RegistrationAdapter sentAdapter;

    int itemCount = 0;
    int sentCount = 0;
    List<Vehicle> sendAllList;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_registration);

        // fillData();

        ButterKnife.bind(this);

        loadList();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


//    private void fillData() {
//
//        for (int i = 0; i < 15; i++) {
//            Vehicle vehicle = new Vehicle();
//
//            vehicle.setUuid(UUID.randomUUID().toString());
//            vehicle.setVehicleNo(getSaltNumber());
//            vehicle.setMobileNo(getSaltNumber());
//            vehicle.setName(getSaltString());
//            vehicle.setVisitType(getSaltString());
//
//            vehicle.setUnitNo(getSaltNumber());
//            vehicle.setResident(getSaltNumber());
//            vehicle.setPurpose(getSaltString());
//            vehicle.setRemarks(getSaltString());
//
//            // vehicle.setCreateDate(Utils.convertDateToString(new Date(), "yyyyMMdd hh:mm:ss"));
//            vehicle.setCreateBy("KZHTUN");
//
//            vehicle.save();
//
//        }
//    }

//    public static String getSaltNumber() {
//        String SALTCHARS = "1234567890";
//        StringBuilder salt = new StringBuilder();
//        Random rnd = new Random();
//        while (salt.length() < 8) { // length of the random string.
//            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
//            salt.append(SALTCHARS.charAt(index));
//        }
//        String saltStr = salt.toString();
//        return saltStr;
//
//    }
//
//
//    public static String getSaltString() {
//        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        StringBuilder salt = new StringBuilder();
//        Random rnd = new Random();
//        while (salt.length() < 4) { // length of the random string.
//            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
//            salt.append(SALTCHARS.charAt(index));
//        }
//        String saltStr = salt.toString();
//        return saltStr;
//
//    }

    @Subscribe
    public void onEvent(Vehicle v) {
        sendAll = false;


        if (!isOnline(ViewRegistrationActivity.this)) {
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

        alertDialog = new AlertDialog.Builder(ViewRegistrationActivity.this)
                .setMessage("Initializing...")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();

        alertDialog.setCancelable(false);
        alertDialog.show();

        sendVehicleInfo(v);

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


    private void sendVehicleInfo(Vehicle v) {

        if (sendAll) {
            if (itemCount == 0) return;
            alertDialog.setMessage("Sending " + (vehicleIndex + 1) + " of " + itemCount);
        } else {
            alertDialog.setMessage("Sending ...");
        }

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

            notSendList = Select.from(Vehicle.class)
                    .where(Condition.prop("STATUS").eq(Status.NOTSEND))
                    .list();

            notSendAdapter = new RegistrationAdapter(notSendList, ViewRegistrationActivity.this, "NOTSEND");
            mRecyclerView.setAdapter(notSendAdapter);
            notSendAdapter.notifyDataSetChanged();

            mToolbar.setTitle("Not Send Vehicle List (" + notSendList.size() + ")");
            // send all
            if (sendAll) {
                if (vehicleIndex == sendAllList.size() - 1) {
                    alertDialog.dismiss();

                    alertDialog = new AlertDialog.Builder(ViewRegistrationActivity.this)
                            .setMessage("Record(s) Uploaded to server. If some records still remain, please send all again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    vehicleIndex++;
                    sendVehicleInfo(sendAllList.get(vehicleIndex));
                }

            } else {
                // send individual
                alertDialog.dismiss();

                alertDialog = new AlertDialog.Builder(ViewRegistrationActivity.this)
                        .setMessage("Record successfully uploaded to server.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }


            //   showMessage();

            Log.e("KZHTUN", itemCount + " : " + sentCount);

        }

    }


//    private void showMessage() {
//        if (itemCount == sentCount) {
//            final AlertDialog alertDialog = new AlertDialog.Builder(ViewRegistrationActivity.this)
//                    .setMessage("Record(s) Uploaded to server. If some records still remain, please send all again"
//                    )
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).create();
//            alertDialog.setCancelable(false);
//            alertDialog.show();
//        }
//    }

    private MultipartBody.Part getFileRequestBody(String fileName, String tagName) {
        if (Utils.isNullOrEmpty(fileName)) return null;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + fileName);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        return MultipartBody.Part.createFormData(tagName, file.getName(), requestFile);

    }


    private RequestBody getTextRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadList();

    }


    private void loadList() {
        notSendList = Select.from(Vehicle.class)
                .where(Condition.prop("STATUS").eq(Status.NOTSEND))
                .orderBy("CREATE_DATE DESC")
                .list();

        sentList = Select.from(Vehicle.class)
                .where(Condition.prop("STATUS").eq(Status.SENT))
                .orderBy("CREATE_DATE DESC")
                .list();

        if (getIntent().getStringExtra(VIEW_TYPE).equals("NOTSEND")) {
            mToolbar.setTitle("Not Send Vehicle List (" + notSendList.size() + ")");
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(ViewRegistrationActivity.this, LinearLayoutManager.VERTICAL, false));
            notSendAdapter = new RegistrationAdapter(notSendList, ViewRegistrationActivity.this, "NOTSEND");
            mRecyclerView.setAdapter(notSendAdapter);
        }

        // SENT VEHICLE LIST
        if (getIntent().getStringExtra(VIEW_TYPE).equals("SENT")) {
            mToolbar.setTitle("Sent Vehicle List (" + sentList.size() + ")");
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(ViewRegistrationActivity.this, LinearLayoutManager.VERTICAL, false));
            sentAdapter = new RegistrationAdapter(sentList, ViewRegistrationActivity.this, "SENT");
            mRecyclerView.setAdapter(sentAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra(VIEW_TYPE).equals("NOTSEND")) {
            MenuItemCompat.setShowAsAction(menu.add(Menu.NONE, 0, Menu.NONE, "Send All")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (notSendList.size() > 0)
                                sendAll();
                            else
                                showEmptyListDialog();
                            return true;
                        }
                    }), MenuItem.SHOW_AS_ACTION_ALWAYS);


        }
        return super.onCreateOptionsMenu(menu);
    }

    public void showEmptyListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There is no item to send")
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
    }


    public void sendAll() {
        sendAll = true;
        vehicleIndex = 0;
        sendAllList = notSendList;
        itemCount = sendAllList.size();



        alertDialog = new AlertDialog.Builder(ViewRegistrationActivity.this)
                .setMessage("Initializing...")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        vehicleIndex = sendAllList.size() - 1;
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        sendVehicleInfo(sendAllList.get(vehicleIndex));
    }

    @Subscribe
    public void onEvent(Throwable t) {

        final AlertDialog alertDialog = new AlertDialog.Builder(ViewRegistrationActivity.this)
                .setMessage(t.getMessage())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();


    }
}
