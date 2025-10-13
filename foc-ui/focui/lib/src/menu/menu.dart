import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

/// A placeholder class that represents an entity or model.
class Menu {
  const Menu(this.iconData, this.displayName, this.entityPath, this.entityName,
      {this.widgetClassName});

  final String iconImageFile = 'assets/images/flutter_logo.png';
  final IconData? iconData; // Made nullable to support image-only menu items
  final String displayName;
  final String entityPath;
  final String entityName;
  final String?
      widgetClassName; // Optional widget class name to override default FocListView
}
