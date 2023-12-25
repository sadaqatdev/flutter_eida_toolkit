//package com.eida.eida_toolkit.tasks;
//
//import static android.content.Context.LOCATION_SERVICE;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//
//
//
//import java.util.List;
//
//
//public class Utility {
//
//    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10; // in Meters
//    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 100000; // in Millise
//    public static String lat = "0.0";
//    public static String lng = "0.0";
//
//    //your common method
//    public static void showDialog(Context context, int type) {
//
//        //TODO task
//    }
//
//    public static void dismissKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (null != activity.getCurrentFocus())
//            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
//                    .getApplicationWindowToken(), 0);
//    }
//
//    public static void showDialogDismiss(Context context, String msg) {
////        new MaterialAlertDialogBuilder(context)
////                .setMessage(msg)
////                .setCancelable(false)
////                .setPositiveButton("OK",
////                        new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int id) {
////                                dialog.cancel();
////                            }
////                        })
////                .show();
//    }
//
////    public static void checkError(VolleyError error, Context con) {
////        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
////            showDialogDashboard(con, "Network Timeout, Try again");
////        } else if (error instanceof AuthFailureError) {
////            showDialogDashboard(con, "AuthFailureError, Try again");
////        } else if (error instanceof ServerError) {
////            showDialogDashboard(con, "ServerError, Try again");
////        } else if (error instanceof NetworkError) {
////            showDialogDashboard(con, "NetworkError, Try again");
////        } else if (error instanceof ParseError) {
////            showDialogDashboard(con, "ParseError, Try again");
////        } else {
////            showDialogDashboard(con, "Unknown Error, Try again");
////        }
////        Logger.e("Volley Error" + error);
////    }
//
//
//
//    public static void showDialogDashboard(final Context context, String msg) {
//        new MaterialAlertDialogBuilder(context)
//                .setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//
//                            }
//                        })
//                .show();
//    }
//
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//    private void showSnack(View view, String msg) {
//        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
//                .show();
//    }
//
//    private static class MyLocationListener implements LocationListener {
//
//        public void onLocationChanged(Location location) {
//        }
//
//        public void onStatusChanged(String s, int i, Bundle b) {
//        }
//
//        public void onProviderDisabled(String s) {
//        }
//
//        public void onProviderEnabled(String s) {
//        }
//
//    }
//}