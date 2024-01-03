import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';
import 'edia/eida_toolkit.dart';

final kNavig = GlobalKey<NavigatorState>();

dp(msg, arg) {
  debugPrint("${"Flutter side " + msg}+   $arg");
}

void main() {
  //

  runApp(ChangeNotifierProvider(
      create: (BuildContext context) {
        return DataProvider();
      },
      child: const MyApp()));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Tactivo',
      navigatorKey: kNavig,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Grabba Test 3'),
    );
  }
}

class DataProvider extends ChangeNotifier {
  var errorlist = [];

  bool? isConnnete = false;

  ScrollController scrollController = ScrollController();

  FingerData? fingerData;

  List<String?> m = [];
  add(String? a, String? b, String? c) async {
    try {
      m.add(a);
      m.add(b);
      m.add(c);
      dp("Lenght ", m.length);

      notifyListeners();
      await Future.delayed(Duration.zero);
      scrollDown();
    } catch (e, s) {
      dp("Error in add $e", s);
    }
  }

  scrollDown() {
    scrollController.animateTo(
      scrollController.position.maxScrollExtent,
      duration: const Duration(milliseconds: 100),
      curve: Curves.easeInOut,
    );
    notifyListeners();
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  //

  EidaToolkitConnect eidaToolkitConnect = EidaToolkitConnect();

  @override
  void initState() {
    //

    EidaToolkitData.setup(EdiaImpl());

    Future.delayed(const Duration(seconds: 2)).then((value) {
      getPermission();
    });

    super.initState();
  }

  getPermission() async {
    //

    await Permission.camera.request();
    await Permission.storage.request();
    await Permission.location.request();
    await Permission.audio.request();
    await Permission.bluetooth.request();
    await Permission.bluetoothConnect.request();
    await Permission.phone.request();

    await Permission.videos.request();
    await Permission.nearbyWifiDevices.request();
    await Permission.accessMediaLocation.request();
    await Permission.manageExternalStorage.request();
  }

  String currentDevice = 'Grabba';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Consumer<DataProvider>(builder: (context, p, c) {
        return SingleChildScrollView(
          controller: p.scrollController,
          child: SizedBox(
              height: MediaQuery.of(context).size.height,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  DropdownButton(
                    items: ['Grabba', 'Tacktivo']
                        .map((e) => DropdownMenuItem(
                              child: Text(e),
                              value: e,
                            ))
                        .toList(),
                    value: currentDevice,
                    onChanged: (value) {
                      currentDevice = value!;
                      setState(() {});
                    },
                  ),
                  MaterialButton(
                    onPressed: () {
                      setState(() {});
                    },
                    child: const Text("Refersh"),
                  ),
                  Text("Is Deveice connected ${p.isConnnete} "),
                  const SizedBox(
                    height: 20,
                  ),
                  MaterialButton(
                    onPressed: () {
                      eidaToolkitConnect
                          .connectAndInitializeF(currentDevice == "Grabba");
                    },
                    child: const Text("Connect and initialize"),
                  ),
                  MaterialButton(
                    onPressed: () {
                      eidaToolkitConnect.onClickLoadFingerDataF();
                    },
                    child: const Text("onClickLoadFingerDataF"),
                  ),
                  MaterialButton(
                    onPressed: () {
                      eidaToolkitConnect.onClickCheckCardStatusF();
                    },
                    child: const Text("onClickCheckCardStatusF"),
                  ),
                  MaterialButton(
                    onPressed: () {
                      if (p.fingerData == null) {
                        ScaffoldMessenger.maybeOf(context)!.showSnackBar(
                            const SnackBar(
                                content: Text("Select finger first")));
                        return;
                      }
                      eidaToolkitConnect.onClickFingerVerifyF(
                          p.fingerData!.fingerId!,
                          p.fingerData!.fingerIndex!.index);
                    },
                    child: const Text("onClickFingerVerifyF"),
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  const Text("Status Data"),
                  Expanded(
                    child: ListView.builder(
                      itemCount: p.m.length,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemBuilder: (context, index) {
                        return Text(
                          p.m[index]?.trim() ?? '',
                          style: const TextStyle(
                              fontSize: 14, color: Colors.black),
                        );
                      },
                    ),
                  ),
                ],
              )),
        );
      }),
    );
  }
}

class EdiaImpl extends EidaToolkitData {
  @override
  void onBiometricVerify(int? status, String? message, String? vgResponse) {
    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .add(status.toString(), message.toString(), vgResponse.toString());
    dp("onBiometricVerify", message);
    // streamController.add([...m]);
  }

  @override
  void onCardReadComplete(
      int? status, String? message, Map<String?, String?>? cardPublicData) {
    cardPublicData!.forEach((e, f) {
      Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
          .add(status.toString(), message.toString(), f ?? '');
    });

    dp("onCardReadComplete", message);
    // streamController.add([...m]);
  }

  @override
  void onCheckCardStatus(int? status, String? message, String? xmlString) {
    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .add(status.toString(), message.toString(), xmlString);
    dp("onCheckCardStatus", message);
    // streamController.add([...m]);
  }

  @override
  void onFingerIndexFetched(
      int? status, String? message, List<FingerData?>? fingers) {
    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .add(status.toString(), message.toString(), fingers?.length.toString());

    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .fingerData = fingers?.first;

    // fingerData = fingers?.first;
  }

  @override
  void onToolkitConnected(int? status, bool? isConnectFlag, String? message) {
    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .add(status.toString(), message.toString(), isConnectFlag.toString());
    // streamController.add([...m]);
    dp("onToolkitConnected", message);
    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .add("$status", message, isConnectFlag.toString());
  }

  @override
  void onToolkitInitialized(bool? isSuccessful, String? statusMessage) {
    Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
        .add(isSuccessful.toString(), statusMessage.toString(), '');
    // streamController.add([...m]);
    dp("onToolkitInitialized", statusMessage);
  }

  @override
  void statusListener(String? message) {
    dp("statusListener", message);
    try {
      Provider.of<DataProvider>(kNavig.currentState!.context, listen: false)
          .add('', message?.toString(), '');
    } catch (e, s) {
      dp("Error in add $e ", s);
    }

    // streamController.add([...m]);
  }
}
