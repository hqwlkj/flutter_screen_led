import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_screen_led/flutter_screen_led.dart';
import 'package:flutter_screen_led/flutter_screen_led_platform_interface.dart';
import 'package:flutter_screen_led/flutter_screen_led_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterScreenLedPlatform
    with MockPlatformInterfaceMixin
    implements FlutterScreenLedPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterScreenLedPlatform initialPlatform = FlutterScreenLedPlatform.instance;

  test('$MethodChannelFlutterScreenLed is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterScreenLed>());
  });

  test('getPlatformVersion', () async {
    FlutterScreenLed flutterScreenLedPlugin = FlutterScreenLed();
    MockFlutterScreenLedPlatform fakePlatform = MockFlutterScreenLedPlatform();
    FlutterScreenLedPlatform.instance = fakePlatform;

    expect(await flutterScreenLedPlugin.getPlatformVersion(), '42');
  });
}
