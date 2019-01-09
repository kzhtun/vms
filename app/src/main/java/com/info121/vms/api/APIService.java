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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


/**
 * Created by KZHTUN on 7/6/2017.
 */

public interface APIService {

    @GET("getUserAccountsList")
    Call<List<UserRes>> getUserList();

    @GET("getPurposesList")
    Call<List<Purpose>> getAllPurpose();

    @POST("saveOnLineCarReg")
    Call<VehicleSaveRes> saveVehicle(@Body VehicleSaveReq req);

    @GET("getAllBlocks")
    Call<List<Block>> getAllBlocks();

    @GET("getAllStoreys")
    Call<List<Storey>> getAllStoreys();

    @GET("getallUnitNos")
    Call<List<UnitNo>> getAllUnitNos();

    @GET("getMobilePhotoAndCondoName")
    Call<CondoInfo> getCondoInformation();

    @Multipart
    @POST("upload")
    Call<VehicleSaveRes> sendItem(@Part("UUID") RequestBody uuid,
                                  @Part  MultipartBody.Part  carPhoto,
                                  @Part  MultipartBody.Part  driverPhoto,
                                  @Part  MultipartBody.Part  otherPhoto1,

                                  @Part("carinoutsrno") RequestBody carinoutsrno,
                                  @Part("carno") RequestBody carno,
                                  @Part("mobileno") RequestBody mobileno,
                                  @Part("name") RequestBody name,
                                  @Part("visittype") RequestBody visittype,
                                  @Part("purpose") RequestBody purpose,
                                  @Part("unitno") RequestBody unitno,
                                  @Part("resident") RequestBody resident,
                                  @Part("remarks") RequestBody remarks,
                                  @Part("username") RequestBody username,
                                  @Part("entrydatetime") RequestBody entrydatetime
    );


    @Multipart
    @POST("upload")
    Call<VehicleSaveRes> uploadTest(@Part("UUID") RequestBody uuid,
                                    @Part  MultipartBody.Part   carPhoto);
//    @Multipart
//    @POST("upload")
//    Call<VehicleSaveRes> sendItem2(@Part("UUID") RequestBody uuid,
//                                  @Part MultipartBody.Part  carPhoto,
//                                  @Part MultipartBody.Part driverPhoto,
//                                  @Part MultipartBody.Part  otherPhoto1,
//
////                                  @Part ("CarPhoto") RequestBody carPhoto,
////                                  @Part ("DriverPhoto") RequestBody driverPhoto,
////                                  @Part ("DriverPhoto") RequestBody otherPhoto1,
//
//                                  @Part("carinoutsrno") RequestBody carinoutsrno,
//                                  @Part("carno") RequestBody carno,
//                                  @Part("mobileno") RequestBody mobileno,
//                                  @Part("name") RequestBody name,
//                                  @Part("visittype") RequestBody visittype,
//                                  @Part("purpose") RequestBody purpose,
//                                  @Part("unitno") RequestBody unitno,
//                                  @Part("resident") RequestBody resident,
//                                  @Part("remarks") RequestBody remarks,
//                                  @Part("username") RequestBody username,
//                                  @Part("entrydatetime") RequestBody entrydatetime
//    );

//    @POST("UploadPhoto")
//    Call<String> UploadPhoto1(@Body RequestBody photo);
//
//    @Multipart
//    @POST("UploadPhoto")
//    Call<String> UploadPhoto(@Part MultipartBody.Part filePart);
//
//    @Multipart
//    @POST("UploadPhoto")
//    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @POST("UploadCarPhoto")
    Call<String> UploadCarPhoto(@Body RequestBody photo);

    @POST("UploadDriverPhoto")
    Call<String> UploadDriverPhoto(@Body RequestBody photo);

    @POST("UploadOtherPhoto1")
    Call<String> UploadOtherPhoto1(@Body RequestBody photo);


    @GET("scanIUNumber/{lane}")
    Call<ScanResult> GetScanResult(@Path("lane") String lane);

//
//    @POST("UploadCarPhoto")
//    Observable<String> rxUpload(@Body RequestBody photo);
//
//    //amad,123,android,241jlksfljsaf
//    @GET("updatedriverdetail/{user},{deviceId},{deviceType},{fcm_token}")
//    Call<UpdateDriverDetailRes> updateDriverDetail(@Path("user") String user, @Path("deviceId") String deviceId, @Path("deviceType") String deviceType, @Path("fcm_token") String fcm_token);
//
//    //amad,1.299654,103.855107,0
//    @GET("getdriverlocation/{user},{latitude},{longitude},{status},{address}")
//    Call<UpdateDriverLocationRes> updateDriverLocation(@Path("user") String user, @Path("latitude") String latitude, @Path("longitude") String longitude, @Path("status") int status, @Path("address") String addresss);
//
//
//    @GET("saveshowpic/{user},{job_no},{filename}")
//    Call<SaveShowPicRes> saveShowPic(@Path("user") String user, @Path("job_no") String jobNo, @Path("filename") String fileName);
//
//    @GET("confirmjobreminder/{job_no}")
//    Call<ConfirmJobRes> confirmJob(@Path("job_no") String jobNo);
//
//    @GET("remindmelater/{job_no}")
//    Call<RemindLaterRes> remindLater(@Path("job_no") String jobNo);
//
//    @GET("product")
//    Call<List<product>> getProduct();
//
//    @GET("getversion/AndriodV-{versionCode}")
//    Call<VersionRes> checkVersion(@Path("versionCode") String versionCode);
}
