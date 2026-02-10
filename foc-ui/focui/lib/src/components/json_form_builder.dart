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

  // Filter states keyed by "{tableName}_{filterKey}"
  // Each value: { 'operator': String, 'value': dynamic, 'value2': dynamic }
  final Map<String, Map<String, dynamic>> _filterStates = {};

  // Server-side search results keyed by table field name
  final Map<String, List<FocEntity>> _searchResults = {};
  // Tables currently running a search
  final Set<String> _searchingTables = {};

  // Pagination state keyed by table field name
  // Each value: { 'start': int, 'count': int, 'totalCount': int, 'currentPage': int }
  final Map<String, Map<String, int>> _paginationStates = {};
  // Track tables that have already scheduled their initial paginated load
  final Set<String> _initialLoadScheduled = {};

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
        final tableOptions =
            fieldData['table_options'] as Map<String, dynamic>? ?? {};
        final showAddButton = tableOptions['showAddButton'] ?? true;
        final showEditButton = tableOptions['showEditButton'] ?? true;
        final showDeleteButton = tableOptions['showDeleteButton'] ?? true;
        final showActionsColumn =
            showEditButton == true || showDeleteButton == true;

        // Parse pagination config
        final paginationConfig =
            tableOptions['pagination'] as Map<String, dynamic>?;
        final paginationEnabled = paginationConfig != null;

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

        // Parse filters
        final filtersData = (tableOptions['filters'] as List<dynamic>?)
            ?.map((f) => f as Map<String, dynamic>)
            .toList();

        // Initialize pagination and trigger initial load if enabled
        if (paginationEnabled && tableMetaEntity != null) {
          _initPaginationState(name, paginationConfig);
          if (!_searchResults.containsKey(name) &&
              !_searchingTables.contains(name) &&
              !_initialLoadScheduled.contains(name)) {
            _initialLoadScheduled.add(name);
            WidgetsBinding.instance.addPostFrameCallback((_) {
              _performPaginatedSearch(name, filtersData, tableMetaEntity!);
            });
          }
        }

        // Use server-side search results if available, otherwise fallback to client-side
        if (_searchResults.containsKey(name)) {
          rowsData = _searchResults[name]!;
        } else if (paginationEnabled) {
          // Pagination enabled but no results yet - show empty while loading
          rowsData = [];
        } else if (filtersData != null &&
            filtersData.isNotEmpty &&
            tableMetaEntity == null) {
          // Client-side fallback only when no metaEntity for server search
          rowsData = _applyFilters(name, filtersData, rowsData);
        }

        final columns = [
          ...columnsData.map<DataColumn>((col) => DataColumn(
                label: Text(col['label']?.toString() ?? ''),
                numeric: col['numeric'] == true,
              )),
          ...widget.state
              .getCustomColumns(), // Add custom columns from subclass
          if (showActionsColumn)
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
            if (showActionsColumn)
              DataCell(Row(
                children: [
                  if (showEditButton == true)
                    IconButton(
                      icon: const Icon(Icons.open_in_new),
                      tooltip: 'Open',
                      onPressed: () {
                        _editFocEntity(row);
                      },
                    ),
                  if (showDeleteButton == true)
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
            if (filtersData != null && filtersData.isNotEmpty) ...[
              _buildFilterSection(context, name, filtersData, tableMetaEntity),
              const SizedBox(height: 10),
            ],
            if (showAddButton == true) ...[
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
            ],
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
            if (paginationEnabled && _paginationStates.containsKey(name)) ...[
              const SizedBox(height: 8),
              _buildPaginationBar(name, filtersData, tableMetaEntity),
            ],
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

  // ── Filter helpers ──────────────────────────────────────────────────

  List<String> _getOperatorsForType(String type) {
    switch (type) {
      case 'string':
        return ['contains', '=', '!=', 'isNull', 'isNotNull'];
      case 'numeric':
        return ['=', '>=', '<=', '>', '<', '!=', 'between', 'isNull', 'isNotNull'];
      case 'date':
        return ['=', '>=', '<=', 'between', 'isNull', 'isNotNull'];
      default:
        return ['='];
    }
  }

  String _operatorLabel(String op) {
    switch (op) {
      case 'contains':
        return 'Contains';
      case 'like':
        return 'Like';
      case '=':
        return 'Equals';
      case '!=':
        return 'Not equals';
      case '>=':
        return '>=';
      case '<=':
        return '<=';
      case '>':
        return '>';
      case '<':
        return '<';
      case 'between':
        return 'Between';
      case 'isNull':
        return 'Is Null';
      case 'isNotNull':
        return 'Is Not Null';
      case 'in':
        return 'In';
      default:
        return op;
    }
  }

  Map<String, dynamic> _getFilterState(String tableName, String key) {
    final stateKey = '${tableName}_$key';
    return _filterStates[stateKey] ??= {
      'operator': null,
      'value': null,
      'value2': null,
    };
  }

  /// Initializes pagination state from the JSON table_options config.
  void _initPaginationState(String tableName, Map<String, dynamic> paginationConfig) {
    if (_paginationStates.containsKey(tableName)) return;
    final defaultPageSize = paginationConfig['defaultPageSize'] as int? ?? 50;
    _paginationStates[tableName] = {
      'start': 0,
      'count': defaultPageSize,
      'totalCount': 0,
      'currentPage': 1,
    };
  }

  /// Performs a server-side search with both filters and pagination
  Future<void> _performPaginatedSearch(
      String tableName,
      List<Map<String, dynamic>>? filtersData,
      MetaEntity metaEntity) async {
    final paginationState = _paginationStates[tableName];
    if (paginationState == null) return;

    Map<String, dynamic> searchBody;
    if (filtersData != null && filtersData.isNotEmpty) {
      searchBody = _buildSearchBody(tableName, filtersData);
    } else {
      searchBody = {'filters': {}};
    }

    searchBody['pagination'] = {
      'start': paginationState['start'],
      'count': paginationState['count'],
    };

    setState(() => _searchingTables.add(tableName));
    try {
      final results = await FocService().searchItems(metaEntity, searchBody);
      final data = results['data'] as List<dynamic>;
      final totalCount = results['totalCount'] as int? ?? data.length;
      if (mounted) {
        setState(() {
          _searchResults[tableName] = data
              .map((item) =>
                  FocEntity.fromJson(metaEntity, item as Map<String, dynamic>))
              .toList();
          paginationState['totalCount'] = totalCount;
          _searchingTables.remove(tableName);
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _searchingTables.remove(tableName));
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: Text('Search failed: $e'),
              backgroundColor: Colors.red),
        );
      }
    }
  }

  Widget _buildPaginationBar(
      String tableName,
      List<Map<String, dynamic>>? filtersData,
      MetaEntity? metaEntity) {
    final state = _paginationStates[tableName]!;
    final currentPage = state['currentPage']!;
    final pageSize = state['count']!;
    final totalCount = state['totalCount']!;
    final totalPages =
        totalCount > 0 ? ((totalCount + pageSize - 1) ~/ pageSize) : 1;
    final isSearching = _searchingTables.contains(tableName);
    final pageSizeOptions = [10, 25, 50, 100];

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Page size selector
          Row(
            children: [
              const Text('Rows per page: ', style: TextStyle(fontSize: 13)),
              DropdownButton<int>(
                value: pageSizeOptions.contains(pageSize)
                    ? pageSize
                    : pageSizeOptions.first,
                underline: const SizedBox(),
                style: const TextStyle(fontSize: 13, color: Colors.black),
                items: pageSizeOptions
                    .map((size) =>
                        DropdownMenuItem(value: size, child: Text('$size')))
                    .toList(),
                onChanged: isSearching
                    ? null
                    : (newSize) {
                        if (newSize != null && metaEntity != null) {
                          setState(() {
                            state['count'] = newSize;
                            state['start'] = 0;
                            state['currentPage'] = 1;
                          });
                          _performPaginatedSearch(
                              tableName, filtersData, metaEntity);
                        }
                      },
              ),
            ],
          ),
          // Info text
          Text(
            '${totalCount > 0 ? state['start']! + 1 : 0}-'
            '${(state['start']! + pageSize).clamp(0, totalCount)}'
            ' of $totalCount',
            style: const TextStyle(fontSize: 13, color: Colors.grey),
          ),
          // Navigation buttons
          Row(
            children: [
              IconButton(
                icon: const Icon(Icons.first_page, size: 20),
                tooltip: 'First page',
                onPressed:
                    (isSearching || currentPage <= 1 || metaEntity == null)
                        ? null
                        : () {
                            setState(() {
                              state['currentPage'] = 1;
                              state['start'] = 0;
                            });
                            _performPaginatedSearch(
                                tableName, filtersData, metaEntity);
                          },
              ),
              IconButton(
                icon: const Icon(Icons.chevron_left, size: 20),
                tooltip: 'Previous page',
                onPressed:
                    (isSearching || currentPage <= 1 || metaEntity == null)
                        ? null
                        : () {
                            setState(() {
                              state['currentPage'] = currentPage - 1;
                              state['start'] = (currentPage - 2) * pageSize;
                            });
                            _performPaginatedSearch(
                                tableName, filtersData, metaEntity);
                          },
              ),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                child: Text(
                  'Page $currentPage of $totalPages',
                  style: const TextStyle(fontSize: 13),
                ),
              ),
              IconButton(
                icon: const Icon(Icons.chevron_right, size: 20),
                tooltip: 'Next page',
                onPressed: (isSearching ||
                        currentPage >= totalPages ||
                        metaEntity == null)
                    ? null
                    : () {
                        setState(() {
                          state['currentPage'] = currentPage + 1;
                          state['start'] = currentPage * pageSize;
                        });
                        _performPaginatedSearch(
                            tableName, filtersData, metaEntity);
                      },
              ),
              IconButton(
                icon: const Icon(Icons.last_page, size: 20),
                tooltip: 'Last page',
                onPressed: (isSearching ||
                        currentPage >= totalPages ||
                        metaEntity == null)
                    ? null
                    : () {
                        setState(() {
                          state['currentPage'] = totalPages;
                          state['start'] = (totalPages - 1) * pageSize;
                        });
                        _performPaginatedSearch(
                            tableName, filtersData, metaEntity);
                      },
              ),
            ],
          ),
        ],
      ),
    );
  }

  List<dynamic> _applyFilters(
      String tableName, List<Map<String, dynamic>> filters, List<dynamic> rows) {
    return rows.where((row) {
      for (final filter in filters) {
        final key = filter['key'] as String;
        final type = filter['type'] as String? ?? 'string';
        final state = _getFilterState(tableName, key);
        final op = state['operator'] as String?;
        final value = state['value'];

        if (op == null || value == null || value.toString().isEmpty) continue;

        final cellRaw = row is Map ? row[key] : null;
        final cellStr = cellRaw?.toString() ?? '';

        switch (type) {
          case 'string':
            final cellLower = cellStr.toLowerCase();
            final valLower = value.toString().toLowerCase();
            switch (op) {
              case 'contains':
                if (!cellLower.contains(valLower)) return false;
              case 'equals':
                if (cellLower != valLower) return false;
              case 'not_equals':
                if (cellLower == valLower) return false;
              case 'not_contains':
                if (cellLower.contains(valLower)) return false;
            }
            break;
          case 'numeric':
            final cellNum = num.tryParse(cellStr);
            final valNum = num.tryParse(value.toString());
            if (cellNum == null || valNum == null) return false;
            switch (op) {
              case 'equals':
                if (cellNum != valNum) return false;
              case 'greater_than':
                if (cellNum <= valNum) return false;
              case 'less_than':
                if (cellNum >= valNum) return false;
              case 'greater_or_equal':
                if (cellNum < valNum) return false;
              case 'less_or_equal':
                if (cellNum > valNum) return false;
              case 'between':
                final val2 = state['value2'];
                final valNum2 =
                    val2 != null ? num.tryParse(val2.toString()) : null;
                if (valNum2 == null) return false;
                if (cellNum < valNum || cellNum > valNum2) return false;
            }
            break;
          case 'date':
            final cellDate = DateTime.tryParse(cellStr);
            final valDate =
                value is DateTime ? value : DateTime.tryParse(value.toString());
            if (cellDate == null || valDate == null) return false;
            final cellDay =
                DateTime(cellDate.year, cellDate.month, cellDate.day);
            final valDay =
                DateTime(valDate.year, valDate.month, valDate.day);
            switch (op) {
              case 'equals':
                if (cellDay != valDay) return false;
              case 'after':
                if (!cellDay.isAfter(valDay)) return false;
              case 'before':
                if (!cellDay.isBefore(valDay)) return false;
              case 'between':
                final val2 = state['value2'];
                final valDate2 = val2 is DateTime
                    ? val2
                    : DateTime.tryParse(val2?.toString() ?? '');
                if (valDate2 == null) return false;
                final valDay2 =
                    DateTime(valDate2.year, valDate2.month, valDate2.day);
                if (cellDay.isBefore(valDay) || cellDay.isAfter(valDay2)) {
                  return false;
                }
            }
            break;
        }
      }
      return true;
    }).toList();
  }

  Widget _buildFilterSection(
      BuildContext context, String tableName,
      List<Map<String, dynamic>> filters, MetaEntity? metaEntity) {
    final isSearching = _searchingTables.contains(tableName);
    return ExpansionTile(
      title: Row(
        children: [
          Icon(Icons.filter_list, color: Colors.blueGrey.shade700, size: 20),
          const SizedBox(width: 8),
          Text('Filters',
              style: TextStyle(
                  fontSize: 15,
                  fontWeight: FontWeight.w600,
                  color: Colors.blueGrey.shade700)),
        ],
      ),
      tilePadding: const EdgeInsets.symmetric(horizontal: 12),
      childrenPadding: const EdgeInsets.fromLTRB(16, 0, 16, 12),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
        side: BorderSide(color: Colors.grey.shade300),
      ),
      collapsedShape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
        side: BorderSide(color: Colors.grey.shade300),
      ),
      children: [
        ...filters.map((filter) {
          final key = filter['key'] as String;
          final label = filter['label'] as String? ?? key;
          final type = filter['type'] as String? ?? 'string';
          final operators = _getOperatorsForType(type);
          final state = _getFilterState(tableName, key);
          final selectedOp = state['operator'] as String?;
          final isNullOp = selectedOp == 'isNull' || selectedOp == 'isNotNull';

          return Padding(
            padding: const EdgeInsets.symmetric(vertical: 6),
            child: Row(
              children: [
                SizedBox(
                  width: 130,
                  child: Text(label,
                      style: const TextStyle(
                          fontWeight: FontWeight.w500, fontSize: 14)),
                ),
                const SizedBox(width: 8),
                SizedBox(
                  width: 150,
                  child: DropdownButtonFormField<String>(
                    value: selectedOp,
                    decoration: InputDecoration(
                      isDense: true,
                      contentPadding: const EdgeInsets.symmetric(
                          horizontal: 10, vertical: 8),
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8)),
                    ),
                    hint: const Text('Operator', style: TextStyle(fontSize: 13)),
                    items: operators
                        .map((op) => DropdownMenuItem(
                            value: op,
                            child: Text(_operatorLabel(op),
                                style: const TextStyle(fontSize: 13))))
                        .toList(),
                    onChanged: (val) {
                      setState(() {
                        state['operator'] = val;
                        state['value'] = null;
                        state['value2'] = null;
                      });
                    },
                  ),
                ),
                const SizedBox(width: 8),
                if (selectedOp != null && !isNullOp) ...[
                  Expanded(child: _buildFilterInput(context, tableName, key, type, 'value', state)),
                  if (selectedOp == 'between') ...[
                    const Padding(
                      padding: EdgeInsets.symmetric(horizontal: 8),
                      child: Text('and', style: TextStyle(fontSize: 13)),
                    ),
                    Expanded(
                        child: _buildFilterInput(context, tableName, key, type, 'value2', state)),
                  ],
                ],
              ],
            ),
          );
        }),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            TextButton.icon(
              icon: const Icon(Icons.clear, size: 18),
              label: const Text('Clear'),
              onPressed: () {
                setState(() {
                  for (final filter in filters) {
                    final stateKey = '${tableName}_${filter['key']}';
                    _filterStates.remove(stateKey);
                  }
                  _searchResults.remove(tableName);
                  // Reset pagination to page 1 on clear
                  final paginationState = _paginationStates[tableName];
                  if (paginationState != null) {
                    paginationState['start'] = 0;
                    paginationState['currentPage'] = 1;
                  }
                });
                // Re-fetch with pagination (no filters)
                final paginationState = _paginationStates[tableName];
                if (paginationState != null && metaEntity != null) {
                  _performPaginatedSearch(tableName, null, metaEntity);
                }
              },
            ),
            const SizedBox(width: 8),
            ElevatedButton.icon(
              icon: isSearching
                  ? const SizedBox(
                      width: 18, height: 18,
                      child: CircularProgressIndicator(
                          strokeWidth: 2, color: Colors.white))
                  : const Icon(Icons.search, size: 18),
              label: Text(isSearching ? 'Searching...' : 'Apply'),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.blueGrey.shade700,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20)),
                padding:
                    const EdgeInsets.symmetric(horizontal: 18, vertical: 10),
              ),
              onPressed: isSearching
                  ? null
                  : () async {
                      if (metaEntity == null) {
                        // Fallback to client-side filtering
                        setState(() {});
                        return;
                      }

                      // Reset pagination to page 1 when filters change
                      final paginationState = _paginationStates[tableName];
                      if (paginationState != null) {
                        paginationState['start'] = 0;
                        paginationState['currentPage'] = 1;
                        await _performPaginatedSearch(
                            tableName, filters, metaEntity);
                        return;
                      }

                      // Non-paginated search (original logic)
                      final searchBody =
                          _buildSearchBody(tableName, filters);
                      setState(() => _searchingTables.add(tableName));
                      try {
                        final results = await FocService()
                            .searchItems(metaEntity, searchBody);
                        final data = results['data'] as List<dynamic>;
                        if (mounted) {
                          setState(() {
                            _searchResults[tableName] = data
                                .map((item) => FocEntity.fromJson(
                                    metaEntity,
                                    item as Map<String, dynamic>))
                                .toList();
                            _searchingTables.remove(tableName);
                          });
                        }
                      } catch (e) {
                        if (mounted) {
                          setState(
                              () => _searchingTables.remove(tableName));
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                                content: Text('Search failed: $e'),
                                backgroundColor: Colors.red),
                          );
                        }
                      }
                    },
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildFilterInput(BuildContext context, String tableName, String key,
      String type, String valueKey, Map<String, dynamic> state) {
    if (type == 'date') {
      final dateVal = state[valueKey] as DateTime?;
      return InkWell(
        onTap: () async {
          final picked = await showDatePicker(
            context: context,
            initialDate: dateVal ?? DateTime.now(),
            firstDate: DateTime(2000),
            lastDate: DateTime(2100),
          );
          if (picked != null) {
            setState(() {
              state[valueKey] = picked;
            });
          }
        },
        child: InputDecorator(
          decoration: InputDecoration(
            isDense: true,
            contentPadding:
                const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
            suffixIcon: const Icon(Icons.calendar_today, size: 16),
          ),
          child: Text(
            dateVal != null
                ? '${dateVal.year}-${dateVal.month.toString().padLeft(2, '0')}-${dateVal.day.toString().padLeft(2, '0')}'
                : '',
            style: const TextStyle(fontSize: 13),
          ),
        ),
      );
    }

    // String or numeric
    return TextFormField(
      initialValue: state[valueKey]?.toString() ?? '',
      keyboardType:
          type == 'numeric' ? TextInputType.number : TextInputType.text,
      decoration: InputDecoration(
        isDense: true,
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
        hintText: type == 'numeric' ? '0' : 'Value',
        hintStyle: const TextStyle(fontSize: 13),
      ),
      style: const TextStyle(fontSize: 13),
      onChanged: (val) {
        state[valueKey] = val;
      },
    );
  }

  // ── Search API helpers ─────────────────────────────────────────────

  String _formatDateForApi(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
  }

  dynamic _formatFilterValue(String type, dynamic value) {
    if (type == 'date' && value is DateTime) {
      return _formatDateForApi(value);
    } else if (type == 'numeric') {
      return num.tryParse(value.toString()) ?? value;
    }
    return value;
  }

  Map<String, dynamic> _buildSearchBody(
      String tableName, List<Map<String, dynamic>> filters) {
    final Map<String, dynamic> apiFilters = {};

    for (final filter in filters) {
      final key = filter['key'] as String;
      final type = filter['type'] as String? ?? 'string';
      final state = _getFilterState(tableName, key);
      final op = state['operator'] as String?;
      final value = state['value'];

      if (op == null) continue;

      // Operators that don't need a value
      if (op == 'isNull' || op == 'isNotNull') {
        apiFilters[key] = {'operator': op};
        continue;
      }

      if (value == null || value.toString().isEmpty) continue;

      final formattedValue = _formatFilterValue(type, value);

      if (op == '=') {
        // Simple equality - direct value
        apiFilters[key] = formattedValue;
      } else if (op == 'between') {
        final value2 = state['value2'];
        if (value2 != null && value2.toString().isNotEmpty) {
          apiFilters[key] = {
            'operator': 'between',
            'from': formattedValue,
            'to': _formatFilterValue(type, value2),
          };
        }
      } else {
        apiFilters[key] = {
          'operator': op,
          'value': formattedValue,
        };
      }
    }

    return {'filters': apiFilters};
  }

  // ── End filter helpers ────────────────────────────────────────────

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
