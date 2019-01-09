package com.info121.vms.api;


import com.info121.vms.models.Block;
import com.info121.vms.models.CondoInfo;
import com.info121.vms.models.Purpose;
import com.info121.vms.models.ScanResult;
import com.info121.vms.models.Storey;
import com.info121.vms.models.UnitNo;
import com.info121.vms.models.UserRes;
import com.info121.vms.models.VehicleSaveReq;
import com.info121.vms.models.VehicleSaveRes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by KZHTUN on 7/6/2017.
 */

public class APIClient {
    private static String DEVICE_TYPE = "Android";

    public static void GetUserList() {
        Call<List<UserRes>> call = RestClient.VMS().getApiService().getUserList();
        call.enqueue(new APICallback<List<UserRes>>() {
        });
    }

    public static void GetAllPurpose() {
        Call<List<Purpose>> call = RestClient.VMS().getApiService().getAllPurpose();
        call.enqueue(new APICallback<List<Purpose>>() {
        });
    }


    public static void SaveVehicle(VehicleSaveReq req) {
        Call<VehicleSaveRes> call = RestClient.VMS().getApiService().saveVehicle(req);
        call.enqueue(new APICallback<VehicleSaveRes>() {
        });
    }


    public static void GetAllBlocks() {
        Call<List<Block>> call = RestClient.VMS().getApiService().getAllBlocks();
        call.enqueue(new APICallback<List<Block>>() {
        });
    }

    public static void GetAllStoreys() {
        Call<List<Storey>> call = RestClient.VMS().getApiService().getAllStoreys();
        call.enqueue(new APICallback<List<Storey>>() {
        });
    }

    public static void GetAllUnitNos() {
        Call<List<UnitNo>> call = RestClient.VMS().getApiService().getAllUnitNos();
        call.enqueue(new APICallback<List<UnitNo>>() {
        });
    }

    public static void GetCondoInformation() {
        Call<CondoInfo> call = RestClient.VMS().getApiService().getCondoInformation();
        call.enqueue(new APICallback<CondoInfo>() {
        });
    }

    public static void UploadCarPhoto(RequestBody photo) {
        Call<String> call = RestClient.VMS().getApiService().UploadCarPhoto(photo);
        call.enqueue(new APICallback<String>() {
        });
    }

    public static void UploadDriverPhoto(RequestBody photo) {
        Call<String> call = RestClient.VMS().getApiService().UploadDriverPhoto(photo);
        call.enqueue(new APICallback<String>() {
        });
    }

    public static void UploadOtherPhoto1(RequestBody photo) {
        Call<String> call = RestClient.VMS().getApiService().UploadOtherPhoto1(photo);
        call.enqueue(new APICallback<String>() {
        });
    }

    public static void SendItem(RequestBody uuid,
                                MultipartBody.Part carPhoto,
                                MultipartBody.Part driverPhoto,
                                MultipartBody.Part otherPhoto1,
                                RequestBody carinoutsrno,
                                RequestBody carno,
                                RequestBody mobileno,
                                RequestBody name,
                                RequestBody visittype,
                                RequestBody purpose,
                                RequestBody unitno,
                                RequestBody resident,
                                RequestBody remarks,
                                RequestBody username,
                                RequestBody entrydatetime) {

        Call<VehicleSaveRes> call = RestClient.VMSUpload().getApiService().sendItem(uuid,
                carPhoto,
                driverPhoto,
                otherPhoto1,
                carinoutsrno,
                carno,
                mobileno,
                name,
                visittype,
                purpose,
                unitno,
                resident,
                remarks,
                username,
                entrydatetime);
        call.enqueue(new APICallback<VehicleSaveRes>() {
        });
    }


    public static void upload_test(RequestBody uuid, MultipartBody.Part carPhoto){
        Call<VehicleSaveRes> call = RestClient.VMSUpload().getApiService().uploadTest(uuid,
                carPhoto);
        call.enqueue(new APICallback<VehicleSaveRes>() {
        });

    }


    public static void GetIUScan(String lane){
        Call<ScanResult> call = RestClient.VMS().getApiService().GetScanResult(lane);
        call.enqueue(new APICallback<ScanResult>() {
        });

    }


}
