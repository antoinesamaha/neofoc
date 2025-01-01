import 'dart:convert';
import 'package:focui/src/app_constants.dart';
import 'package:http/http.dart' as http;

import 'meta_entity.dart';

class MetaService {
  // Private constructor
  MetaService._internal();

  // Static instance
  static final MetaService _instance = MetaService._internal();

  // Factory constructor
  factory MetaService() => _instance;

  static const String url = '${AppConstants.apiUrl}/meta/entities';
  Map<String, MetaEntity>? _entities;

  Future<Map<String, MetaEntity>> fetchItems() async {
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        Map<String, dynamic> jsonResponse = json.decode(response.body);
        List<dynamic> data = jsonResponse['data'];
        _entities = {};
        for (var item in data) {
          MetaEntity entity = MetaEntity.fromJson(item);
          print('entity name: ${entity.name}');
          _entities![entity.name] = entity;
        }
        return _entities!;
      } else {
        throw Exception('Failed to load items');
      }
    } catch (e) {
      print('Error: $e');
      throw Exception('Failed to load items: $e');
    }
  }

  MetaEntity? getEntityByName(String name) {
    if (_entities == null) {
      throw Exception('Entities not loaded');
    }
    return _entities![name];
  }
}
