import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_printer_plugin/flutter_printer_plugin.dart';
import 'package:flutter_printer_plugin/flutter_printer_plugin_platform_interface.dart';
import 'package:flutter_printer_plugin/flutter_printer_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterPrinterPluginPlatform
    with MockPlatformInterfaceMixin
    implements FlutterPrinterPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterPrinterPluginPlatform initialPlatform = FlutterPrinterPluginPlatform.instance;

  test('$MethodChannelFlutterPrinterPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterPrinterPlugin>());
  });

  test('getPlatformVersion', () async {
    FlutterPrinterPlugin flutterPrinterPlugin = FlutterPrinterPlugin();
    MockFlutterPrinterPluginPlatform fakePlatform = MockFlutterPrinterPluginPlatform();
    FlutterPrinterPluginPlatform.instance = fakePlatform;

    expect(await flutterPrinterPlugin.getPlatformVersion(), '42');
  });
}
