import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'edia/eida_toolkit.dart';

var errorlist = [];

void main() {
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

class _MyHomePageState extends State<MyHomePage> implements EidaToolkitData {
  EidaToolkitConnect eidaToolkitConnect = EidaToolkitConnect();

  List<(dynamic, String)> messages = [];

  @override
  void initState() {
    EidaToolkitData.setup(_MyHomePageState());
    Future.delayed(Duration.zero).then((value) {
      getPermission();
    });
    super.initState();
  }

  getPermission() async {
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
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              MaterialButton(
                onPressed: () {
                  eidaToolkitConnect.connectAndInitializeF();
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
                  eidaToolkitConnect.onClickFingerVerifyF();
                },
                child: const Text("onClickFingerVerifyF"),
              ),
              const SizedBox(
                height: 20,
              ),
              const Text("Status Data"),
              ListView.builder(
                itemCount: messages.length,
                shrinkWrap: true,
                itemBuilder: (context, index) {
                  return Row(
                    children: [
                      Text(
                        "Status= ${messages[index].$1}",
                        style:
                            const TextStyle(fontSize: 14, color: Colors.black),
                      ),
                      const SizedBox(
                        width: 10,
                      ),
                      Text(
                        "Message= ${messages[index].$2}",
                        style:
                            const TextStyle(fontSize: 14, color: Colors.black),
                      ),
                    ],
                  );
                },
              ),
              Row(
                children: [
                  const Text("Errors Data"),
                  MaterialButton(
                    onPressed: () {
                      setState(() {});
                    },
                    child: const Text("Refersh"),
                  ),
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
          ),
        ),
      ),
    );
  }

  @override
  void onBiometricVerify(int status, String message, String vgResponse) {
    messages.add((status, message));
    if (mounted) setState(() {});
  }

  @override
  void onCardReadComplete(int status, String message, String cardPublicData) {
    messages.add((status, message));
    if (mounted) setState(() {});
  }

  @override
  void onCheckCardStatus(int status, String message, String xmlString) {
    messages.add((status, message));
    if (mounted) setState(() {});
  }

  @override
  void onFingerIndexFetched(int status, String message, String fingers) {
    messages.add((status, message));
    if (mounted) setState(() {});
  }

  @override
  void onToolkitConnected(int status, bool isConnectFlag, String message) {
    messages.add((status, message));
    if (mounted) setState(() {});
  }

  @override
  void onToolkitInitialized(bool isSuccessful, String statusMessage) {
    messages.add((isSuccessful.toString(), statusMessage));
    if (mounted) setState(() {});
  }

  @override
  void statusListener(String message) {
    messages.add(("statusListener", message));
    if (mounted) setState(() {});
  }
}
