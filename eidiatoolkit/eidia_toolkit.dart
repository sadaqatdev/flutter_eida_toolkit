import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class EidaToolkitConnect {
  void connectAndInitializeF();
  void onClickCheckCardStatusF();
  void onClickLoadFingerDataF();
  void onClickFingerVerifyF();
}

@FlutterApi()
abstract class EidaToolkitData {
  void onBiometricVerify(int status, String message, String vgResponse);
  void statusListener(String message);
  void onCardReadComplete(int status, String message, String cardPublicData);
  void onFingerIndexFetched(int status, String message, String fingers);
  void onCheckCardStatus(int status, String message, String xmlString);
  void onToolkitConnected(int status, bool isConnectFlag, String message);
  void onToolkitInitialized(bool isSuccessful, String statusMessage);
}
