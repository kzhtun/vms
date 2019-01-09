package com.info121.vms.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.info121.vms.R;
import com.info121.vms.utilities.FTPHelper;
import com.info121.vms.utilities.Utils;
import com.info121.vms.activities.RegisterActivity;
import com.info121.vms.models.Vehicle;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Created by KZHTUN on 2/22/2018.
 */

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.ViewHolder> {

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


    Vehicle tempVehicle = new Vehicle();


    public RegistrationAdapter(List<Vehicle> vehicles, Context context, String type) {

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

        // Return a new holder instance
        return new ViewHolder(promotionView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {

        SpannableString content = new SpannableString(mVehicles.get(i).getMobileNo());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.mobileNo.setText(content);
        holder.mobileNo.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));


      //  holder.mobileNo.setText(mVehicles.get(i).getMobileNo());
        holder.vehicleNo.setText(mVehicles.get(i).getVehicleNo());

        if(mVehicles.get(i).getStatus().toString().equals("NOTSEND")){
            holder.status.setText("NOT SEND");
        }

        holder.srno.setText(String.valueOf(i + 1));

        holder.datetime.setText(Utils.convertDateToString(mVehicles.get(i).getCreateDate(), "dd-MMM-yyyy hh:mm a"));

        if (mVehicles.get(i).getSentDate() != null)
            holder.sentDate.setText(Utils.convertDateToString(mVehicles.get(i).getSentDate(), "dd-MMM-yyyy \nhh:mm a"));

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "vms" + File.separator + mVehicles.get(i).getPhotoVehicle());
        Uri imageUri = Uri.fromFile(file);

        holder.vehiclePhoto.setImageDrawable(getPhotoUri(mVehicles.get(i).getPhotoVehicle()));
        holder.vehiclePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

        final Vehicle v = mVehicles.get(i);
        tempVehicle = v;

        final Intent intent = new Intent(mContext, RegisterActivity.class);
        intent.putExtra(RegisterActivity.ID, v.getId().toString());

        if (mType.equalsIgnoreCase("SENT")) {
            holder.edit.setText("VIEW");
            holder.send.setVisibility(View.GONE);
            intent.putExtra(RegisterActivity.MODE, "VIEW");
        } else {
            intent.putExtra(RegisterActivity.MODE, "EDIT");

            if( holder.sentDate.getText().length() > 0){
                holder.status.setText("Last Attempted Uploaded");
            }
        }


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(intent);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(intent);
            }
        });

        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(tempVehicle);
            }
        });

        holder.mobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("tel:" +  ((TextView) view).getText());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                mContext.startActivity(callIntent);

            }
        });


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
        TextView mobileNo, vehicleNo, status, datetime, srno, sentDate;
        CardView root;
        ImageView vehiclePhoto;

        Button edit, send;

        public ViewHolder(View itemView) {
            super(itemView);

            mobileNo = (TextView) itemView.findViewById(R.id.tv_mobile_no);
            vehicleNo = (TextView) itemView.findViewById(R.id.tv_vehicle_no);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            datetime = (TextView) itemView.findViewById(R.id.tv_date);
            sentDate = (TextView) itemView.findViewById(R.id.tv_sent_date);
            srno = (TextView) itemView.findViewById(R.id.tv_serial);

            edit = (Button) itemView.findViewById(R.id.btn_edit);
            send = (Button) itemView.findViewById(R.id.btn_send);

            vehiclePhoto = (ImageView) itemView.findViewById(R.id.vehicle_photo);

            root = (CardView) itemView.findViewById(R.id.root_layout);

        }

    }




}
