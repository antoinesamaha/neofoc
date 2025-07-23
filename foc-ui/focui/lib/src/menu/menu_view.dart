import 'package:flutter/material.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';
import 'package:focui/src/entities/meta_feature/meta_service.dart';
import 'package:focui/src/settings/config.dart';
import 'package:go_router/go_router.dart';

import '../settings/settings_view.dart';
import 'menu.dart';

/// Displays a list of SampleItems.
class MenuView extends StatelessWidget {
  MenuView({super.key});

  static const routeName = '/';

  List<Menu> items = Config.menuItems;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        //title: const Text('Entities Menu'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              // Navigate to the settings page. If the user leaves and returns
              // to the app after it has been killed while running in the
              // background, the navigation stack is restored.
              Navigator.restorablePushNamed(context, SettingsView.routeName);
            },
          ),
        ],
      ),

      // To work with lists that may contain a large number of items, it’s best
      // to use the ListView.builder constructor.
      //
      // In contrast to the default ListView constructor, which requires
      // building all Widgets up front, the ListView.builder constructor lazily
      // builds Widgets as they’re scrolled into view.
      body: ListView.builder(
        // Providing a restorationId allows the ListView to restore the
        // scroll position when a user leaves and returns to the app after it
        // has been killed while running in the background.
        restorationId: 'sampleItemListView',
        itemCount: items.length,
        itemBuilder: (BuildContext context, int index) {
          final item = items[index];

          return ListTile(
            leading: item.iconData != null
                ? Icon(item.iconData)
                : CircleAvatar(
                    foregroundImage: AssetImage(item.iconImageFile),
                  ),
            title: Text(item.displayName),
            onTap: () {
              // Navigate to the details page. If the user leaves and returns to
              // the app after it has been killed while running in the
              // background, the navigation stack is restored.

              MetaEntity? metaEntity =
                  MetaService().getEntityByName(item.entityName);

              if (metaEntity != null) {
                // If the entity is not null, use it to create a FocListView
                FocListView(
                  metaEntity: metaEntity,
                );

                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (context) => FocListView(metaEntity: metaEntity),
                  ),
                );
                // Navigator.restorablePushNamed(
              } else {
                // If the entity is null, Go to the menu path
                Navigator.restorablePushNamed(
                  context,
                  item.entityPath,
                );
              }
            },
          );

          // Navigate to the details page. If the user leaves and returns to
          // the app after it has been killed while running in the
          // background, the navigation stack is restored.
          //GoRouter.of(context).goNamed('entities');
          // Navigator.restorablePushNamed(
          //   context,
          //   item.entityPath,
          // );
        },
      ),
    );
  }
}
