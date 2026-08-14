import 'dart:convert';

import 'package:focui/src/settings/config.dart';
import 'package:http/http.dart' as http;

class AuthService {
  String accessToken = '';
  String currentUsername = '';

  Future<bool> isLoggedIn() async {
    return accessToken.isNotEmpty;
  }

  Future<bool> login(String username, String password) async {
    accessToken = '';
    currentUsername = '';
    if (username.isNotEmpty && password.isNotEmpty) {
      final response = await http.post(
        Uri.parse('${Config.baseUrl}/foc/auth/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'username': username, 'password': password}),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        accessToken = data['access_token'];
        if (accessToken.isNotEmpty) {
          currentUsername = username;
          return true;
        }
      }
    }
    return false;
  }

  /// Changes the password for [username].
  /// [oldPassword] is required for self-service; omit (pass null) for admin reset.
  /// Throws an Exception with a human-readable message on failure.
  Future<void> changePassword(
      String username, String? oldPassword, String newPassword) async {
    final body = <String, dynamic>{
      'username': username,
      'newPassword': newPassword,
    };
    if (oldPassword != null && oldPassword.isNotEmpty) {
      body['oldPassword'] = oldPassword;
    }

    final response = await http.post(
      Uri.parse('${Config.baseUrl}/foc/auth/change-password'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      },
      body: jsonEncode(body),
    );

    if (response.statusCode != 200) {
      final data = jsonDecode(response.body);
      throw Exception(data['message'] ?? 'Failed to change password');
    }
  }

  Future<void> logout() async {
    accessToken = '';
    currentUsername = '';
  }
}
