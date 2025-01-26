import 'package:flutter/material.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';
import '../meta_feature/meta_entity.dart';
import 'foc_entity.dart';
import 'foc_field_types.dart';
import 'foc_service.dart';

class FocDetailsView extends StatefulWidget {
  final MetaEntity metaEntity;
  final String itemId;

  const FocDetailsView({super.key, required this.metaEntity, required this.itemId});

  static const routeName = '/entity/details';

  @override
  _FocDetailsViewState createState() => _FocDetailsViewState();
}

class _FocDetailsViewState extends State<FocDetailsView> {
  final _formKey = GlobalKey<FormBuilderState>();
  late Future<FocEntity> futureItem;

  @override
  void initState() {
    super.initState();
    futureItem = FocService().fetchItemDetails(widget.metaEntity, widget.itemId);
  }

  void _saveItem(FocEntity focEntity) async {
    if (_formKey.currentState?.saveAndValidate() ?? false) {
      final updatedData = _formKey.currentState?.value;
      final updatedEntity = FocEntity.fromJson(updatedData!);
      try {
        if (updatedEntity.id != null && updatedEntity.id > 0) {
          await FocService().updateItem(widget.metaEntity, updatedEntity);
        } else {
          await FocService().insertItem(widget.metaEntity, updatedEntity);
        }
        Navigator.pop(context, updatedEntity);
      } catch (e) {
        print('Failed to save item: $e');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('${widget.metaEntity.name} Details'),
        actions: [
          IconButton(
            icon: const Icon(Icons.save),
            onPressed: () async {
              final focEntity = await futureItem;
              _saveItem(focEntity);
            },
          ),
        ],
      ),
      body: FutureBuilder<FocEntity>(
        future: futureItem,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData) {
            return const Center(child: Text('No item details found'));
          } else {
            final focEntity = snapshot.data!;
            return Padding(
              padding: const EdgeInsets.all(16.0),
              child: FormBuilder(
                key: _formKey,
                child: Column(
                  children: widget.metaEntity.fields.map((field) {
                    switch (field.sqlType) {
                      case FocFieldTypes.VARCHAR:
                      case FocFieldTypes.CHAR:
                      case FocFieldTypes.LONGVARCHAR:
                      case FocFieldTypes.NVARCHAR:
                      case FocFieldTypes.NCHAR:
                      case FocFieldTypes.LONGNVARCHAR:
                        return FormBuilderTextField(
                          name: field.dbName,
                          initialValue: focEntity[field.dbName].toString(),
                          decoration: InputDecoration(
                            labelText: field.name,
                          ),
                        );
                      case FocFieldTypes.INTEGER:
                      case FocFieldTypes.SMALLINT:
                      case FocFieldTypes.TINYINT:
                      case FocFieldTypes.BIGINT:
                      case FocFieldTypes.FLOAT:
                      case FocFieldTypes.REAL:
                      case FocFieldTypes.DOUBLE:
                      case FocFieldTypes.NUMERIC:
                      case FocFieldTypes.DECIMAL:
                        return FormBuilderTextField(
                          name: field.dbName,
                          initialValue: focEntity[field.dbName].toString(),
                          decoration: InputDecoration(
                            labelText: field.name,
                          ),
                          keyboardType: TextInputType.number,
                        );
                      case FocFieldTypes.DATE:
                      case FocFieldTypes.TIME:
                      case FocFieldTypes.TIMESTAMP:
                      case FocFieldTypes.TIME_WITH_TIMEZONE:
                      case FocFieldTypes.TIMESTAMP_WITH_TIMEZONE:
                        return FormBuilderDateTimePicker(
                          name: field.dbName,
                          initialValue: DateTime.tryParse(focEntity[field.dbName].toString()),
                          decoration: InputDecoration(
                            labelText: field.name,
                          ),
                        );
                      case FocFieldTypes.BOOLEAN:
                        return FormBuilderCheckbox(
                          name: field.dbName,
                          initialValue: focEntity[field.dbName] == 'true',
                          title: Text(field.name),
                        );
                      default:
                        return FormBuilderTextField(
                          name: field.dbName,
                          initialValue: focEntity[field.dbName].toString(),
                          decoration: InputDecoration(
                            labelText: field.name,
                          ),
                        );
                    }
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
