import 'package:flutter/material.dart';
import 'package:focui/main.dart';
import 'package:focui/src/auth/auth_service.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';
import 'package:focui/src/entities/meta_feature/meta_service.dart';
import 'package:focui/src/settings/config.dart';

import '../settings/settings_view.dart';
import 'menu.dart';

/// Displays a list of SampleItems.
class MenuView extends StatelessWidget {
  MenuView({super.key});

  static const routeName = '/';

  final List<Menu> items = Config.menuItems;

  Future<void> _showChangePasswordDialog(BuildContext context) async {
    final formKey = GlobalKey<FormState>();
    final oldPasswordCtrl = TextEditingController();
    final newPasswordCtrl = TextEditingController();
    final confirmCtrl = TextEditingController();
    bool loading = false;

    await showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setState) => AlertDialog(
          title: const Text('Change Password'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: oldPasswordCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'Current Password'),
                  validator: (v) =>
                      (v == null || v.isEmpty) ? 'Required' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: newPasswordCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'New Password'),
                  validator: (v) =>
                      (v == null || v.isEmpty) ? 'Required' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: confirmCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'Confirm New Password'),
                  validator: (v) => v != newPasswordCtrl.text
                      ? 'Passwords do not match'
                      : null,
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: loading ? null : () => Navigator.pop(ctx),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: loading
                  ? null
                  : () async {
                      if (!formKey.currentState!.validate()) return;
                      setState(() => loading = true);
                      try {
                        final authService = getIt<AuthService>();
                        await authService.changePassword(
                          authService.currentUsername,
                          oldPasswordCtrl.text,
                          newPasswordCtrl.text,
                        );
                        Navigator.pop(ctx);
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                              content: Text('Password changed successfully')),
                        );
                      } catch (e) {
                        setState(() => loading = false);
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text(e.toString().replaceFirst('Exception: ', ''))),
                        );
                      }
                    },
              child: loading
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2))
                  : const Text('Change'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        //title: const Text('Entities Menu'),
        actions: [
          IconButton(
            icon: const Icon(Icons.lock_outline),
            tooltip: 'Change Password',
            onPressed: () => _showChangePasswordDialog(context),
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
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
                // Determine which widget class to use
                String widgetClassName = item.widgetClassName ?? 'FocListView';

                // Get the widget factory from the registry
                var widgetFactory = Config.widgetClassRegistry[widgetClassName];

                if (widgetFactory != null) {
                  // Create the widget using the factory function
                  Widget targetWidget = widgetFactory(metaEntity);

                  Navigator.of(context).push(
                    MaterialPageRoute(
                      builder: (context) => targetWidget,
                    ),
                  );
                } else {
                  // Fallback to default FocListView if widget class not found
                  Navigator.of(context).push(
                    MaterialPageRoute(
                      builder: (context) => FocListView(metaEntity: metaEntity),
                    ),
                  );
                }
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
