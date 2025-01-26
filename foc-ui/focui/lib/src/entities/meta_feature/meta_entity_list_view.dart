// lib/src/sample_feature/sample_item_list_view.dart
import 'package:flutter/material.dart';
import '../../settings/settings_view.dart';
import 'meta_entity.dart';
import 'meta_service.dart';

class MetaEntityListView extends StatefulWidget {
  const MetaEntityListView({super.key});

  static const routeName = '/entities';

  @override
  _MetaEntityListViewState createState() => _MetaEntityListViewState();
}

class _MetaEntityListViewState extends State<MetaEntityListView> {
  late Future<Map<String, MetaEntity>> futureItems;

  @override
  void initState() {
    super.initState();
    futureItems = MetaService().fetchItems();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Entities'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.restorablePushNamed(context, SettingsView.routeName);
            },
          ),
        ],
      ),
      body: FutureBuilder<Map<String, MetaEntity>>(
        future: futureItems,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('No items found'));
          } else {
            return SingleChildScrollView(
              scrollDirection: Axis.vertical,
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: DataTable(
                  columns: const [
                    DataColumn(label: Text('Name')),
                    DataColumn(label: Text('Storage Name')),
                    DataColumn(label: Text('Module')),
                    DataColumn(label: Text('Is Cached')),
                    DataColumn(label: Text('Actions')),
                  ],
                  rows: snapshot.data!.values.map((entity) {
                    return DataRow(cells: [
                      DataCell(Text(entity.name)),
                      DataCell(Text(entity.storageName)),
                      DataCell(Text(entity.module)),
                      DataCell(Text(entity.isListInCache.toString())),
                      DataCell(
                        IconButton(
                          icon: const Icon(Icons.list),
                          onPressed: () {
                            // Navigate to the desired screen
                            Navigator.pushNamed(
                              context,
                              '/entity/${entity.name}', // Replace with your desired route
                              arguments: entity, // Pass the entity as an argument if needed
                            );
                          },
                        ),
                      ),
                    ]);
                  }).toList(),
                ),
              ),
            );
          }
        },
      ),
    );
  }
}
