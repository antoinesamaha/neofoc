import 'dart:convert';
import 'package:http/http.dart' as http;

import 'foc_entity.dart';

class FocService {
  static const String url = 'http://localhost:8099/foc/obj/country';

  Future<List<FocEntity>> fetchItems() async {
    final response = await http.get(Uri.parse(url));

    if (response.statusCode == 200) {
      Map<String, dynamic> jsonResponse = json.decode(response.body);
      List<dynamic> data = jsonResponse['data'];
      return data.map((item) => FocEntity.fromJson(item)).toList();
    } else {
      throw Exception('Failed to load items');
    }
  }
}
