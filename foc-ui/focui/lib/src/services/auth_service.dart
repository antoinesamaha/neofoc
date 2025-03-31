import 'dart:convert';

import 'package:focui/src/settings/config.dart';
import 'package:http/http.dart' as http; // For making HTTP requests

class AuthService {
  String accessToken = '';

  Future<bool> isLoggedIn() async {
    return accessToken.isNotEmpty;
  }

  Future<bool> login(String username, String password) async {
    accessToken = '';
    // In a real app, you would make an API call here to authenticate.
    // For this example, we'll just simulate a successful login if the username and password are not empty.
    if (username.isNotEmpty && password.isNotEmpty) {
      final response = await http.post(
        Uri.parse('${Config.baseUrl}/foc/auth/login'), // Replace with your API URL
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'username': username, 'password': password}),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        accessToken = data['access_token'];
        if (accessToken.isNotEmpty) {
          return true;
        }
      }
    }
    return false;
  }

  Future<void> logout() async {
    accessToken = '';
  }
}
