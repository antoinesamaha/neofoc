import 'package:flutter/material.dart';
// import 'package:flutter_gen/gen_l10n/app_localizations.dart';
// import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:focui/main.dart';
import 'package:focui/src/app_router.dart';
import 'package:focui/src/auth/login_page.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_entity.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';
import 'package:focui/src/entities/meta_feature/meta_entity_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_service.dart';
import 'package:focui/src/menu/menu_view.dart';
import 'package:focui/src/settings/theme_toggle_widget.dart';
import 'package:go_router/go_router.dart';

import 'sample_feature/sample_item_details_view.dart';
import 'sample_feature/sample_item_list_view.dart';
import 'auth/auth_service.dart';
import 'settings/settings_controller.dart';
import 'settings/settings_view.dart';

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
