import 'package:flutter/material.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_details_view.dart';
import 'package:focui/src/entities/meta_feature/meta_service.dart';
import 'package:form_builder_validators/form_builder_validators.dart';
import 'dart:convert';
import 'package:flutter/services.dart';
import '../entities/foc_entity_feature/foc_entity.dart';
import '../entities/foc_entity_feature/foc_service.dart';
import '../entities/meta_feature/meta_entity.dart';

/// A widget that parses JSON layout definitions and renders FlutterFormBuilder forms
class JsonFormBuilder extends StatefulWidget {
  final MetaEntity? metaEntity;
  final FocEntity? focEntity; // Either Have focEntity
  final List<FocEntity>? focEntityList; // Either Have focEntityList

  /// The JSON form configuration as a Map
  final Map<String, dynamic>? formData;

  /// The JSON form configuration as a String
  final String? jsonString;

  /// Asset path to load JSON from assets
  final String? assetPath;

  /// Initial form values
  final Map<String, dynamic>? initialValues;

  /// Callback when form values change
  final void Function(Map<String, dynamic>?)? onChanged;

  /// Callback when form is saved
  final void Function(Map<String, dynamic>?)? onSaved;

  /// Whether to auto validate form fields
  final AutovalidateMode autovalidateMode;

  /// Custom form key
  final GlobalKey<FormBuilderState>? formKey;

  /// Whether to show debug information
  final bool enableDebug;

  final JsonFormState state;

  const JsonFormBuilder(
      {super.key,
      this.metaEntity,
      this.focEntity,
      this.focEntityList,
      this.formData,
      this.jsonString,
      this.assetPath,
      this.initialValues,
      this.onChanged,
      this.onSaved,
      this.autovalidateMode = AutovalidateMode.disabled,
      this.formKey,
      this.enableDebug = false,
      required this.state})
      : assert(
          formData != null || jsonString != null || assetPath != null,
          'At least one of formData, jsonString, or assetPath must be provided',
        );

  @override
  State<JsonFormBuilder> createState() => _JsonFormBuilderState();
}

class _JsonFormBuilderState extends State<JsonFormBuilder> {
  late GlobalKey<FormBuilderState> _formKey;
  Map<String, dynamic>? _parsedFormData;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _formKey = widget.formKey ?? GlobalKey<FormBuilderState>();
    _loadFormData();
  }

  @override
  void didUpdateWidget(JsonFormBuilder oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.formData != oldWidget.formData ||
        widget.jsonString != oldWidget.jsonString ||
        widget.assetPath != oldWidget.assetPath) {
      _loadFormData();
    }
  }

  Future<void> _loadFormData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      Map<String, dynamic>? data;

      if (widget.formData != null) {
        data = widget.formData;
      } else if (widget.jsonString != null) {
        data = json.decode(widget.jsonString!) as Map<String, dynamic>;
      } else if (widget.assetPath != null) {
        final jsonString = await rootBundle.loadString(widget.assetPath!);
        data = json.decode(jsonString) as Map<String, dynamic>;
      }

      setState(() {
        _parsedFormData = data;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load form data: $e';
        _isLoading = false;
      });
      if (widget.enableDebug) {
        debugPrint('JsonFormBuilder error: $e');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.error, color: Theme.of(context).colorScheme.error),
            const SizedBox(height: 8),
            Text(
              _error!,
              style: TextStyle(color: Theme.of(context).colorScheme.error),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    }

    if (_parsedFormData == null) {
      return const Center(child: Text('No form data available'));
    }

    return Container(
        child: FormBuilder(
      key: _formKey,
      initialValue: widget.initialValues ?? {},
      autovalidateMode: widget.autovalidateMode,
      onChanged: () {
        if (widget.onChanged != null) {
          widget.onChanged!(_formKey.currentState?.value);
        }
      },
      child: _buildFormFields(_parsedFormData!),
    ));
  }

  Widget _buildFormFields(Map<String, dynamic> formData) {
    final fields = formData['fields'] as List<dynamic>? ?? [];
    final layout = formData['layout'] as String? ?? 'column';
    final title = formData['title'] as String?;
    final spacing = (formData['spacing'] as num?)?.toDouble() ?? 16.0;
    final persistencePanel = formData['persistencePanel'] as bool? ?? false;

    List<Widget> fieldWidgets = [];

    // Add title if provided
    if (title != null) {
      fieldWidgets.add(
        Padding(
          padding: EdgeInsets.only(bottom: spacing),
          child: Text(
            title,
            style: Theme.of(context).textTheme.headlineSmall,
          ),
        ),
      );
    }

    // Build form fields
    for (var fieldData in fields) {
      final widget = _buildFormField(fieldData as Map<String, dynamic>);
      if (widget != null) {
        fieldWidgets.add(widget);
      }
    }

    // Add Save and Cancel buttons at the end
    if (persistencePanel) {
      fieldWidgets.add(
        Padding(
          padding: EdgeInsets.only(top: spacing),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              ElevatedButton.icon(
                icon: const Icon(Icons.save),
                label: const Text('Save'),
                onPressed: () {
                  saveFormWithApi(
                    metaEntity: widget.metaEntity!,
                    context: context,
                    // Optionally provide fromJson if needed:
                    // fromJson: (json) => FocEntity.fromJson(json),
                  ).then((result) {
                    if (result != null) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                            content: Text('Form saved!'),
                            backgroundColor: Colors.green),
                      );
                    } else {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                            content: Text('Please fix validation errors'),
                            backgroundColor: Colors.red),
                      );
                    }
                  });
                },
              ),
              const SizedBox(width: 16),
              OutlinedButton.icon(
                icon: const Icon(Icons.cancel),
                label: const Text('Cancel'),
                onPressed: () {
                  resetForm();
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                        content: Text('Form reset'),
                        backgroundColor: Colors.blue),
                  );
                },
              ),
            ],
          ),
        ),
      );
    }

    // Apply layout
    switch (layout) {
      case 'row':
        if (fieldWidgets.isEmpty) return const SizedBox.shrink();
        return SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: fieldWidgets
                .map((w) => Padding(
                      padding: EdgeInsets.only(right: spacing),
                      child: w,
                    ))
                .toList(),
          ),
        );
      case 'wrap':
        if (fieldWidgets.isEmpty) return const SizedBox.shrink();
        return Wrap(
          spacing: spacing,
          runSpacing: spacing,
          children: fieldWidgets,
        );
      case 'grid':
        final crossAxisCount = formData['crossAxisCount'] as int? ?? 2;
        return fieldWidgets.isEmpty
            ? const SizedBox.shrink()
            : GridView.count(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                crossAxisCount: crossAxisCount,
                crossAxisSpacing: spacing,
                mainAxisSpacing: spacing,
                children: fieldWidgets,
              );
      case 'column':
        if (fieldWidgets.isEmpty) return const SizedBox.shrink();
        return SingleChildScrollView(
          scrollDirection: Axis.vertical,
          child: Center(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: fieldWidgets
                  .map((w) => Padding(
                        padding: EdgeInsets.only(top: spacing),
                        child: w,
                      ))
                  .toList(),
            ),
          ),
        );
      default:
        return fieldWidgets.isEmpty
            ? const SizedBox.shrink()
            : Column(
                children: fieldWidgets
                    .map((w) => Padding(
                          padding: EdgeInsets.only(bottom: spacing),
                          child: w,
                        ))
                    .toList(),
              );
    }
  }

  Widget? _buildFormField(Map<String, dynamic> fieldData) {
    final type = fieldData['type'] as String?;
    if (type == null) return null;
    final name = fieldData['name'] as String? ?? '';
    final label = fieldData['label'] as String?;
    final hint = fieldData['hint'] as String?;
    final required = fieldData['required'] as bool? ?? false;
    final enabled = fieldData['enabled'] as bool? ?? true;
    final validators =
        _buildValidators(fieldData['validators'] as List<dynamic>?, required);

    final decoration = InputDecoration(
      labelText: label,
      hintText: hint,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10.0),
      ),
    );

    switch (type) {
      case 'text':
      case 'string':
        return FormBuilderTextField(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(validators),
          maxLines: fieldData['maxLines'] as int?,
          keyboardType: _getKeyboardType(fieldData['keyboardType'] as String?),
          valueTransformer: (value) {
            if (value == null) return null;
            return value.toString();
          },
        );

      case 'number':
      case 'integer':
        return FormBuilderTextField(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(validators),
          keyboardType: TextInputType.number,
          valueTransformer: (value) =>
              value?.isEmpty == true ? null : num.tryParse(value!),
        );

      case 'email':
        return FormBuilderTextField(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose([
            ...validators,
            FormBuilderValidators.email(),
          ]),
          keyboardType: TextInputType.emailAddress,
        );

      case 'password':
        return FormBuilderTextField(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(validators),
          obscureText: true,
        );

      case 'dropdown':
      case 'select':
        final options = fieldData['options'] as List<dynamic>? ?? [];
        return FormBuilderDropdown<String>(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(validators),
          items: options
              .map((option) => DropdownMenuItem(
                    value: option['value'] as String,
                    child: Text(option['label'] as String),
                  ))
              .toList(),
        );

      case 'checkbox':
      case 'boolean':
        return FormBuilderCheckbox(
          name: name,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<bool>>()),
          title: Text(label ?? name),
          subtitle: hint != null ? Text(hint) : null,
        );

      case 'switch':
        return FormBuilderSwitch(
          name: name,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<bool>>()),
          title: Text(label ?? name),
          subtitle: hint != null ? Text(hint) : null,
        );

      case 'radio':
        final options = fieldData['options'] as List<dynamic>? ?? [];
        return FormBuilderRadioGroup<String>(
          name: name,
          enabled: enabled,
          validator: FormBuilderValidators.compose(validators),
          decoration: InputDecoration(
            labelText: label,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10.0),
            ),
          ),
          options: options
              .map((option) => FormBuilderFieldOption(
                    value: option['value'] as String,
                    child: Text(option['label'] as String),
                  ))
              .toList(),
        );

      case 'checkbox_group':
        final options = fieldData['options'] as List<dynamic>? ?? [];
        return FormBuilderCheckboxGroup<String>(
          name: name,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<List<String>>>()),
          decoration: InputDecoration(
            labelText: label,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10.0),
            ),
          ),
          options: options
              .map((option) => FormBuilderFieldOption(
                    value: option['value'] as String,
                    child: Text(option['label'] as String),
                  ))
              .toList(),
        );

      case 'date':
        return FormBuilderDateTimePicker(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<DateTime>>()),
          inputType: InputType.date,
          format: _getDateFormat(fieldData['format'] as String?),
        );

      case 'time':
        return FormBuilderDateTimePicker(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<DateTime>>()),
          inputType: InputType.time,
        );

      case 'datetime':
        return FormBuilderDateTimePicker(
          name: name,
          decoration: decoration,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<DateTime>>()),
          inputType: InputType.both,
          format: _getDateFormat(fieldData['format'] as String?),
        );

      case 'slider':
        final min = (fieldData['min'] as num?)?.toDouble() ?? 0.0;
        final max = (fieldData['max'] as num?)?.toDouble() ?? 100.0;
        final divisions = fieldData['divisions'] as int?;
        return FormBuilderSlider(
          name: name,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<double>>()),
          min: min,
          max: max,
          divisions: divisions,
          decoration: InputDecoration(
            labelText: label,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10.0),
            ),
          ),
          initialValue: 0,
        );

      case 'range_slider':
        final min = (fieldData['min'] as num?)?.toDouble() ?? 0.0;
        final max = (fieldData['max'] as num?)?.toDouble() ?? 100.0;
        final divisions = fieldData['divisions'] as int?;
        return FormBuilderRangeSlider(
          name: name,
          enabled: enabled,
          validator: FormBuilderValidators.compose(
              validators.cast<FormFieldValidator<RangeValues>>()),
          min: min,
          max: max,
          divisions: divisions,
          decoration: InputDecoration(
            labelText: label,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10.0),
            ),
          ),
        );

      case 'file':
        // You need to add form_builder_file_picker to your pubspec.yaml and import it:
        // import 'package:form_builder_file_picker/form_builder_file_picker.dart';
        // Or, as a fallback, use a placeholder or another widget:
        return ListTile(
          title: Text(label ?? name),
          subtitle: hint != null ? Text(hint) : null,
          trailing: const Icon(Icons.attach_file),
          onTap: () {
            // Implement your file picker logic here
            if (widget.enableDebug) {
              debugPrint('File picker tapped for $name');
            }
          },
        );

      case 'spacer':
        final height = (fieldData['height'] as num?)?.toDouble() ?? 16.0;
        return SizedBox(height: height);

      case 'divider':
        return const Divider();

      case 'text_widget':
        return Text(
          fieldData['text'] as String? ?? '',
          style: _getTextStyle(fieldData['style'] as Map<String, dynamic>?),
        );

      case 'data_table':
        final columnsData = fieldData['columns'] as List<dynamic>? ?? [];

        var tableMetaEntity = widget.metaEntity;

        // Try to get rows from multiple sources in priority order:
        // 1. Explicit rows in fieldData
        // 2. widget.focEntityList
        // 3. Get from widget.focEntity by field name
        List<dynamic> rowsData;
        if (fieldData['rows'] != null) {
          rowsData = fieldData['rows'] as List<dynamic>;
        } else if (widget.focEntityList != null) {
          rowsData = widget.focEntityList!;
        } else if (widget.focEntity != null && name.isNotEmpty) {
          // Try to get the list from focEntity properties by field name
          final fieldValue = widget.focEntity!.properties[name];
          if (fieldValue is List) {
            rowsData = fieldValue;
            if (fieldData['meta_entity'] != null) {
              tableMetaEntity =
                  MetaService().getEntityByName(fieldData['meta_entity']);
            }
          } else {
            rowsData = [];
          }
        } else {
          rowsData = [];
        }

        final columns = [
          ...columnsData.map<DataColumn>((col) => DataColumn(
                label: Text(col['label']?.toString() ?? ''),
                numeric: col['numeric'] == true,
              )),
          ...widget.state
              .getCustomColumns(), // Add custom columns from subclass
          const DataColumn(
            label: Text('Actions'),
          ),
        ];

        final rows = rowsData.map<DataRow>((row) {
          final cells = [
            ...columnsData.map((col) {
              final key = col['key']?.toString() ?? '';
              return DataCell(col['checkbox'] == true
                  ? Icon(
                      row[key] ? Icons.check_circle : Icons.cancel,
                      color: row[key] ? Colors.green : Colors.red,
                    )
                  : Text(row[key]?.toString() ?? ''));
            }),
            ...widget.state
                .getCustomDataCells(row), // Add custom data cells from subclass
            DataCell(Row(
              children: [
                IconButton(
                  icon: const Icon(Icons.open_in_new),
                  tooltip: 'Open',
                  onPressed: () {
                    _editFocEntity(row);
                  },
                ),
                IconButton(
                  icon: const Icon(Icons.delete),
                  tooltip: 'Delete',
                  onPressed: () {
                    _deleteFocEntity(row);
                  },
                ),
              ],
            )),
          ];
          return DataRow(cells: cells);
        }).toList();

        return Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                ElevatedButton.icon(
                  icon: const Icon(Icons.add),
                  label: const Text('Add ++'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue,
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
                    // Open empty details view for new item
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => FocDetailsView(
                          metaEntity: tableMetaEntity!, //widget.metaEntity!,
                          itemId: null, // null means create new
                        ),
                      ),
                    );
                  },
                ),
              ],
            ),
            const SizedBox(height: 8),
            Container(
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey.shade400, width: 1.2),
                borderRadius: BorderRadius.circular(10),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: DataTable(
                  columns: columns,
                  rows: rows,
                  headingRowColor: MaterialStateProperty.resolveWith<Color?>(
                      (states) => Colors.blueGrey.shade700),
                  headingTextStyle: const TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    letterSpacing: 1.1,
                  ),
                ),
              ),
            ),
          ],
        );

      case 'section':
        return _buildFormFields(fieldData);

      default:
        if (widget.enableDebug) {
          debugPrint('Unknown field type: $type');
        }
        return null;
    }
  }

  List<String? Function(String?)> _buildValidators(
      List<dynamic>? validatorConfigs, bool required) {
    List<String? Function(String?)> validators = [];

    if (required) {
      validators.add(FormBuilderValidators.required());
    }

    if (validatorConfigs != null) {
      for (var config in validatorConfigs) {
        if (config is Map<String, dynamic>) {
          final type = config['type'] as String;
          switch (type) {
            case 'min_length':
              final minLength = config['value'] as int;
              validators.add(FormBuilderValidators.minLength(minLength));
              break;
            case 'max_length':
              final maxLength = config['value'] as int;
              validators.add(FormBuilderValidators.maxLength(maxLength));
              break;
            case 'min':
              final min = config['value'] as num;
              validators.add(FormBuilderValidators.min(min));
              break;
            case 'max':
              final max = config['value'] as num;
              validators.add(FormBuilderValidators.max(max));
              break;
            case 'pattern':
              final pattern = config['value'] as String;
              validators.add(FormBuilderValidators.match(pattern as RegExp));
              break;
            case 'custom':
              final message = config['message'] as String?;
              final pattern = config['pattern'] as String?;
              if (pattern != null) {
                validators.add(FormBuilderValidators.match(pattern as RegExp,
                    errorText: message));
              }
              break;
          }
        }
      }
    }

    return validators;
  }

  TextInputType? _getKeyboardType(String? type) {
    switch (type) {
      case 'number':
        return TextInputType.number;
      case 'email':
        return TextInputType.emailAddress;
      case 'phone':
        return TextInputType.phone;
      case 'url':
        return TextInputType.url;
      case 'multiline':
        return TextInputType.multiline;
      default:
        return null;
    }
  }

  dynamic _getDateFormat(String? format) {
    // You can implement custom date formatting here
    // For now, returning null to use default format
    return null;
  }

  TextStyle? _getTextStyle(Map<String, dynamic>? styleConfig) {
    if (styleConfig == null) return null;

    return TextStyle(
      fontSize: (styleConfig['fontSize'] as num?)?.toDouble(),
      fontWeight: _getFontWeight(styleConfig['fontWeight'] as String?),
      color: _getColor(styleConfig['color'] as String?),
      fontStyle: styleConfig['italic'] == true ? FontStyle.italic : null,
      decoration:
          styleConfig['underline'] == true ? TextDecoration.underline : null,
    );
  }

  FontWeight? _getFontWeight(String? weight) {
    switch (weight) {
      case 'bold':
        return FontWeight.bold;
      case 'normal':
        return FontWeight.normal;
      default:
        return null;
    }
  }

  Color? _getColor(String? colorString) {
    if (colorString == null) return null;
    try {
      if (colorString.startsWith('#')) {
        return Color(
            int.parse(colorString.substring(1), radix: 16) + 0xFF000000);
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  /// Get the form key to access form state
  GlobalKey<FormBuilderState> get formKey => _formKey;

  /// Save the form and return the values
  Map<String, dynamic>? saveForm() {
    if (_formKey.currentState?.saveAndValidate() == true) {
      final values = _formKey.currentState!.value;
      if (widget.onSaved != null) {
        widget.onSaved!(values);
      }
      return values;
    }
    return null;
  }

  /// Save the form and send API call using the Entity object
  Future<Map<String, dynamic>?> saveFormWithApi({
    required MetaEntity metaEntity,
    required BuildContext context,
    FocEntity Function(Map<String, dynamic>)? fromJson,
  }) async {
    if (_formKey.currentState?.saveAndValidate() == true) {
      final values = _formKey.currentState!.value;
      try {
        // Use provided fromJson or fallback to identity
        // If focEntity has an id, add it to the values before creating newEntity
        final updatedValues = Map<String, dynamic>.from(values);
        if (widget.focEntity != null && widget.focEntity!.id != null) {
          updatedValues['id'] = widget.focEntity!.id;
        }
        FocEntity newEntity = FocEntity(metaEntity, updatedValues);
        //final entity = fromJson != null ? fromJson(values) : values;
//        if (entity is FocEntity) {
        if (newEntity.id != null && newEntity.id > 0) {
          await FocService().updateItem(metaEntity, newEntity);
        } else {
          await FocService().insertItem(metaEntity, newEntity);
        }
        Navigator.pop(context, newEntity);
        // } else {
        //   // If not FocEntity, just return values
        //   return values;
        // }
      } catch (e) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
                content: Text('Failed to save item: $e'),
                backgroundColor: Colors.red),
          );
        }
        print('Failed to save item: $e');
      }
      return values;
    }
    return null;
  }

  /// Validate the form
  bool validateForm() {
    return _formKey.currentState?.validate() == true;
  }

  /// Reset the form
  void resetForm() {
    _formKey.currentState?.reset();
  }

  /// Get current form values
  Map<String, dynamic>? getCurrentValues() {
    return _formKey.currentState?.value;
  }

  void _editFocEntity(FocEntity item) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => FocDetailsView(
            metaEntity: item.metaEntity, itemId: item.id.toString()),
      ),
    ).then((updatedItem) {
      // if (updatedItem != null) {
      //   setState(() {
      //     futureItems = FocService().fetchItems(item.metaEntity);
      //   });
      // }
    });
  }

  Future<void> _deleteFocEntity(FocEntity item) async {
    try {
      debugPrint("About to delete ${item.id}");
      await FocService().deleteItem(item.metaEntity, item.id);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content: Text('Item ${item.id} deleted successfully'),
            backgroundColor: Colors.green),
      );
    } catch (e, stacktrace) {
      debugPrint("Error deleting item: $e");
      debugPrint("Stacktrace: $stacktrace");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content: Text('Failed to delete item: $e'),
            backgroundColor: Colors.red),
      );
    }
  }
}

abstract class JsonFormState<T extends StatefulWidget> extends State<T> {
  @override
  Widget build(BuildContext context);

  /// Override this method to add custom column headers
  /// Returns a list of DataColumn widgets that will be inserted before the Actions column
  List<DataColumn> getCustomColumns() {
    return [];
  }

  /// Override this method to add custom data cells for each row
  /// Returns a list of DataCell widgets that will be inserted before the Actions column
  /// The dynamic item is passed as parameter to allow cell content based on the row data
  List<DataCell> getCustomDataCells(dynamic item) {
    return [];
  }
}
