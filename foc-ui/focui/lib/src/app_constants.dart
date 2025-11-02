import 'dart:js' as js;

class AppConstants {
  static String get apiUrl {
    try {
      // Try to get from JavaScript window.ENV
      final env = js.context['ENV'];
      if (env != null && env['API_URL'] != null) {
        final url = env['API_URL'] as String;
        // Make sure it's not the template placeholder
        if (url.isNotEmpty && !url.startsWith('\${')) {
          return url;
        }
      }
    } catch (e) {
      print('Could not load API_URL from environment: $e');
    }

    // Fallback to default
    return 'http://localhost:8099';
  }

  static const int timeoutDuration = 5000;
  static const String appName = 'NeoFoc';

  // For debugging
  static void printConfig() {
    print('API URL: ${apiUrl}');
  }
}
