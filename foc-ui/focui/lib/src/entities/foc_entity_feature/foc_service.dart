import 'dart:convert';
import 'package:focui/main.dart';
import 'package:focui/src/app_constants.dart';
import 'package:http/http.dart' as http;

import '../meta_feature/meta_entity.dart';
import 'foc_entity.dart';
import '../../auth/auth_service.dart'; // Import AuthService

class FocService {
  static String url = '${AppConstants.apiUrl}/foc/obj/';

  // Helper to get headers with Authorization
  Map<String, String> headers({bool isJson = false}) {
    final authService = getIt<AuthService>();
    final headers = <String, String>{
      'Authorization': 'Bearer ${authService.accessToken}',
    };
    if (isJson) {
      headers['Content-Type'] = 'application/json';
    }
    return headers;
  }

  Future<List<FocEntity>> fetchItems(MetaEntity metaEntity) async {
    String fullUrl = '$url${metaEntity.storageName}';
    final response = await http.get(
      Uri.parse(fullUrl),
      headers: headers(),
    );

    if (response.statusCode == 200) {
      Map<String, dynamic> jsonResponse = json.decode(response.body);
      List<dynamic> data = jsonResponse['data'];
      return data.map((item) => FocEntity.fromJson(metaEntity, item)).toList();
    } else {
      throw Exception('Failed to load items');
    }
  }

  Future<FocEntity> fetchItemDetails(
      MetaEntity metaEntity, String itemId) async {
    String fullUrl = '$url${metaEntity.storageName}/$itemId';
    final response = await http.get(
      Uri.parse(fullUrl),
      headers: headers(),
    );

    if (response.statusCode == 200) {
      Map<String, dynamic> jsonResponse = json.decode(response.body);
      return FocEntity.fromJson(metaEntity, jsonResponse);
    } else {
      throw Exception('Failed to load item details');
    }
  }

  Future<void> updateItem(MetaEntity metaEntity, FocEntity focEntity) async {
    String fullUrl = '$url${metaEntity.storageName}/${focEntity.id}';
    final response = await http.put(
      Uri.parse(fullUrl),
      headers: headers(isJson: true),
      body: json.encode(focEntity.toJson()),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to update item');
    }
  }

  Future<void> insertItem(MetaEntity metaEntity, FocEntity focEntity) async {
    String fullUrl = '$url${metaEntity.storageName}';
    final response = await http.post(
      Uri.parse(fullUrl),
      headers: headers(isJson: true),
      body: json.encode(focEntity.toJson()),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to insert item');
    }
  }

  Future<void> deleteItem(MetaEntity metaEntity, int itemId) async {
    String fullUrl = '$url${metaEntity.storageName}/$itemId';
    final response = await http.delete(
      Uri.parse(fullUrl),
      headers: headers(),
    );

    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to delete item');
    }
  }
}
