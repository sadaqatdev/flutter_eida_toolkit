import 'package:pigeon/pigeon.dart';

class FingerData {
  final int? fingerId;
  final FingerIndex? fingerIndex;
  FingerData({
    required this.fingerId,
    required this.fingerIndex,
  });
}

// class Constants {
//   final int SUCCESS = 0;
//   final int ERROR = 1;
//   final int DISCONNECTED = 245;
//   final int DISCONNECTED2 = 1000;
//   int CARD_VALID = 101;
//   int AUTH_CERT = 102;
//   int SIGN_CERT = 103;
// }

enum FingerIndex {
  NONE,
  NO_MEANING,
  RIGHT_THUMB,
  RIGHT_INDEX,
  RIGHT_MIDDLE,
  RIGHT_RING,
  RIGHT_LITTLE,
  LEFT_THUMB,
  LEFT_INDEX,
  LEFT_MIDDLE,
  LEFT_RING,
  LEFT_LITTLE;
}

@HostApi()
abstract class EidaToolkitConnect {
  void connectAndInitializeF(bool isGraba);
  void onClickCheckCardStatusF();
  void onClickLoadFingerDataF();
  void onClickFingerVerifyF(int fingerId, int fingerIndex);
}

@FlutterApi()
abstract class EidaToolkitData {
  void onBiometricVerify(int? status, String? message, String? vgResponse);
  void statusListener(String? message);
  void onCardReadComplete(
      int? status, String? message, Map<String, String>? cardPublicData);
  void onFingerIndexFetched(
      int? status, String? message, List<FingerData>? fingers);
  void onCheckCardStatus(int? status, String? message, String? xmlString);
  void onToolkitConnected(int? status, bool? isConnectFlag, String? message);
  void onToolkitInitialized(bool? isSuccessful, String? statusMessage);
}
