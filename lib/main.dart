import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'edia/eida_toolkit.dart';

var errorlist = [];

bool? isConnnete = false;

FingerData? fingerData;

var m = <(dynamic, dynamic, dynamic)>[];

void main() {
  //

  PlatformDispatcher.instance.onError = (error, stack) {
    //
    errorlist.add(error);

    errorlist.add(stack);

    print(error);
    print(stack);
    return true;
  };

  FlutterError.onError = (details) {
    print(details);
    errorlist.add(details);
  };

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
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
      m.add(("status", "message", "Data"));
    });

    super.initState();
  }

  var m = [];

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
      body: SingleChildScrollView(
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
                Text("Is Deveice connected ${isConnnete} "),
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
                    if (fingerData == null) {
                      ScaffoldMessenger.maybeOf(context)!.showSnackBar(
                          const SnackBar(content: Text("Select finger first")));
                      return;
                    }
                    eidaToolkitConnect.onClickFingerVerifyF(
                        fingerData!.fingerId!, fingerData!.fingerIndex!.index);
                  },
                  child: const Text("onClickFingerVerifyF"),
                ),
                const SizedBox(
                  height: 20,
                ),
                const Text("Status Data"),
                ListView.builder(
                  itemCount: m.length,
                  shrinkWrap: true,
                  itemBuilder: (context, index) {
                    return Row(
                      children: [
                        Text(
                          "Status= ${m[index].$1}",
                          style: const TextStyle(
                              fontSize: 14, color: Colors.black),
                        ),
                        const SizedBox(
                          width: 10,
                        ),
                        Text(
                          "Message= ${m[index].$2}",
                          style: const TextStyle(
                              fontSize: 14, color: Colors.black),
                        ),
                        Text(
                          "Data= ${m[index].$3}",
                          style: const TextStyle(
                              fontSize: 14, color: Colors.black),
                        ),
                      ],
                    );
                  },
                ),
                Row(
                  children: [
                    const Text("Errors Data"),
                  ],
                ),
                ListView.builder(
                  itemCount: errorlist.length,
                  shrinkWrap: true,
                  itemBuilder: (context, index) {
                    return Text(
                      "Error= ${errorlist[index]}",
                      style: const TextStyle(fontSize: 8, color: Colors.black),
                    );
                  },
                ),
              ],
            )),
      ),
    );
  }
}

class EdiaImpl extends EidaToolkitData {
  @override
  void onBiometricVerify(int? status, String? message, String? vgResponse) {
    m.add((status, message, vgResponse));
    // streamController.add([...m]);
  }

  @override
  void onCardReadComplete(
      int? status, String? message, Map<String?, String?>? cardPublicData) {
    m.add((status, message, cardPublicData));
    // streamController.add([...m]);
  }

  @override
  void onCheckCardStatus(int? status, String? message, String? xmlString) {
    m.add((status, message, xmlString));
    // streamController.add([...m]);
  }

  @override
  void onFingerIndexFetched(
      int? status, String? message, List<FingerData?>? fingers) {
    m.add((status, message, fingers?.length));
    // streamController.add([...m]);

    fingerData = fingers?.first;

    // fingerData = fingers?.first;
  }

  @override
  void onToolkitConnected(int? status, bool? isConnectFlag, String? message) {
    m.add((status, message, isConnectFlag));
    // streamController.add([...m]);
    isConnnete = isConnectFlag;
  }

  @override
  void onToolkitInitialized(bool? isSuccessful, String? statusMessage) {
    m.add((isSuccessful, statusMessage, ''));
    // streamController.add([...m]);
  }

  @override
  void statusListener(String? message) {
    m.add(("", message, ''));
    // streamController.add([...m]);
  }
}
