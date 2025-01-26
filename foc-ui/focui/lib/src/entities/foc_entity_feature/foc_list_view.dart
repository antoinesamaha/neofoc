// lib/src/sample_feature/sample_item_list_view.dart
import 'package:flutter/material.dart';
import '../../settings/settings_view.dart';
import '../meta_feature/meta_entity.dart';
import 'foc_details_view.dart';
import 'foc_entity.dart';
import 'foc_service.dart';

class FocListView extends StatefulWidget {
  final MetaEntity metaEntity;

  const FocListView({super.key, required this.metaEntity});

  static const routeName = '/entity';

  @override
  _FocListViewState createState() => _FocListViewState();
}

class _FocListViewState extends State<FocListView> {
  late Future<List<FocEntity>> futureItems;

  @override
  void initState() {
    super.initState();
    futureItems = FocService().fetchItems(widget.metaEntity);
  }

  void _editItem(FocEntity item) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => FocDetailsView(metaEntity: widget.metaEntity, itemId: item.id.toString()),
      ),
    ).then((updatedItem) {
      if (updatedItem != null) {
        setState(() {
          futureItems = FocService().fetchItems(widget.metaEntity);
        });
      }
    });
  }

  void _deleteItem(FocEntity item) {
    // Implement delete functionality here
    print('Delete item: ${item}');
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
            return SingleChildScrollView(
              scrollDirection: Axis.vertical,
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: DataTable(
                  columns: [
                    ...widget.metaEntity.fields.map((field) {
                      return DataColumn(label: Text(field.name));
                    }).toList(),
                    const DataColumn(label: Text('Actions')),
                  ],
                  rows: snapshot.data!.map((item) {
                    return DataRow(cells: [
                      ...widget.metaEntity.fields.map((field) {
                        return DataCell(Text(item[field.dbName].toString()));
                      }).toList(),
                      DataCell(Row(children: [
                        IconButton(
                          icon: const Icon(Icons.edit),
                          onPressed: () => _editItem(item),
                        ),
                        IconButton(
                          icon: const Icon(Icons.delete),
                          onPressed: () => _deleteItem(item),
                        ),
                      ])),
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
