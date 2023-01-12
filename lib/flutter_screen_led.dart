
import 'package:flutter_screen_led/flutter_screen_led_method_channel.dart';

import 'flutter_screen_led_platform_interface.dart';

class FlutterScreenLed {
  Future<String?> getPlatformVersion() {
    return FlutterScreenLedPlatform.instance.getPlatformVersion();
  }

  ///
  /// 设备 api 初始化
  ///
  Future<String?> initDeviceLed() {
    return FlutterScreenLedPlatform.instance.initDeviceLed();
  }

  ///
  /// 切换 led 演色
  /// color 使用 [LedColorType]的 name 值
  ///
  Future<String?> switchDeviceLed(String color) {
    return FlutterScreenLedPlatform.instance.switchDeviceLed(color);
  }

}
