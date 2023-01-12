import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_screen_led_method_channel.dart';

abstract class FlutterScreenLedPlatform extends PlatformInterface {
  /// Constructs a FlutterScreenLedPlatform.
  FlutterScreenLedPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterScreenLedPlatform _instance = MethodChannelFlutterScreenLed();

  /// The default instance of [FlutterScreenLedPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterScreenLed].
  static FlutterScreenLedPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterScreenLedPlatform] when
  /// they register themselves.
  static set instance(FlutterScreenLedPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> initDeviceLed() {
    throw UnimplementedError('initDeviceLed() has not been implemented.');
  }

  Future<String?> switchDeviceLed(String color) {
    throw UnimplementedError('switchDeviceLed() has not been implemented.');
  }
}
