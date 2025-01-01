import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_entity.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';
import 'package:focui/src/entities/meta_feature/meta_entity_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_service.dart';
import 'package:focui/src/menu/menu_view.dart';

import 'sample_feature/sample_item_details_view.dart';
import 'sample_feature/sample_item_list_view.dart';
import 'settings/settings_controller.dart';
import 'settings/settings_view.dart';

/// The Widget that configures your application.
class MyApp extends StatelessWidget {
  const MyApp({
    super.key,
    required this.settingsController,
  });

  final SettingsController settingsController;

  @override
  Widget build(BuildContext context) {
    // Glue the SettingsController to the MaterialApp.
    //
    // The ListenableBuilder Widget listens to the SettingsController for changes.
    // Whenever the user updates their settings, the MaterialApp is rebuilt.
    return ListenableBuilder(
      listenable: settingsController,
      builder: (BuildContext context, Widget? child) {
        return MaterialApp(
            // Providing a restorationScopeId allows the Navigator built by the
            // MaterialApp to restore the navigation stack when a user leaves and
            // returns to the app after it has been killed while running in the
            // background.
            restorationScopeId: 'app',

            // Provide the generated AppLocalizations to the MaterialApp. This
            // allows descendant Widgets to display the correct translations
            // depending on the user's locale.
            localizationsDelegates: const [
              AppLocalizations.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            supportedLocales: const [
              Locale('en', ''), // English, no country code
            ],

            // Use AppLocalizations to configure the correct application title
            // depending on the user's locale.
            //
            // The appTitle is defined in .arb files found in the localization
            // directory.
            onGenerateTitle: (BuildContext context) => AppLocalizations.of(context)!.appTitle,

            // Define a light and dark color theme. Then, read the user's
            // preferred ThemeMode (light, dark, or system default) from the
            // SettingsController to display the correct theme.
            theme: ThemeData(),
            darkTheme: ThemeData.dark(),
            themeMode: settingsController.themeMode,

            // Define a function to handle named routes in order to support
            // Flutter web url navigation and deep linking.
            onGenerateRoute: (RouteSettings routeSettings) {
              final uri = Uri.parse(routeSettings.name!);
              if (uri.pathSegments.length == 2 && uri.pathSegments[0] == 'entity') {
                final entityName = uri.pathSegments[1];
                MetaEntity metaEntity = MetaService().getEntityByName(entityName)!;
                return MaterialPageRoute<void>(
                  settings: routeSettings,
                  builder: (BuildContext context) => FocListView(metaEntity: metaEntity),
                );
              }

              switch (routeSettings.name) {
                case SettingsView.routeName:
                  return MaterialPageRoute<void>(
                    settings: routeSettings,
                    builder: (BuildContext context) => SettingsView(controller: settingsController),
                  );
                case MenuView.routeName:
                  return MaterialPageRoute<void>(
                    settings: routeSettings,
                    builder: (BuildContext context) => const MenuView(),
                  );
                case MetaEntityListView.routeName:
                  return MaterialPageRoute<void>(
                    settings: routeSettings,
                    builder: (BuildContext context) => const MetaEntityListView(),
                  );
                case SampleItemDetailsView.routeName:
                  return MaterialPageRoute<void>(
                    settings: routeSettings,
                    builder: (BuildContext context) => const SampleItemDetailsView(),
                  );
                case SampleItemListView.routeName:
                  return MaterialPageRoute<void>(
                    settings: routeSettings,
                    builder: (BuildContext context) => const SampleItemListView(),
                  );
                default:
                  return MaterialPageRoute<void>(
                    settings: routeSettings,
                    builder: (BuildContext context) => const MenuView(),
                  );
              }
            });
      },
    );
  }
}
