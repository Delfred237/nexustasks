import 'package:flutter/material.dart';

/// Convertit une couleur hex backend ("#4f46e5" ou "4f46e5") en Color Flutter.
Color colorFromHex(String hex) {
  final buffer = StringBuffer();
  if (hex.length == 6 || hex.length == 7) buffer.write('ff');
  buffer.write(hex.replaceFirst('#', ''));
  return Color(int.parse(buffer.toString(), radix: 16));
}
