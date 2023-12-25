
package com.eida.flutter_eida_toolkit;


import android.content.Intent;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;

import com.eida.flutter_eida_toolkit.tasks.AppController;
import com.eida.flutter_eida_toolkit.tasks.CardReaderConnectionTask;
import com.eida.flutter_eida_toolkit.tasks.CheckCardStatusAsync;
import com.eida.flutter_eida_toolkit.tasks.Constants;
import com.eida.flutter_eida_toolkit.tasks.GetFingerIndexAsync;
import com.eida.flutter_eida_toolkit.tasks.InitializeToolkitTask;
import com.eida.flutter_eida_toolkit.tasks.ReaderCardDataAsync;
import com.eida.flutter_eida_toolkit.tasks.ReaderCardDataListener;
import com.eida.flutter_eida_toolkit.tasks.VerifyBiometricAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import ae.emiratesid.idcard.toolkit.datamodel.CardPublicData;
import ae.emiratesid.idcard.toolkit.datamodel.FingerData;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.EidaToolkitPlugin;
import io.flutter.plugins.EidaToolkitPlugin.Result;


public class MainActivity extends FlutterActivity {


    private final int REQUEST_TAKE_PHOTO = 1;


    JSONObject logs = new JSONObject();


    private FingerData[] fingerList;

    private FingerData fingerData;

    EidaToolkitPlugin.EidaToolkitData eidaToolkitData;

    Result result = new Result() {

        @Override
        public void success(@NonNull Object result) {

        }

        @Override
        public void error(@NonNull Throwable error) {

        }
    };


    private final CheckCardStatusAsync.CheckCardStatusListener checkCardStatusListener =
            (status, message, xmlString) -> {
                //


                eidaToolkitData.onCheckCardStatus((long) status, message, xmlString, result);

                AppController.isReading = false;

                if (status == Constants.SUCCESS) {

                } else if (status == Constants.DISCONNECTED || status == Constants.DISCONNECTED2) {

                } else {

                }
                try {
                    logs.put("card_status", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };

    private final GetFingerIndexAsync.GetFingerIndexListener getFingerIndexListener = new GetFingerIndexAsync.GetFingerIndexListener() {
        @Override
        public void onFingerIndexFetched(int status, String message, FingerData[] fingers) {
            eidaToolkitData.onFingerIndexFetched((long) status, message, Arrays.toString(fingers), result);
            AppController.isReading = false;
            if (status == Constants.SUCCESS) {

                fingerList = fingers;

                String[] fingerData = {fingers[0].getFingerIndex() + "", fingers[1].getFingerIndex() + ""};

            } else if (status == Constants.DISCONNECTED || status == Constants.DISCONNECTED2) {

            } else {


            }
        }
    };


//


    private final ReaderCardDataListener readerCardDataListener = new ReaderCardDataListener() {
        @Override
        public void onCardReadComplete(int status, String message, CardPublicData cardPublicData
        ) {
            eidaToolkitData.onCardReadComplete((long) status, message, cardPublicData.toString(), result);
            AppController.isReading = false;

            if (status == Constants.SUCCESS && cardPublicData != null) {

                try {

                    displayPhoto(cardPublicData);

                } catch (Exception e) {
                    e.printStackTrace();

                }

            } else {

                //
            }
        }

    };

    private final CardReaderConnectionTask.ConnectToolkitListener connectToolkitListener = new CardReaderConnectionTask.ConnectToolkitListener() {
        @Override
        public void onToolkitConnected(int status, boolean isConnectFlag, String message) {
            eidaToolkitData.onToolkitConnected((long) status, isConnectFlag, message, result);
            if (isConnectFlag) {
                if (status == Constants.SUCCESS) {

                    ReaderCardDataAsync readerTask = new ReaderCardDataAsync(readerCardDataListener);
                    readerTask.execute();
//                    postDataLog("onToolkitConnected", message);message
                    return;
                }

                if (message.equals("Failed to establish connection with smartcard")) {


                } else if (message.equals("Invalid handle value")) {

                } else {

                }
                return;
            }

            if (status == Constants.SUCCESS) {

                return;
            }

        }
    };

    private boolean isInitialized;

    private final InitializeToolkitTask.InitializationListener mInitializationListener =
            new InitializeToolkitTask.InitializationListener() {
                @Override
                public void onToolkitInitialized(boolean isSuccessful, String statusMessage) {
                    //
                    eidaToolkitData.onToolkitInitialized(isSuccessful, statusMessage, result);
                    if (isSuccessful) {
                        isInitialized = true;
                        ConnectCard();
                        return;
                    }


                }
            };


    private String result_fail;
    private String msg_fail;
    private int fp_tries = 0;

    VerifyBiometricAsync.VerifyFingerprintListener verifyBiometricListener = new VerifyBiometricAsync.VerifyFingerprintListener() {
        @Override
        public void onBiometricVerify(int status, String message, String vgResponse) {

            eidaToolkitData.onBiometricVerify((long) status, message, vgResponse, result);

            fp_tries = fp_tries + 1;
            if (status == Constants.SUCCESS) {


//                postDataLog("onBiometricVerifySuccess", vgResponse);
            } else if (status == Constants.DISCONNECTED || status == Constants.DISCONNECTED2) {
//                showDialog();
//                postDataLog("carddisconnected", message);
            } else {

            }

            try {
                logs.put("attempt_" + fp_tries, message);
//                postDataLog("attempt_" + fp_tries, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }//onBiometricVerify
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


            //create the object of ReaderCardDataAsync
            CheckCardStatusAsync checkCardStatusAsync = new CheckCardStatusAsync(checkCardStatusListener);
            checkCardStatusAsync.execute();

        }//
        else {

        }
    }

    public void onClickLoadFingerData() {

        GetFingerIndexAsync getFingerIndexAsync = new GetFingerIndexAsync(getFingerIndexListener);

        getFingerIndexAsync.execute();
    }

    public void onClickFingerVerify() {

        VerifyBiometricAsync verifyBiometricAsync = new VerifyBiometricAsync(verifyBiometricListener, fingerData, 20);

        verifyBiometricAsync.execute();
    }

    private void initialize() {

        InitializeToolkitTask initializeToolkitTask = new InitializeToolkitTask
                (mInitializationListener);

        initializeToolkitTask.execute();
    }

    private void ConnectCard() {

        CardReaderConnectionTask cardReaderConnectionTask = new CardReaderConnectionTask
                (connectToolkitListener, true);

        cardReaderConnectionTask.execute();
    }

    protected void Connect() {

        if (!isInitialized) {
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
    }

    private void init() {
        File outDir = new File(AppController.path);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }//
    }


    public class EidaToolkitConnectImpl implements EidaToolkitPlugin.EidaToolkitConnect {

        @Override
        public void connectAndInitializeF() {

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
        public void onClickFingerVerifyF() {
            onClickFingerVerify();
        }
    }
}