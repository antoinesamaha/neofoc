// lib/src/sample_feature/sample_item_list_view.dart
import 'package:flutter/material.dart';
import '../../settings/settings_view.dart';
import '../meta_feature/meta_entity.dart';
import 'foc_entity.dart';
import 'foc_service.dart';

class FocListView extends StatefulWidget {
  final MetaEntity metaEntity;

  const FocListView({super.key, required this.metaEntity});

  static const routeName = '/countries';

  @override
  _FocListViewState createState() => _FocListViewState();
}

class _FocListViewState extends State<FocListView> {
  late Future<List<FocEntity>> futureItems;

  @override
  void initState() {
    super.initState();
    futureItems = FocService().fetchItems();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.metaEntity.name),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.restorablePushNamed(context, SettingsView.routeName);
            },
          ),
        ],
      ),
      body: FutureBuilder<List<FocEntity>>(
        future: futureItems,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('No items found'));
          } else {
            return DataTable(
              columns: widget.metaEntity.fields.map((field) {
                return DataColumn(label: Text(field.name));
              }).toList(),
              rows: snapshot.data!.map((item) {
                return DataRow(
                    cells: widget.metaEntity.fields.map((field) {
                  return DataCell(Text(item[field.dbName].toString()));
                }).toList());
              }).toList(),
            );
          }
        },
      ),
    );
  }
}
