// lib/src/sample_feature/sample_item_list_view.dart
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';
import 'package:focui/src/components/json_form_builder.dart';
import 'dart:convert';
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
  FocListViewState createState() => FocListViewState();
}

class FocListViewState extends JsonFormState<FocListView> {
  final _formKey = GlobalKey<FormBuilderState>();
  late Future<List<FocEntity>> futureItems;

  @override
  void initState() {
    super.initState();
    futureItems = FocService().fetchItems(widget.metaEntity);
  }

  void editItem(FocEntity item) {
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

  void deleteItem(FocEntity item) {
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
        ],
      ),
      body: FutureBuilder<List<FocEntity>>(
        future: futureItems,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
            // } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            //   return const Center(child: Text('No items found'));
          } else {
            final List<FocEntity> focEntityList = snapshot.data!;

            return Padding(
              padding: const EdgeInsets.all(16.0),
              child: FutureBuilder<Widget>(
                future: entityListForm(focEntityList),
                builder: (context, formSnapshot) {
                  if (formSnapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  } else if (formSnapshot.hasError) {
                    return Center(
                        child:
                            Text('Error loading form: ${formSnapshot.error}'));
                  } else if (formSnapshot.hasData) {
                    return formSnapshot.data!;
                  } else {
                    return const Center(child: Text('No form available'));
                  }
                },
              ),
            );
          }
        },
      ),
    );
  }

  Future<Widget> entityListForm(List<FocEntity> focEntityList) async {
    // Try to load a form file matching the entity name
    String entityFormFile =
        'assets/forms/${widget.metaEntity.name.toLowerCase().replaceAll(' ', '_')}_list.json';
    try {
      final jsonString = await rootBundle.loadString(entityFormFile);
      final formData = json.decode(jsonString);
      return JsonFormBuilder(
        metaEntity: widget.metaEntity,
        focEntityList: focEntityList,
        assetPath: entityFormFile,
        formData: formData,
        formKey: _formKey,
        onChanged: (values) {
          // Handle form changes if needed
        },
        autovalidateMode: AutovalidateMode.onUserInteraction,
        state: this,
      );
    } catch (e) {
      // Fallback to the original form builder if no JSON form is available
      return defaultEntityListForm(focEntityList);
    }
  }

  Widget defaultEntityListForm(List<FocEntity> focEntityList) {
    return LayoutBuilder(
      // Remove Center widget
      builder: (context, constraints) {
        final double tableWidth = constraints.maxWidth; // * 0.8;
        return SizedBox(
          width: tableWidth,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              // Add button aligned with the table
              Padding(
                padding: const EdgeInsets.only(bottom: 8, top: 8),
                child: ElevatedButton.icon(
                  icon: const Icon(Icons.add),
                  label: const Text('Add'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF4A00E0),
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30),
                    ),
                    padding: const EdgeInsets.symmetric(
                        horizontal: 22, vertical: 14),
                    textStyle: const TextStyle(fontSize: 16),
                    elevation: 0,
                  ),
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
                          futureItems =
                              FocService().fetchItems(widget.metaEntity);
                        });
                      }
                    });
                  },
                ),
              ),
              // Table
              ClipRRect(
                borderRadius: BorderRadius.circular(18),
                child: Builder(
                  builder: (context) {
                    final displayFieldNames = getDisplayFieldNames();
                    final displayFields =
                        widget.metaEntity.fields.where((field) {
                      final lower = field.name.toLowerCase();
                      return displayFieldNames.contains(lower);
                    }).toList();
                    return DataTable(
                      headingRowColor:
                          MaterialStateProperty.resolveWith<Color?>(
                              (states) => const Color(0xFF232946)),
                      headingTextStyle: const TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                        fontSize: 17,
                        letterSpacing: 1.1,
                      ),
                      dataRowColor:
                          MaterialStateProperty.resolveWith<Color?>((states) {
                        if (states.contains(MaterialState.selected)) {
                          return Colors.deepPurple.withOpacity(0.10);
                        }
                        if (states.contains(MaterialState.hovered)) {
                          return const Color(0xFFB8C1EC).withOpacity(0.35);
                        }
                        return states.contains(MaterialState.focused)
                            ? Colors.blue.withOpacity(0.10)
                            : null;
                      }),
                      dataTextStyle: const TextStyle(
                        fontSize: 15,
                        color: Colors.black,
                        fontWeight: FontWeight.w500,
                      ),
                      dividerThickness: 1.2,
                      columnSpacing: 32,
                      horizontalMargin: 22,
                      columns: [
                        ...displayFields.map((field) {
                          return DataColumn(
                              label: Text(_capitalize(field.name)));
                        }).toList(),
                        ...getCustomColumns(),
                        const DataColumn(label: Text('Actions')),
                      ],
                      rows: focEntityList.asMap().entries.map((entry) {
                        final index = entry.key;
                        final item = entry.value;
                        return DataRow(
                          color: MaterialStateProperty.resolveWith<Color?>(
                              (states) {
                            if (states.contains(MaterialState.hovered)) {
                              return const Color(0xFFB8C1EC).withOpacity(0.35);
                            }
                            return index % 2 == 0
                                ? const Color(0xFFF4F6FB)
                                : const Color(0xFFF9F9FB);
                          }),
                          cells: [
                            ...displayFields.map((field) {
                              return DataCell(
                                  Text(item[field.dbName]?.toString() ?? ''));
                            }).toList(),
                            ...getCustomDataCells(item),
                            DataCell(Row(children: [
                              IconButton(
                                icon: const Icon(Icons.edit),
                                tooltip: 'Edit',
                                color: const Color(0xFF4A00E0),
                                onPressed: () => editItem(item),
                              ),
                              IconButton(
                                icon: const Icon(Icons.delete),
                                tooltip: 'Delete',
                                color: Colors.redAccent,
                                onPressed: () => deleteItem(item),
                              ),
                            ])),
                          ],
                        );
                      }).toList(),
                    );
                  },
                ),
              ),
              const SizedBox(height: 24),
            ],
          ),
        );
      },
    );
  }

  /// Override this method in subclasses to add custom column headers
  /// Returns a list of DataColumn widgets that will be inserted before the Actions column
  @override
  List<DataColumn> getCustomColumns() {
    return [];
  }

  /// Override this method in subclasses to add custom data cells for each row
  /// Returns a list of DataCell widgets that will be inserted before the Actions column
  /// The item is passed as parameter to allow cell content based on the row data
  @override
  List<DataCell> getCustomDataCells(dynamic item) {
    return [];
  }

  /// Override this method in subclasses to customize which fields are displayed
  /// By default shows 'code' and 'name' fields
  List<String> getDisplayFieldNames() {
    return ['code', 'name'];
  }
}
