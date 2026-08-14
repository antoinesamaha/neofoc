import 'package:flutter/material.dart';
import 'package:focui/src/app_router.dart';
import 'settings/settings_controller.dart';

/// The Widget that configures your application.
class NeoFocApp extends StatelessWidget {
  const NeoFocApp({
    super.key,
    required this.settingsController,
  });

  final SettingsController settingsController;

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Auth Routing App',
      routerConfig: appRouter,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blueGrey.shade700),
        useMaterial3: true,
      ),
      // darkTheme: ThemeData(
      //   colorScheme: ColorScheme.fromSeed(
      //       seedColor: Colors.blueGrey.shade700, brightness: Brightness.dark),
      //   useMaterial3: true,
      // ),
      themeMode: settingsController.themeMode,
    );
  }
}
