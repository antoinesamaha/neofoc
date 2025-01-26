import 'dart:convert';
import 'package:focui/src/app_constants.dart';
import 'package:http/http.dart' as http;

import '../meta_feature/meta_entity.dart';
import 'foc_entity.dart';

class FocService {
  static const String url = '${AppConstants.apiUrl}/foc/obj/';

  Future<List<FocEntity>> fetchItems(MetaEntity metaEntity) async {
    String fullUrl = '$url${metaEntity.storageName}';
    final response = await http.get(Uri.parse(fullUrl));

    if (response.statusCode == 200) {
      Map<String, dynamic> jsonResponse = json.decode(response.body);
      List<dynamic> data = jsonResponse['data'];
      return data.map((item) => FocEntity.fromJson(item)).toList();
    } else {
      throw Exception('Failed to load items');
    }
  }

  Future<FocEntity> fetchItemDetails(MetaEntity metaEntity, String itemId) async {
    String fullUrl = '$url${metaEntity.storageName}/$itemId';
    final response = await http.get(Uri.parse(fullUrl));

    if (response.statusCode == 200) {
      Map<String, dynamic> jsonResponse = json.decode(response.body);
      return FocEntity.fromJson(jsonResponse);
    } else {
      throw Exception('Failed to load item details');
    }
  }

  Future<void> updateItem(MetaEntity metaEntity, FocEntity focEntity) async {
    String fullUrl = '$url${metaEntity.storageName}/${focEntity.id}';
    final response = await http.put(
      Uri.parse(fullUrl),
      headers: {'Content-Type': 'application/json'},
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
      headers: {'Content-Type': 'application/json'},
      body: json.encode(focEntity.toJson()),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to insert item');
    }
  }
}
