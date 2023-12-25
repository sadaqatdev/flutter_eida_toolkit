package com.eida.flutter_eida_toolkit.tasks;

import android.os.AsyncTask;

import ae.emiratesid.idcard.toolkit.ToolkitException;



public class InitializeToolkitTask extends AsyncTask<Void, Integer, String> {

    private final InitializationListener mInitializationListener;
    private boolean isToolkitInitialized;

    public InitializeToolkitTask(InitializationListener initializationListener) {
        this.mInitializationListener = initializationListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String statusMessage = null;
        try {
            
            isToolkitInitialized = ConnectionController.initialize();

            statusMessage = "Toolkit Successfully initialized.";
        } catch (ToolkitException e) {
            e.printStackTrace();
            Logger.e("Exception code " + e.getCode());
            statusMessage = e.getMessage();
        }
        return statusMessage;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (null != mInitializationListener) {
            mInitializationListener.onToolkitInitialized(isToolkitInitialized, s);
        } else {
            Logger.e("No listener attached to Acknowledge.");
        }
    }

    public interface InitializationListener {
        void onToolkitInitialized(boolean isSuccessful, String statusMessage);
    }
}
