import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';

import '../models/category.dart';

final categoriesProvider = FutureProvider<List<Category>>((ref) async {
  final dio = ref.watch(dioProvider);
  final response = await dio.get(
    '/categories',
    queryParameters: {'page': 0, 'size': 100, 'sort': 'name,asc'},
  );
  final data = response.data as Map<String, dynamic>;
  return (data['content'] as List? ?? [])
      .map((e) => Category.fromJson(e as Map<String, dynamic>))
      .toList();
});
