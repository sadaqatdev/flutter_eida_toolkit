package com.eida.cms.tasks;

import android.nfc.Tag;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ae.emiratesid.idcard.toolkit.CardReader;
import ae.emiratesid.idcard.toolkit.ToolkitException;
import ae.emiratesid.idcard.toolkit.datamodel.FingerData;



public class GetFingerIndexAsync extends AsyncTask<String, Integer, Integer> {
    private final WeakReference<GetFingerIndexListener> weakReference;
    private CardReader cardReader;
    private int status;
    private FingerData[] indexes;
    private String message;
    private Tag tag;
    private String cardNumber, dob, expiryDate;


    public GetFingerIndexAsync(GetFingerIndexListener listener) {
        this.weakReference = new WeakReference<GetFingerIndexListener>(listener);
    }//GetFingerIndexAsync

    public GetFingerIndexAsync(GetFingerIndexListener listener,
                               Tag tag) {

        this(listener);
        this.tag = tag;
    }

    @Override
    protected Integer doInBackground(String... params) {

        try {
            if (tag != null) {
                cardReader = ConnectionController.initConnection(tag);
                ConnectionController.setNFCParams(cardNumber, dob, expiryDate);
            } else {
                cardReader = ConnectionController.getConnection();
            }

            if (cardReader == null) {
                return Constants.ERROR;
            }//

            indexes = cardReader.getFingerData();
            if (null != indexes || indexes.length <= 0) {
                return Constants.ERROR;
            }

        } catch (ToolkitException e) {
            status = (int) e.getCode();
            message = e.getMessage();
        }//catch()
        return status;
    }//doInBackground()


    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        weakReference.get().onFingerIndexFetched(this.status, message, indexes);
    }//onPostExecute()

    public interface GetFingerIndexListener {
        void onFingerIndexFetched(int status, String message, FingerData[] fingers);
    }
}
