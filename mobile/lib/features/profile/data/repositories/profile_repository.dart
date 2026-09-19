import 'package:dio/dio.dart';
import 'package:image_picker/image_picker.dart';
import 'package:nexustasks_mobile/features/auth/models/user.dart';


class ChangePasswordResponse {
  const ChangePasswordResponse({
    required this.message,
    required this.otherSessionsRevoked,
  });

  final String message;
  final bool otherSessionsRevoked;

  factory ChangePasswordResponse.fromJson(Map<String, dynamic> json) =>
      ChangePasswordResponse(
        message: (json['message'] as String?) ?? 'Password changed',
        otherSessionsRevoked: (json['otherSessionsRevoked'] as bool?) ?? false,
      );
}

class ProfileRepository {
  const ProfileRepository(this._dio);

  final Dio _dio;

  Future<User> updateProfile({
    required String firstName,
    required String lastName,
  }) async {
    final response = await _dio.patch(
      '/users/me',
      data: {'firstName': firstName, 'lastName': lastName},
    );
    return User.fromJson(response.data as Map<String, dynamic>);
  }

  Future<ChangePasswordResponse> changePassword({
    required String currentPassword,
    required String newPassword,
  }) async {
    final response = await _dio.patch(
      '/users/me/password',
      data: {'currentPassword': currentPassword, 'newPassword': newPassword},
    );
    return ChangePasswordResponse.fromJson(
      response.data as Map<String, dynamic>,
    );
  }

  /// Le backend attend le champ multipart nommé "file".
  Future<String> uploadAvatar(XFile file) async {
    final formData = FormData.fromMap({
      'file': await MultipartFile.fromFile(file.path, filename: file.name),
    });
    final response = await _dio.post('/users/me/avatar', data: formData);
    final data = response.data as Map<String, dynamic>;
    return (data['avatarUrl'] as String?) ?? '';
  }

  Future<void> deleteAvatar() async {
    await _dio.delete('/users/me/avatar');
  }
}
