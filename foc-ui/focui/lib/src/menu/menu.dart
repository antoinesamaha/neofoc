import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

/// A placeholder class that represents an entity or model.
class Menu {
  const Menu(this.iconData, this.displayName, this.entityPath, this.entityName);

  final String iconImageFile = 'assets/images/flutter_logo.png';
  final IconData iconData;
  final String displayName;
  final String entityPath;
  final String entityName;
}
