import 'package:flutter/material.dart';
import 'package:focui/src/menu/menu.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/foc_entity_feature/custom_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';
import 'dart:js' as js;

class Config {
  static String get baseUrl {
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

  // static String baseUrl =
  //     'http://192.168.100.114:8099'; // Replace with your API base URL
  static String appName =
      'Neo Foc Application'; // Name of the application, to be set by the using package
  static IconData appIcon = Icons
      .construction; // Path to the application icon, to be set by the using package
  static List<Menu> menuItems = [
    Menu(Icons.people, "Users", "/users", "FUSER"),
    Menu(Icons.document_scanner, "Countries", "/countries", "Country",
        widgetClassName: "CustomListView"),
    Menu(Icons.document_scanner, "Entities", "/entities", ""),
    Menu(Icons.memory_rounded, "Object Monitor", "/monitor/objects", ""),
  ];

  static Map<String, WidgetBuilder> entityListViewRegistry = {
    //'Instruments': (context) => InstrumentsListView(),
    // Add other entity-specific views here
  };

  /// Registry for widget classes that can be used in menu navigation
  /// Maps widget class names to factory functions that create the widget with a MetaEntity
  static Map<String, Widget Function(MetaEntity)> widgetClassRegistry = {
    'FocListView': (metaEntity) => FocListView(metaEntity: metaEntity),
    'CustomListView': (metaEntity) => CustomListView(metaEntity: metaEntity),
    // Add other custom widget classes here
  };
}
