package com.meeting.flutter_screen_led

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.meeting.flutter_screen_led.handlers.FlutterScreenLedHandler
import com.ys.rkapi.MyManager
import com.ys.rkapi.Utils.GPIOUtils.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterScreenLedPlugin */
class FlutterScreenLedPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  //声明 MyManager 对象
  private lateinit var manager: MyManager

  private val uiThreadHandler = Handler(Looper.getMainLooper())

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_screen_led")
    channel.setMethodCallHandler(this)
  }

  companion object{
    const val tag = "FlutterScreenLedPlugin"

    @JvmStatic
    fun registerWith(registrar: Registrar){
      FlutterScreenLedHandler.setContext(registrar.activity())
      val channel = MethodChannel(registrar.messenger(), "flutter_screen_led")
      channel.setMethodCallHandler(FlutterScreenLedPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "initDevice") { //初始化
      initDeviceApi(result)
    } else if (call.method == "switchLed") { //切换灯
      val led_color = call.argument<String>("color")!!
      switchLed(result, led_color)
    } else {
      result.notImplemented()
    }
  }

  private fun  initDeviceApi(@NonNull result: Result){
    manager = MyManager.getInstance(FlutterScreenLedHandler.getContext())
    manager.bindAIDLService(FlutterScreenLedHandler.getContext())
    manager.setConnectClickInterface {
      if (Integer.valueOf(manager.firmwareVersion) < 4) println("正在使用旧版本api") else println(
        "正在使用新版本api"
      )
      val b = manager.androidModle
      if (b.contains("3368")) {
        GpioUtils.upgradeRootPermissionForExport()
        if (GpioUtils.exportGpio(90)) {
          GpioUtils.upgradeRootPermissionForGpio(90)
          GpioUtils.setGpioDirection(90, 0)
        }
        if (GpioUtils.exportGpio(111)) {
          GpioUtils.upgradeRootPermissionForGpio(111)
          GpioUtils.setGpioDirection(111, 0)
        }
        if (GpioUtils.exportGpio(109)) {
          GpioUtils.upgradeRootPermissionForGpio(109)
          GpioUtils.setGpioDirection(109, 0)
        }
        GpioUtils.writeGpioValue(90, "0")
        GpioUtils.writeGpioValue(111, "0")
        GpioUtils.writeGpioValue(109, "0")
      } else if (b.contains("3288")) {
        GpioUtils.upgradeRootPermissionForExport()
        if (GpioUtils.exportGpio(138)) {
          GpioUtils.upgradeRootPermissionForGpio(138)
          GpioUtils.setGpioDirection(138, 0)
        }
        if (GpioUtils.exportGpio(68)) {
          GpioUtils.upgradeRootPermissionForGpio(68)
          GpioUtils.setGpioDirection(68, 0)
        }
        if (GpioUtils.exportGpio(69)) {
          GpioUtils.upgradeRootPermissionForGpio(69)
          GpioUtils.setGpioDirection(69, 0)
        }
        GpioUtils.writeGpioValue(138, "0")
        GpioUtils.writeGpioValue(68, "0")
        GpioUtils.writeGpioValue(69, "0")
      } else if (b.contains("3128")) {
        GpioUtils.upgradeRootPermissionForExport()
        if (GpioUtils.exportGpio(11)) {
          GpioUtils.upgradeRootPermissionForGpio(11)
          GpioUtils.setGpioDirection(11, 0)
        }
        if (GpioUtils.exportGpio(12)) {
          GpioUtils.upgradeRootPermissionForGpio(12)
          GpioUtils.setGpioDirection(12, 0)
        }
        if (GpioUtils.exportGpio(13)) {
          GpioUtils.upgradeRootPermissionForGpio(13)
          GpioUtils.setGpioDirection(13, 0)
        }
        GpioUtils.writeGpioValue(11, "0")
        GpioUtils.writeGpioValue(12, "0")
        GpioUtils.writeGpioValue(13, "0")
      } else if (b.contains("3568")) {
        manager.upgradeRootPermissionForExport()
        val ioList = arrayOf("1", "2", "3", "4")
        for (io in ioList) {
          if (manager.exportGpio(io.toInt())) {
            manager.upgradeRootPermissionForGpio(io.toInt())
            manager.setGpioDirection(io.toInt(), 0)
          }
          manager.writeGpioValue(io.toInt(), "0")
        }
      }
    }
    result.success("init success")
  }

  private fun switchLed(@NonNull result: Result, color: String){
    manager.upgradeRootPermissionForExport()
    val modle = manager.androidModle
    when (color){
      "red" -> { // 红色
        //红色
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(111, "1")
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(138, "1")
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(13, "1")
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(1, "1")
        }
      }
      "green" -> { // 绿色
        //绿色
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(109, "1")
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(69, "1")
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //绿色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(11, "1")
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(2, "1")
        }
      }
      "blue" -> { // 蓝色
        //蓝色
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(90, "1")
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(68, "1")
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(12, "1")
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(3, "1")
        }
      }
      "yellow" -> { // 黄色
        //黄色
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(111, "1") //红色
          GpioUtils.writeGpioValue(109, "1") //绿色
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(69, "1") //绿色
          GpioUtils.writeGpioValue(138, "1") //红色
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(11, "1") //绿色
          GpioUtils.writeGpioValue(13, "1") //红色
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(1, "1")
          manager.writeGpioValue(2, "1")
        }
      }
      "purple" -> {
        //紫色
        if (modle.contains("3368")){
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(90, "1") //蓝色
          GpioUtils.writeGpioValue(111, "1") //红色
        } else if (modle.contains("3288")){
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(68, "1") //蓝色
          GpioUtils.writeGpioValue(138, "1") //红色
        } else if (modle.contains("3128")){
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(12, "1") //蓝色
          GpioUtils.writeGpioValue(13, "1") //红色
        } else if (modle.contains("3568")){
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(1, "1")
          manager.writeGpioValue(3, "1")
        }
      }
      "lightblue" -> { // 浅蓝色
        //浅蓝色
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(90, "1") //蓝色
          GpioUtils.writeGpioValue(109, "1") //绿色
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(68, "1") //蓝色
          GpioUtils.writeGpioValue(69, "1") //绿色
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(12, "1") //蓝色
          GpioUtils.writeGpioValue(11, "1") //绿色
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(2, "1")
          manager.writeGpioValue(3, "1")
        }
      }
      "pink" -> { // 红色
        //粉色
        //粉色
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
          GpioUtils.writeGpioValue(90, "1") //蓝色
          GpioUtils.writeGpioValue(111, "1") //红色
          GpioUtils.writeGpioValue(109, "1") //绿色
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
          GpioUtils.writeGpioValue(68, "1") //蓝色
          GpioUtils.writeGpioValue(69, "1") //绿色
          GpioUtils.writeGpioValue(138, "1") //红色
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
          GpioUtils.writeGpioValue(12, "1") //蓝色
          GpioUtils.writeGpioValue(11, "1") //绿色
          GpioUtils.writeGpioValue(13, "1") //红色
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(1, "1")
          manager.writeGpioValue(2, "1")
          manager.writeGpioValue(3, "1")
        }
      }
      "close" -> { // 关闭灯
        if (modle.contains("3368")) {
          GpioUtils.writeGpioValue(90, "0") //蓝色
          GpioUtils.writeGpioValue(111, "0") //红色
          GpioUtils.writeGpioValue(109, "0") //绿色
        } else if (modle.contains("3288")) {
          GpioUtils.writeGpioValue(68, "0") //蓝色
          GpioUtils.writeGpioValue(69, "0") //绿色
          GpioUtils.writeGpioValue(138, "0") //红色
        } else if (modle.contains("3128")) {
          GpioUtils.writeGpioValue(12, "0") //蓝色
          GpioUtils.writeGpioValue(11, "0") //绿色
          GpioUtils.writeGpioValue(13, "0") //红色
        } else if (modle.contains("3568")) {
          manager.writeGpioValue(1, "0") //红色
          manager.writeGpioValue(2, "0") //绿色
          manager.writeGpioValue(3, "0") //蓝色
          manager.writeGpioValue(1, "0")
          manager.writeGpioValue(2, "0")
          manager.writeGpioValue(3, "0")
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    FlutterScreenLedHandler.setContext(binding.activity)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }
}
