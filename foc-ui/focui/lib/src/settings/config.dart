import 'package:flutter/material.dart';
import 'package:focui/src/menu/menu.dart';

class Config {
  static String baseUrl =
      'http://localhost:8099'; // Replace with your API base URL
  static String appName =
      'Neo Foc Application'; // Name of the application, to be set by the using package
  static IconData appIcon = Icons
      .construction; // Path to the application icon, to be set by the using package
  static List<Menu> menuItems = [
    Menu(Icons.document_scanner, "Countries", "/countries", "Country"),
    Menu(Icons.document_scanner, "Entities", "/entities", ""),
  ];

  static Map<String, WidgetBuilder> entityListViewRegistry = {
    //'Instruments': (context) => InstrumentsListView(),
    // Add other entity-specific views here
  };
}
