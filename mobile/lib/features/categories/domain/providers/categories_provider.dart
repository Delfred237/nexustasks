import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/core/providers/core_providers.dart';

import '../../data/repositories/category_repository.dart';
import '../models/category.dart';

final categoryRepositoryProvider = Provider<CategoryRepository>(
  (ref) => CategoryRepository(ref.watch(dioProvider)),
);

final categoriesProvider = FutureProvider<List<Category>>((ref) async {
  return ref.watch(categoryRepositoryProvider).getCategories();
});