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
        builder: (context) => FocDetailsView(
            metaEntity: widget.metaEntity, itemId: item.id.toString()),
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

  String _capitalize(String s) {
    if (s.isEmpty) return s;
    return s[0].toUpperCase() + s.substring(1);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_capitalize(widget.metaEntity.name)),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.restorablePushNamed(context, SettingsView.routeName);
            },
          ),
          IconButton(
            icon: const Icon(Icons.add),
            tooltip: 'Add',
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => FocDetailsView(
                    metaEntity: widget.metaEntity,
                    itemId: null, // null means create new
                  ),
                ),
              ).then((newItem) {
                if (newItem != null) {
                  setState(() {
                    futureItems = FocService().fetchItems(widget.metaEntity);
                  });
                }
              });
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
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // Row for the Add button aligned left above the table, with same margin as the table
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        ElevatedButton.icon(
                          icon: const Icon(Icons.add),
                          label: const Text('Add'),
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => FocDetailsView(
                                  metaEntity: widget.metaEntity,
                                  itemId: null, // null means create new
                                ),
                              ),
                            ).then((newItem) {
                              if (newItem != null) {
                                setState(() {
                                  futureItems = FocService()
                                      .fetchItems(widget.metaEntity);
                                });
                              }
                            });
                          },
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 8),
                  Padding(
                    padding: const EdgeInsets.all(
                        16.0), // Add margin around the table
                    child: SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: Builder(
                        builder: (context) {
                          // Only show 'Code' and/or 'Name' columns if present
                          final displayFields =
                              widget.metaEntity.fields.where((field) {
                            final lower = field.name.toLowerCase();
                            return lower == 'code' || lower == 'name';
                          }).toList();
                          return DataTable(
                            headingRowColor:
                                MaterialStateProperty.resolveWith<Color?>(
                                    (states) =>
                                        Colors.indigoAccent.withOpacity(0.85)),
                            headingTextStyle: const TextStyle(
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                              fontSize: 17,
                              letterSpacing: 1.1,
                            ),
                            dataRowColor:
                                MaterialStateProperty.resolveWith<Color?>(
                                    (states) {
                              if (states.contains(MaterialState.selected)) {
                                return Colors.deepPurple.withOpacity(0.10);
                              }
                              if (states.contains(MaterialState.hovered)) {
                                return Colors.lightBlueAccent
                                    .withOpacity(0.25); // More vibrant hover
                              }
                              // Default row color, will be overridden per-row below
                              return null;
                            }),
                            dataTextStyle: const TextStyle(
                              fontSize: 15,
                              color: Colors.black,
                              fontWeight: FontWeight.w500,
                            ),
                            dividerThickness: 1.5,
                            columnSpacing: 32,
                            horizontalMargin: 22,
                            columns: [
                              ...displayFields.map((field) {
                                return DataColumn(
                                    label: Text(_capitalize(field.name)));
                              }).toList(),
                              const DataColumn(label: Text('Actions')),
                            ],
                            rows: snapshot.data!.asMap().entries.map((entry) {
                              final index = entry.key;
                              final item = entry.value;
                              return DataRow(
                                color:
                                    MaterialStateProperty.resolveWith<Color?>(
                                        (states) {
                                  if (states.contains(MaterialState.hovered)) {
                                    return Colors.lightBlueAccent
                                        .withOpacity(0.25);
                                  }
                                  return index % 2 == 0
                                      ? Colors.cyan.withOpacity(0.08)
                                      : Colors.amber.withOpacity(0.10);
                                }),
                                cells: [
                                  ...displayFields.map((field) {
                                    return DataCell(Text(
                                        item[field.dbName]?.toString() ?? ''));
                                  }).toList(),
                                  DataCell(Row(children: [
                                    IconButton(
                                      icon: const Icon(Icons.edit),
                                      tooltip: 'Edit',
                                      color: Colors.indigo,
                                      onPressed: () => _editItem(item),
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.delete),
                                      tooltip: 'Delete',
                                      color: Colors.redAccent,
                                      onPressed: () => _deleteItem(item),
                                    ),
                                  ])),
                                ],
                              );
                            }).toList(),
                          );
                        },
                      ),
                    ),
                  ),
                ],
              ),
            );
          }
        },
      ),
    );
  }
}
