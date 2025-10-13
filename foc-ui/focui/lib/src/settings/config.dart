import 'package:flutter/material.dart';
import 'package:focui/src/menu/menu.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/foc_entity_feature/custom_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';

class Config {
  static String baseUrl =
      'http://localhost:8099'; // Replace with your API base URL
  static String appName =
      'Neo Foc Application'; // Name of the application, to be set by the using package
  static IconData appIcon = Icons
      .construction; // Path to the application icon, to be set by the using package
  static List<Menu> menuItems = [
    Menu(Icons.document_scanner, "Countries", "/countries", "Country",
        widgetClassName: "CustomListView"),
    Menu(Icons.document_scanner, "Entities", "/entities", ""),
    // Example of using a custom widget class:
    // Menu(Icons.list, "Custom View", "/custom", "CustomEntity", widgetClassName: "CustomListView"),
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
