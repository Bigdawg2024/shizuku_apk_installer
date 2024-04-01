package dev.re7gog.shizuku_apk_installer

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/** ShizukuApkInstallerPlugin */
class ShizukuApkInstallerPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var worker : ShizukuWorker

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "shizuku_apk_installer")
    channel.setMethodCallHandler(this)
    worker = ShizukuWorker()
    worker.init()
  }

  @OptIn(DelicateCoroutinesApi::class)
  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "checkPermission") {
      GlobalScope.async { worker.checkPermission(result) }
    } else if (call.method == "getPlatformVersion") {
      result.success(android.os.Build.VERSION.SDK_INT.toString())
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    worker.exit()
  }
}