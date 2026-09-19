import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nexustasks_mobile/core/utils/color_utils.dart';

void main() {
  group('colorFromHex', () {
    test('parse un hex 6 chiffres avec dièse', () {
      expect(colorFromHex('#4f46e5'), const Color(0xFF4F46E5));
    });

    test('parse un hex sans dièse', () {
      expect(colorFromHex('10b981'), const Color(0xFF10B981));
    });
  });
}
