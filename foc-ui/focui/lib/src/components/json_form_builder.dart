import 'package:flutter/material.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';
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
  final FocEntity? focEntity;

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

  const JsonFormBuilder({
    super.key,
    this.metaEntity,
    this.focEntity,
    this.formData,
    this.jsonString,
    this.assetPath,
    this.initialValues,
    this.onChanged,
    this.onSaved,
    this.autovalidateMode = AutovalidateMode.disabled,
    this.formKey,
    this.enableDebug = false,
  }) : assert(
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

    return FormBuilder(
      key: _formKey,
      initialValue: widget.initialValues ?? {},
      autovalidateMode: widget.autovalidateMode,
      onChanged: () {
        if (widget.onChanged != null) {
          widget.onChanged!(_formKey.currentState?.value);
        }
      },
      child: _buildFormFields(_parsedFormData!),
    );
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
          child: ConstrainedBox(
            constraints: BoxConstraints(
                maxWidth: 800), // Set your desired max width here
            child: Row(
              children: fieldWidgets
                  .map((w) => Padding(
                        padding: EdgeInsets.only(right: spacing),
                        child: w,
                      ))
                  .toList(),
            ),
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
            child: ConstrainedBox(
              constraints: const BoxConstraints(
                  maxWidth: 800), // Set your desired max width here
              child: Column(
                children: fieldWidgets
                    .map((w) => Padding(
                          padding: EdgeInsets.only(top: spacing),
                          child: w,
                        ))
                    .toList(),
              ),
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

      case 'section':
        return _buildFormFields(fieldData);
/*
        final sectionTitle = fieldData['title'] as String?;
        final sectionFields = fieldData['fields'] as List<dynamic>? ?? [];
        final spacing = (fieldData['spacing'] as num?)?.toDouble() ?? 16.0;

        List<Widget> sectionWidgets = [];

        if (sectionTitle != null) {
          sectionWidgets.add(
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0),
              child: Text(
                sectionTitle,
                style: Theme.of(context).textTheme.titleMedium,
              ),
            ),
          );
        }

        for (var sectionFieldData in sectionFields) {
          if (sectionFieldData is Map<String, dynamic>) {
            final widget = _buildFormField(sectionFieldData);
            if (widget != null) {
              sectionWidgets.add(widget);
            }
          }
        }

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: sectionWidgets,
        );
*/
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
        FocEntity newEntity = FocEntity(updatedValues);
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
}
