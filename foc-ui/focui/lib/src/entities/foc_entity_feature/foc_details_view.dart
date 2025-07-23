import 'package:flutter/material.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';
import '../meta_feature/meta_entity.dart';
import 'foc_entity.dart';
import 'foc_field_types.dart';
import 'foc_service.dart';
import 'dart:convert';
import 'package:flutter/services.dart';
import '../../components/json_form_builder.dart';

class FocDetailsView extends StatefulWidget {
  final MetaEntity metaEntity;
  final String? itemId;

  const FocDetailsView(
      {super.key, required this.metaEntity, required this.itemId});

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
    futureItem =
        FocService().fetchItemDetails(widget.metaEntity, widget.itemId ?? '');
  }

  void _saveItem(FocEntity focEntity) async {
    if (_formKey.currentState?.saveAndValidate() ?? false) {
      final updatedData = _formKey.currentState?.value;
      final updatedEntity =
          FocEntity.fromJson(focEntity.metaEntity, updatedData!);
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
        title: Text(
            '${widget.metaEntity.name[0].toUpperCase()}${widget.metaEntity.name.substring(1)}'),
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
              child: FutureBuilder<Widget>(
                future: entityForm(focEntity),
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

  Future<Map<String, dynamic>?> loadJsonAssetIfExists(String assetPath) async {
    try {
      final jsonString = await rootBundle.loadString(assetPath);
      return jsonDecode(jsonString) as Map<String, dynamic>;
    } catch (e) {
      // Asset does not exist or failed to load
      return null;
    }
  }

  Future<Widget> entityForm(FocEntity focEntity) async {
    // Try to load a form file matching the entity name
    String entityFormFile =
        'assets/forms/${widget.metaEntity.name.toLowerCase().replaceAll(' ', '_')}_form.json';
    try {
      final jsonString = await rootBundle.loadString(entityFormFile);
      final formData = json.decode(jsonString);
      return JsonFormBuilder(
        metaEntity: widget.metaEntity,
        focEntity: focEntity,
        assetPath: entityFormFile,
        formData: formData,
        formKey: _formKey,
        initialValues: focEntity.properties,
        onChanged: (values) {
          // Handle form changes if needed
        },
        autovalidateMode: AutovalidateMode.onUserInteraction,
      );
    } catch (e) {
      // Fallback to the original form builder if no JSON form is available
      return entityFormColumnWithAllFields(focEntity);
    }
  }

  Widget entityFormColumnWithAllFields(FocEntity focEntity) {
    return FormBuilder(
      key: _formKey,
      child: Column(
        children: widget.metaEntity.fields.map((field) {
          final value = focEntity[field.dbName];
          final stringValue = value == null ? '' : value.toString();
          switch (field.sqlType) {
            case FocFieldTypes.VARCHAR:
            case FocFieldTypes.CHAR:
            case FocFieldTypes.LONGVARCHAR:
            case FocFieldTypes.NVARCHAR:
            case FocFieldTypes.NCHAR:
            case FocFieldTypes.LONGNVARCHAR:
              return FormBuilderTextField(
                name: field.dbName,
                initialValue: stringValue,
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
                initialValue: stringValue,
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
                initialValue: (value == null ||
                        value.toString() == '' ||
                        value.toString() == 'null')
                    ? null
                    : DateTime.tryParse(value.toString()),
                decoration: InputDecoration(
                  labelText: field.name,
                ),
              );
            case FocFieldTypes.BOOLEAN:
              return FormBuilderCheckbox(
                name: field.dbName,
                initialValue:
                    value == null ? false : (value == 'true' || value == true),
                title: Text(field.name),
              );
            default:
              return FormBuilderTextField(
                name: field.dbName,
                initialValue: stringValue,
                decoration: InputDecoration(
                  labelText: field.name,
                ),
              );
          }
        }).toList(),
      ),
    );
  }
}
