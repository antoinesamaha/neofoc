import 'package:flutter/material.dart';

import 'settings_controller.dart';

class ThemeToggleWidget extends StatelessWidget {
  final SettingsController settingsController;

  const ThemeToggleWidget({Key? key, required this.settingsController}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text('Light'),
        Switch(
          value: settingsController.themeMode == ThemeMode.dark,
          onChanged: (value) {
            settingsController.updateThemeMode(value ? ThemeMode.dark : ThemeMode.light);
          },
        ),
        const Text('Dark'),
      ],
    );
  }
}
