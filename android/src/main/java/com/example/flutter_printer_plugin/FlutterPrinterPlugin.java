package com.example.flutter_printer_plugin;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import tspl.HPRTPrinterHelper;

/**
 * FlutterPrinterPlugin
 *
 * This plugin receives PDF bytes (as a List of byte arrays),
 * saves each to a temporary file, converts them to Bitmap(s)
 * and prints them using the HPRTPrinterHelper. Here we use
 * PortOpen to connect to a printer over Bluetooth.
 */
public class FlutterPrinterPlugin implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;
  private Context applicationContext;

  // Replace with your printer's actual Bluetooth address or name.
  // For example, if your printer Bluetooth address is "00:11:22:33:44:55",
  // then:
  // private static final String PRINTER_ADDRESS = "00:11:22:33:44:55";
  private static final String PRINTER_ADDRESS = "FD:58:FA:68:2B:96";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    applicationContext = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.example.flutter_printer_plugin/print");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) throws Exception {
    if (call.method.equals("printPDFList")) {
      ArrayList<byte[]> pdfBytesList = call.argument("pdfBytesList");
      if (pdfBytesList != null) {
        printPDFList(pdfBytesList);
        result.success("Printing started");
      } else {
        result.error("INVALID_ARGUMENT", "pdfBytesList is null", null);
      }
    } else {
      result.notImplemented();
    }
  }

  /**
   * Processes each PDF (given as a byte array) by writing it to a temporary file,
   * converting the file to Bitmap images, and issuing printing commands.
   */
  private void printPDFList(ArrayList<byte[]> pdfBytesList) throws Exception {
    // Use PortOpen to establish the connection with the printer.
    // The connection string here is for Bluetooth; adjust it if needed.
    int portStatus = HPRTPrinterHelper.PortOpen(applicationContext, "Bluetooth," + PRINTER_ADDRESS);
    if (portStatus == -1) {
      // The printer connection failed.
      // Optionally handle the error (e.g., send an error message back via MethodChannel).
      return;
    }

    // Process each PDF (sent as a byte array)
    for (byte[] pdfData : pdfBytesList) {
      try {
        // Create a temporary file to store the PDF bytes.
        File tempPdfFile = File.createTempFile("temp_pdf", ".pdf", applicationContext.getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempPdfFile);
        fos.write(pdfData);
        fos.flush();
        fos.close();

        // Run PDF conversion and printing in a background thread.
        new Thread(() -> {
          try {
            // Convert the PDF file to a list of Bitmap images.
            // (The Utility.pdfToBitmap is assumed to accept a File and return a List<Bitmap>.)
            List<Bitmap> bitmaps = Utility.pdfToBitmap(applicationContext, tempPdfFile, "1", 576);
            if (bitmaps == null || bitmaps.isEmpty() || bitmaps.get(0) == null) {
              // Optionally handle the conversion error.
              return;
            }
            // Loop through each page (Bitmap) and execute printing commands.
            for (Bitmap bitmap : bitmaps) {
              int printAreaWidth = bitmap.getWidth() / 8;
              int printAreaHeight = bitmap.getHeight() / 8;
              HPRTPrinterHelper.printAreaSize("" + printAreaWidth, "" + printAreaHeight);
              HPRTPrinterHelper.CLS();
              HPRTPrinterHelper.printImage("0", "0", bitmap, true, true, 1);
              if (HPRTPrinterHelper.Print("1", "1") == -1) {
                // Optionally handle a printing error.
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            // Delete the temporary file after processing.
            tempPdfFile.delete();
          }
        }).start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}