/// Modèle utilisateur minimal.
/// Sera migré vers freezed/json_serializable en Phase 22.2
/// avec le reste des modèles (Task, Category, ...).
class User {
  const User({
    required this.publicId,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.role,
    required this.emailVerified,
    this.avatarUrl,
  });

  final String publicId;
  final String firstName;
  final String lastName;
  final String email;
  final String role;
  final bool emailVerified;
  final String? avatarUrl;

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      publicId: json['publicId'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      email: json['email'] as String,
      role: json['role'] as String,
      emailVerified: json['emailVerified'] as bool,
      avatarUrl: json['avatarUrl'] as String?,
    );
  }
}
