import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_screen_led_platform_interface.dart';

enum LedColorType {
  red,
  green,
  blue,
  yellow,
  purple,
  lightblue,
  pink,
  close // 关闭全部灯
}

/// An implementation of [FlutterScreenLedPlatform] that uses method channels.
class MethodChannelFlutterScreenLed extends FlutterScreenLedPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_screen_led');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> initDeviceLed() async {
    final result = await methodChannel.invokeMethod<String>('initDevice');
    return result;
  }

  @override
  Future<String?> switchDeviceLed(String color) async {
    print('color: ${color.toString()}');
    final result = await methodChannel.invokeMethod<String>('switchLed', {"color": color});
    return result;
  }

}
