class Category {
  const Category({
    required this.publicId,
    required this.name,
    required this.color,
    this.description,
  });

  final String publicId;
  final String name;
  final String color;
  final String? description;

  factory Category.fromJson(Map<String, dynamic> json) => Category(
    publicId: json['publicId'] as String,
    name: json['name'] as String,
    color: (json['color'] as String?) ?? '#6b7280',
    description: json['description'] as String?,
  );
}
