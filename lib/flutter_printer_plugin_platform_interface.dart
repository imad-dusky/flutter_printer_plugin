import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_printer_plugin_method_channel.dart';

abstract class FlutterPrinterPluginPlatform extends PlatformInterface {
  /// Constructs a FlutterPrinterPluginPlatform.
  FlutterPrinterPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterPrinterPluginPlatform _instance = MethodChannelFlutterPrinterPlugin();

  /// The default instance of [FlutterPrinterPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterPrinterPlugin].
  static FlutterPrinterPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterPrinterPluginPlatform] when
  /// they register themselves.
  static set instance(FlutterPrinterPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
