import 'package:dio/dio.dart';

import '../../domain/models/category.dart';

class CategoryRequest {
  const CategoryRequest({
    required this.name,
    this.description,
    required this.color,
  });

  final String name;
  final String? description;
  final String color;

  Map<String, dynamic> toJson() => {
    'name': name,
    'description': description,
    'color': color,
  };
}

class CategoryRepository {
  const CategoryRepository(this._dio);

  final Dio _dio;

  Future<List<Category>> getCategories({int page = 0, int size = 100}) async {
    final response = await _dio.get(
      '/categories',
      queryParameters: {'page': page, 'size': size, 'sort': 'name,asc'},
    );
    final data = response.data as Map<String, dynamic>;
    return (data['content'] as List? ?? [])
        .map((e) => Category.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<Category> createCategory(CategoryRequest request) async {
    final response = await _dio.post('/categories', data: request.toJson());
    return Category.fromJson(response.data as Map<String, dynamic>);
  }

  Future<Category> updateCategory(
    String publicId,
    CategoryRequest request,
  ) async {
    final response = await _dio.put(
      '/categories/$publicId',
      data: request.toJson(),
    );
    return Category.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteCategory(String publicId) async {
    await _dio.delete('/categories/$publicId');
  }
}
