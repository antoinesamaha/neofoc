import 'dart:convert';
import 'package:focui/main.dart';
import 'package:focui/src/app_constants.dart';
import 'package:focui/src/auth/auth_service.dart';
import 'package:http/http.dart' as http;

class FocDescCount {
  final String name;
  final int count;

  const FocDescCount({required this.name, required this.count});

  factory FocDescCount.fromJson(Map<String, dynamic> json) => FocDescCount(
        name: json['name'] as String? ?? '',
        count: json['count'] as int? ?? -1,
      );
}

class MonitorResult {
  final int total;
  final List<FocDescCount> items;

  const MonitorResult({required this.total, required this.items});
}

class MonitorService {
  static String get url => '${AppConstants.apiUrl}/meta/monitor/objects';

  Map<String, String> _headers() {
    final authService = getIt<AuthService>();
    return {'Authorization': 'Bearer ${authService.accessToken}'};
  }

  Future<MonitorResult> fetchObjectCounts() async {
    final response = await http.get(Uri.parse(url), headers: _headers());
    if (response.statusCode == 200) {
      final body = jsonDecode(response.body) as Map<String, dynamic>;
      final total = body['total'] as int? ?? 0;
      final items = (body['data'] as List<dynamic>)
          .map((e) => FocDescCount.fromJson(e as Map<String, dynamic>))
          .toList();
      return MonitorResult(total: total, items: items);
    }
    throw Exception('Failed to load object counts (${response.statusCode})');
  }
}
