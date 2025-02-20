import 'dart:typed_data';
import 'package:flutter/services.dart';

class FlutterPrinterPlugin {
  static const MethodChannel _channel = MethodChannel(
    'com.example.flutter_printer_plugin/print',
  );

  /// Sends a list of PDF files (as Uint8List) to the native printing logic.
  static Future<String?> printPDFList(List<Uint8List> pdfBytesList) async {
    final String? result = await _channel.invokeMethod('printPDFList', {
      'pdfBytesList': pdfBytesList,
    });
    return result;
  }
}
