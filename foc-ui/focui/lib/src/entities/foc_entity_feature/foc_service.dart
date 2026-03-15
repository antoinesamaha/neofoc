import 'dart:convert';
import 'package:focui/main.dart';
import 'package:focui/src/app_constants.dart';
import 'package:http/http.dart' as http;

import '../meta_feature/meta_entity.dart';
import '../meta_feature/meta_service.dart';
import 'foc_cache.dart';
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
      final entities = data.map((item) => FocEntity.fromJson(metaEntity, item)).toList();
      if (metaEntity.isListInCache) {
        FocCache().populate(metaEntity.storageName, entities);
      }
      return entities;
    } else {
      throw Exception('Failed to load items');
    }
  }

  /// Resolves a foreign key field on [entity] to its full [FocEntity].
  /// Checks the cache first; falls back to a GET if not found.
  Future<FocEntity?> resolveField(FocEntity entity, String fieldName) async {
    final metaField = entity.metaEntity.fields
        .where((f) => f.name == fieldName)
        .firstOrNull;

    if (metaField == null) {
      print('[resolveField] metaField "$fieldName" not found in ${entity.metaEntity.name}. Known fields: ${entity.metaEntity.fields.map((f) => f.name).toList()}');
      return null;
    }
    if (!metaField.isForeignKey) {
      print('[resolveField] "$fieldName" has no storageName (not a FK)');
      return null;
    }

    final rawValue = entity[fieldName];
    if (rawValue == null) {
      print('[resolveField] "$fieldName" value is null');
      return null;
    }
    final id = rawValue is int ? rawValue : int.tryParse(rawValue.toString());
    if (id == null) {
      print('[resolveField] "$fieldName" value "$rawValue" is not a valid id');
      return null;
    }

    final storageName = metaField.storageName!;

    if (FocCache().has(storageName, id)) {
      return FocCache().get(storageName, id);
    }

    final referencedMeta = MetaService().getEntityByName(storageName);
    if (referencedMeta == null) {
      print('[resolveField] MetaEntity "$storageName" not found in MetaService');
      return null;
    }

    final fetched = await fetchItemDetails(referencedMeta, id.toString());
    if (referencedMeta.isListInCache) {
      FocCache().populate(storageName, [fetched]);
    }
    return fetched;
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

  Future<Map<String, dynamic>> searchItems(
      MetaEntity metaEntity, Map<String, dynamic> searchBody) async {
    String fullUrl = '$url${metaEntity.storageName}/search';
    final response = await http.post(
      Uri.parse(fullUrl),
      headers: headers(isJson: true),
      body: json.encode(searchBody),
    );

    if (response.statusCode == 200) {
      return json.decode(response.body) as Map<String, dynamic>;
    } else {
      final errorBody = json.decode(response.body);
      final message = errorBody['message'] ?? 'Search failed';
      throw Exception(message);
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
