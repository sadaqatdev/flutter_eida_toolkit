
package com.eida.cms;


import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

import android.content.Intent;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eida.cms.tasks.AppController;
import com.eida.cms.tasks.CardReaderConnectionTask;
import com.eida.cms.tasks.CheckCardStatusAsync;
import com.eida.cms.tasks.Constants;
import com.eida.cms.tasks.GetFingerIndexAsync;
import com.eida.cms.tasks.InitializeToolkitTask;
import com.eida.cms.tasks.ReaderCardDataAsync;
import com.eida.cms.tasks.ReaderCardDataListener;
import com.eida.cms.tasks.VerifyBiometricAsync;
import com.grabba.Grabba;
import com.grabba.GrabbaBarcode;
import com.grabba.GrabbaBarcodeListener;
import com.grabba.GrabbaButtonListener;
import com.grabba.GrabbaDriverNotInstalledException;
import com.grabba.GrabbaFingerprintPlugin;
import com.grabba.GrabbaSmartcardPlugin;
import com.identos.android.idtplugin.bio.BiometryAPI;
import com.identos.android.idtplugin.sc.SmartcardAPI;
import com.identos.android.idtplugin.bio.BiometryAPI;
import com.identos.android.idtplugin.sc.SmartcardAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ae.emiratesid.idcard.toolkit.datamodel.CardPublicData;
import ae.emiratesid.idcard.toolkit.datamodel.FingerData;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.EidaToolkitPlugin;
import io.flutter.plugins.EidaToolkitPlugin.Result;


public class MainActivity extends FlutterActivity {

    void printL(String msg) {
        Log.d("EID", msg);
    }


    JSONObject logs = new JSONObject();

    private final Map<String, String> DataToSend = new HashMap<>();

    //    EidaToolkitPlugin.EidaToolkitData.
    EidaToolkitPlugin.EidaToolkitData eidaToolkitData;


    Result<Void> result = new EidaToolkitPlugin.Result<Void>() {
        @Override
        public void success(@NonNull Void result) {

        }

        @Override
        public void error(@NonNull Throwable error) {

        }
    };

    private final GrabbaButtonListener buttonListener = new GrabbaButtonListener() {
        @Override
        public void grabbaRightButtonEvent(boolean pressed) {

        }

        @Override
        public void grabbaLeftButtonEvent(boolean pressed) {

        }
    };

    private final GrabbaBarcodeListener barcodeListener = new GrabbaBarcodeListener() {

        public void barcodeTriggeredEvent() {
        }

        public void barcodeTimeoutEvent() {

        }

        public void barcodeScanningStopped() {

        }

        public void barcodeScannedEvent(String barcode, int symbologyType) {
//            try {
//                if (deliveryNoteIds.contains(barcode)) {
//                    getData(barcode);
//                    validateShipment();
//                } else {
//                    showToast("Shipment number: " + barcode + "," + "\nDoesn't exists for biometric delivery !");
//                }
//            } catch (Exception e) {
//                showToast(e.getMessage());
//            }
        }
    };


    private final CheckCardStatusAsync.CheckCardStatusListener checkCardStatusListener =
            (status, message, xmlString) -> {
                //

                printL("checkCardStatusListener  " + message);
                printL("checkCardStatusListener  " + status);
                printL("checkCardStatusListener  " + xmlString);


                eidaToolkitData.onCheckCardStatus((long) status, message, xmlString, result);

                AppController.isReading = false;


            };

    private final GetFingerIndexAsync.GetFingerIndexListener getFingerIndexListener = new GetFingerIndexAsync.GetFingerIndexListener() {
        @Override
        public void onFingerIndexFetched(int status, String message, FingerData[] fingers) {
            printL("onFingerIndexFetched" + status);
            printL("onFingerIndexFetched" + message);
            printL("onFingerIndexFetched" + fingers.length);

            List<EidaToolkitPlugin.FingerData> fingersArg = new ArrayList<>();

            if (status == Constants.SUCCESS) {

                for (FingerData finger : fingers) {
                    long ll = finger.getFingerId();
                    EidaToolkitPlugin.FingerIndex f = EidaToolkitPlugin.FingerIndex.valueOf(finger.getFingerIndex().name());
                    EidaToolkitPlugin.FingerData d = new EidaToolkitPlugin.FingerData();
                    d.setFingerId(ll);
                    d.setFingerIndex(f);
                    fingersArg.add(d);
                }

                eidaToolkitData.onFingerIndexFetched((long) status, message, fingersArg, result);
                AppController.isReading = false;

            }


        }
    };


//


    private final ReaderCardDataListener readerCardDataListener = new ReaderCardDataListener() {
        @Override
        public void onCardReadComplete(int status, String message, CardPublicData cardPublicData
        ) {

            printL("onCardReadComplete" + status);
            printL("onCardReadComplete" + message);
            printL("onCardReadComplete" + cardPublicData.getCardNumber());
            printL("onCardReadComplete" + cardPublicData.getCardSerialNumber());

            try {

                if (status == Constants.SUCCESS) {


                    DataToSend.put("id_number", cardPublicData.getIdNumber());
                    DataToSend.put("name_en", cardPublicData.getNonModifiablePublicData().getFullNameEnglish());
                    DataToSend.put("nationality_en", cardPublicData.getNonModifiablePublicData().getNationalityEnglish());
                    DataToSend.put("gender", cardPublicData.getNonModifiablePublicData().getGender());
                    DataToSend.put("place_of_birth_en", cardPublicData.getNonModifiablePublicData().getPlaceOfBirthEnglish());
                    DataToSend.put("date_of_birth", cardPublicData.getNonModifiablePublicData().getDateOfBirth());
                    DataToSend.put("expiry_date", cardPublicData.getNonModifiablePublicData().getExpiryDate());
                    DataToSend.put("issue_date", cardPublicData.getNonModifiablePublicData().getIssueDate());
                    DataToSend.put("passport_number", cardPublicData.getModifiablePublicData().getPassportNumber());
                    DataToSend.put("id_type", cardPublicData.getNonModifiablePublicData().getIDType());
                    DataToSend.put("mother_name", cardPublicData.getModifiablePublicData().getMotherFullNameEnglish());
                    DataToSend.put("card_number", cardPublicData.getCardNumber());
                    DataToSend.put("image", cardPublicData.getCardHolderPhoto());
                    DataToSend.put("sponsor_unified_number", cardPublicData.getModifiablePublicData().getSponsorUnifiedNumber());
                    DataToSend.put("residency_number", cardPublicData.getModifiablePublicData().getResidencyNumber());
                    DataToSend.put("residency_expiryDate", cardPublicData.getModifiablePublicData().getResidencyExpiryDate());
                    DataToSend.put("signture", cardPublicData.getHolderSignatureImage());


                    DataToSend.put("title", cardPublicData.getNonModifiablePublicData().getTitleEnglish());
                    DataToSend.put("nationalityCode", cardPublicData.getNonModifiablePublicData().getNationalityCode());
                    DataToSend.put("occupationCode", String.valueOf(cardPublicData.getModifiablePublicData().getOccupationCode()));
                    DataToSend.put("occupationEnglish", cardPublicData.getModifiablePublicData().getOccupationEnglish());
                    DataToSend.put("occupationTypeEnglish", cardPublicData.getModifiablePublicData().getOccupationTypeEnglish());
                    DataToSend.put("occupationFieldCode", cardPublicData.getModifiablePublicData().getOccupationFieldCode());
                    DataToSend.put("companyNameEnglish", cardPublicData.getModifiablePublicData().getCompanyNameEnglish());
                    DataToSend.put("maritalStatusCode", cardPublicData.getModifiablePublicData().getMaritalStatusCode());
                    DataToSend.put("residencyTypeCode", cardPublicData.getModifiablePublicData().getResidencyTypeCode());
                    DataToSend.put("passportTypeCode", cardPublicData.getModifiablePublicData().getPassportTypeCode());
                    DataToSend.put("passportCountryCode", cardPublicData.getModifiablePublicData().getPassportCountryCode());
                    DataToSend.put("passportCountryEnglish", cardPublicData.getModifiablePublicData().getPassportCountryDescEnglish());
                    DataToSend.put("passportIssueDate", cardPublicData.getModifiablePublicData().getPassportIssueDate());
                    DataToSend.put("passportExpiryDate", cardPublicData.getModifiablePublicData().getPassportExpiryDate());
                    DataToSend.put("fullNameArabic", cardPublicData.getNonModifiablePublicData().getFullNameArabic());

                    DataToSend.put("CardSerialNumber", cardPublicData.getCardSerialNumber());
                    DataToSend.put("HomeAddress", cardPublicData.getHomeAddress().getAreaDescEnglish());
                    DataToSend.put("MobilePhoneNumber", cardPublicData.getHomeAddress().getMobilePhoneNumber());
                    DataToSend.put("WorkAddress", cardPublicData.getWorkAddress().getAreaDescEnglish());
                    DataToSend.put("CityDescEnglish", cardPublicData.getHomeAddress().getCityDescEnglish());
                    DataToSend.put("AreaDescEnglish", cardPublicData.getHomeAddress().getAreaDescEnglish());
                    DataToSend.put("BuildingNameEnglish", cardPublicData.getHomeAddress().getBuildingNameEnglish());


                    DataToSend.put("husbandIdNumber", cardPublicData.getModifiablePublicData().getHusbandIDN());
                    DataToSend.put("sponsorTypeCode", cardPublicData.getModifiablePublicData().getSponsorTypeCode());
                    DataToSend.put("companyNameArabic", cardPublicData.getModifiablePublicData().getCompanyNameArabic());
                    DataToSend.put("titleArabic", cardPublicData.getNonModifiablePublicData().getTitleArabic());
                    DataToSend.put("titleEnglish", cardPublicData.getNonModifiablePublicData().getTitleEnglish());
                    DataToSend.put("nationalityArabic", cardPublicData.getNonModifiablePublicData().getNationalityArabic());
                    DataToSend.put("placeOfBirthArabic", cardPublicData.getNonModifiablePublicData().getPlaceOfBirthArabic());
                    DataToSend.put("occupationArabic", cardPublicData.getModifiablePublicData().getOccupationArabic());
                    DataToSend.put("familyId", cardPublicData.getModifiablePublicData().getFamilyID());
                    DataToSend.put("occupationTypeArabic", cardPublicData.getModifiablePublicData().getOccupationArabic());
                    DataToSend.put("sponsorName", cardPublicData.getModifiablePublicData().getSponsorName());
                    DataToSend.put("passportCountryArabic", cardPublicData.getModifiablePublicData().getPassportCountryDescArabic());
                    DataToSend.put("qualificationLevelCode", cardPublicData.getModifiablePublicData().getQualificationLevelCode());
                    DataToSend.put("qualificationLevelArabic", cardPublicData.getModifiablePublicData().getQualificationLevelDescArabic());
                    DataToSend.put("qualificationLevelEnglish", cardPublicData.getModifiablePublicData().getQualificationLevelDescEnglish());
                    DataToSend.put("degreeDescriptionArabic", cardPublicData.getModifiablePublicData().getDegreeDescArabic());
                    DataToSend.put("degreeDescriptionEnglish", cardPublicData.getModifiablePublicData().getDegreeDescEnglish());
                    DataToSend.put("fieldOfStudyCode", String.valueOf(cardPublicData.getModifiablePublicData().getFieldOfStudyCode()));
                    DataToSend.put("fieldOfStudyArabic", cardPublicData.getModifiablePublicData().getFieldOfStudyArabic());
                    DataToSend.put("fieldOfStudyEnglish", cardPublicData.getModifiablePublicData().getFieldOfStudyEnglish());

                    DataToSend.put("placeOfStudyEnglish", cardPublicData.getModifiablePublicData().getPlaceOfStudyEnglish());
                    DataToSend.put("dateOfGraduation", cardPublicData.getModifiablePublicData().getDateOfGraduation());
                    DataToSend.put("motherFullNameArabic", cardPublicData.getModifiablePublicData().getMotherFullNameArabic());
                    DataToSend.put("motherFullNameEnglish", cardPublicData.getModifiablePublicData().getMotherFullNameEnglish());
                    DataToSend.put("homeAddress", cardPublicData.getHomeAddress().getCityDescArabic());
                    DataToSend.put("placeOfStudyArabic", cardPublicData.getModifiablePublicData().getPlaceOfStudyArabic());

                    DataToSend.put("addressTypeCode", cardPublicData.getHomeAddress().getAddressTypeCode());
                    DataToSend.put("locationCode", cardPublicData.getHomeAddress().getLocationCode());

                    DataToSend.put("emiratesCode", cardPublicData.getHomeAddress().getEmiratesCode());
                    DataToSend.put("emiratesDescArabic", cardPublicData.getHomeAddress().getEmiratesDescArabic());
                    DataToSend.put("emiratesDescEnglish", cardPublicData.getHomeAddress().getEmiratesDescEnglish());

                    DataToSend.put("cityDescArabic", cardPublicData.getHomeAddress().getCityDescArabic());
                    DataToSend.put("cityDescEnglish", cardPublicData.getHomeAddress().getCityDescEnglish());
                    DataToSend.put("streetArabic", cardPublicData.getHomeAddress().getStreetArabic());
                    DataToSend.put("cityCode", cardPublicData.getHomeAddress().getCityCode());

                    DataToSend.put("streetEnglish", cardPublicData.getHomeAddress().getStreetEnglish());
                    DataToSend.put("pobox", cardPublicData.getHomeAddress().getPOBOX());
                    DataToSend.put("areaCode", cardPublicData.getHomeAddress().getAreaCode());
                    DataToSend.put("areaDescArabic", cardPublicData.getHomeAddress().getAreaDescArabic());
                    DataToSend.put("areaDescEnglish", cardPublicData.getHomeAddress().getAreaDescEnglish());

                    DataToSend.put("buildingNameArabic", cardPublicData.getHomeAddress().getBuildingNameArabic());
                    DataToSend.put("buildingNameEnglish", cardPublicData.getHomeAddress().getBuildingNameEnglish());
                    DataToSend.put("mobilePhoneNumber", cardPublicData.getHomeAddress().getAreaDescEnglish());

                    DataToSend.put("email", cardPublicData.getHomeAddress().getEmail());
                    DataToSend.put("flatNo", cardPublicData.getHomeAddress().getFlatNo());
                    DataToSend.put("residentPhoneNumber", cardPublicData.getHomeAddress().getResidentPhoneNumber());
                    DataToSend.put("workAddress", cardPublicData.getWorkAddress().getAreaDescEnglish());
                    DataToSend.put("landPhoneNumber", cardPublicData.getHomeAddress().getResidentPhoneNumber());
                    DataToSend.put("cardHolderPhoto", cardPublicData.getCardHolderPhoto());
                    DataToSend.put("holderSignatureImage", cardPublicData.getHolderSignatureImage());

                    eidaToolkitData.onCardReadComplete(0l, "Card read", DataToSend, result);

                    AppController.isReading = false;

                }


//                displayPhoto(cardPublicData);

            } catch (Exception e) {
                e.printStackTrace();

                eidaToolkitData.statusListener(e.getMessage(), result);

            }


        }

    };

    private final CardReaderConnectionTask.ConnectToolkitListener connectToolkitListener = new CardReaderConnectionTask.ConnectToolkitListener() {
        @Override
        public void onToolkitConnected(int status, boolean isConnectFlag, String message) {

            printL("onToolkitConnected" + status);
            printL("onToolkitConnected" + isConnectFlag);
            printL("onToolkitConnected" + message);

            eidaToolkitData.onToolkitConnected((long) status, isConnectFlag, message == null ? "" : message, result);

            if (isConnectFlag) {
                if (status == Constants.SUCCESS) {

                    ReaderCardDataAsync readerTask = new ReaderCardDataAsync(readerCardDataListener);

                    readerTask.execute();

                }


            }


        }
    };


    private final InitializeToolkitTask.InitializationListener mInitializationListener =
            new InitializeToolkitTask.InitializationListener() {
                @Override
                public void onToolkitInitialized(boolean isSuccessful, String statusMessage) {
                    //
                    printL("onToolkitInitialized" + isSuccessful);
                    printL("onToolkitInitialized" + statusMessage);


                    eidaToolkitData.onToolkitInitialized(isSuccessful, statusMessage == null ? "" : statusMessage, result);

                    if (isSuccessful) {
                        AppController.isInitialized = true;
                        ConnectCard();
                    }


                }
            };


    VerifyBiometricAsync.VerifyFingerprintListener verifyBiometricListener = new VerifyBiometricAsync.VerifyFingerprintListener() {
        @Override
        public void onBiometricVerify(int status, String message, String vgResponse) {

            printL("onBiometricVerify" + status);
            printL("onBiometricVerify" + message);
            printL("onBiometricVerify" + vgResponse);

            eidaToolkitData.onBiometricVerify((long) status, message == null ? "" : message, vgResponse, result);


        }
    };


    private void displayPhoto(CardPublicData cardPublicData) {
        if (cardPublicData == null) {
            return;
        }
        byte[] photo = Base64.decode(cardPublicData.getCardHolderPhoto(), Base64.DEFAULT);
        if (photo == null || photo.length <= 0) {
            return;
        }

//        Image.setImageBitmap(Bitmaps.decodeSampledBitmapFromBytes(photo, 150, 150));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

//

            } else if (requestCode == 2) {

            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = null;
//                getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    }

    public void OnClickTakePicSign(View view) {
        selectImage();
    }

    private void selectImage() {
        dispatchTakePictureIntent();
    }

    public void onClickReadData(View view) {

    }

    public void OnClickCheckCardStatus() {
        if (!AppController.isReading) {


            //set the reading flag..
            AppController.isReading = true;

            printL("OnClickCheckCardStatus");


            //create the object of ReaderCardDataAsync
            CheckCardStatusAsync checkCardStatusAsync = new CheckCardStatusAsync(checkCardStatusListener);
            checkCardStatusAsync.execute();

        }//

    }

    public void onClickLoadFingerData() {

        printL("onClickLoadFingerData");

        GetFingerIndexAsync getFingerIndexAsync = new GetFingerIndexAsync(getFingerIndexListener);

        getFingerIndexAsync.execute();
    }

    public void onClickFingerVerify(FingerData fingerData) {

        VerifyBiometricAsync verifyBiometricAsync = new VerifyBiometricAsync(verifyBiometricListener, fingerData, 20);

        verifyBiometricAsync.execute();
    }

    void initGrabba(boolean isGraba) {

        try {

            Grabba.open(getApplicationContext(), "bio");

            Grabba.getInstance().addButtonListener(buttonListener);

            GrabbaBarcode.getInstance().addEventListener(barcodeListener);

        } catch (GrabbaDriverNotInstalledException e) {
            eidaToolkitData.statusListener(e.getMessage(), result);
            eidaToolkitData.statusListener("Grabaa not inilize error", result);
            e.printStackTrace();
        }


        //        try {
//            int s = SmartcardAPI.initialize(getApplicationContext());
//
//            int b = BiometryAPI.initialize(getApplicationContext());
//
//            printL(" Smartcard API   card init if print 0 initialize ele not = " + s);
//
//            printL(" BiometryAPI   card init if print 0 initialize ele not = " + b);
//        } catch (Exception e) {
//            eidaToolkitData.statusListener(e.getMessage(), result);
//        }

    }

    private void initialize() {
        printL("initialize call");
        eidaToolkitData.statusListener("initialize cal", result);
        try {
            InitializeToolkitTask initializeToolkitTask = new InitializeToolkitTask
                    (mInitializationListener);

            initializeToolkitTask.execute();
        } catch (Exception e) {
            eidaToolkitData.statusListener("ConnectCard call", result);
            printL("Error in initialize call");
        }
    }

    private void ConnectCard() {


        try {
            printL("ConnectCard call");
            eidaToolkitData.statusListener("ConnectCard call", result);
            CardReaderConnectionTask cardReaderConnectionTask = new CardReaderConnectionTask
                    (connectToolkitListener, true);

            cardReaderConnectionTask.execute();
        } catch (Exception e) {
            eidaToolkitData.statusListener("ConnectCard call", result);
        }
    }

    protected void Connect() {
        printL("Connect");
        eidaToolkitData.statusListener("Connect", result);
        if (!AppController.isInitialized) {
            initialize();
        } else {
            ConnectCard();
        }
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        EidaToolkitPlugin.EidaToolkitConnect.setUp(flutterEngine.getDartExecutor().getBinaryMessenger(), new EidaToolkitConnectImpl());
        eidaToolkitData = new EidaToolkitPlugin.EidaToolkitData(flutterEngine.getDartExecutor().getBinaryMessenger());

        init();
//


    }

    private void init() {
        File outDir = new File(AppController.path);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }//
    }


    public class EidaToolkitConnectImpl implements EidaToolkitPlugin.EidaToolkitConnect {


        @Override
        public void connectAndInitializeF(@NonNull Boolean isGraba) {
            //

            initGrabba(isGraba);

            Connect();

        }

        @Override
        public void onClickCheckCardStatusF() {
            OnClickCheckCardStatus();
        }


        @Override
        public void onClickLoadFingerDataF() {
            onClickLoadFingerData();
        }

        @Override
        public void onClickFingerVerifyF(@NonNull Long fingerId, @NonNull Long fingerIndex) {


            onClickFingerVerify(new FingerData((int) fingerId.intValue(), (int) fingerIndex.intValue()));
        }


    }
}