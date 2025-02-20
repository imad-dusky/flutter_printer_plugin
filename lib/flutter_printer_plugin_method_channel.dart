import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_printer_plugin_platform_interface.dart';

/// An implementation of [FlutterPrinterPluginPlatform] that uses method channels.
class MethodChannelFlutterPrinterPlugin extends FlutterPrinterPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_printer_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
