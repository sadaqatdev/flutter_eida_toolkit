package com.eida.flutter_eida_toolkit.tasks;


import ae.emiratesid.idcard.toolkit.datamodel.CardPublicData;

public interface ReaderCardDataListener {
    void onCardReadComplete(int status, String message, CardPublicData cardPublicData);
}
