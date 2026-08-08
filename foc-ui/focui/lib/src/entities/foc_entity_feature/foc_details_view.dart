import 'package:flutter/material.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';
import 'package:focui/main.dart';
import 'package:focui/src/auth/auth_service.dart';
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
  final Map<String, dynamic>? defaultValues;

  /// Optional widget shown above the form (e.g. a live status banner).
  /// It is rendered independently from the form and does not affect editable fields.
  final Widget? statusWidget;

  const FocDetailsView(
      {super.key,
      required this.metaEntity,
      required this.itemId,
      this.defaultValues,
      this.statusWidget});

  static const routeName = '/entity/details';

  @override
  _FocDetailsViewState createState() => _FocDetailsViewState();
}

class _FocDetailsViewState extends JsonFormState<FocDetailsView> {
  final _formKey = GlobalKey<FormBuilderState>();
  late Future<FocEntity> futureItem;
  late Future<String?> futureFormTitle;

  @override
  void initState() {
    super.initState();
    futureItem =
        FocService().fetchItemDetails(widget.metaEntity, widget.itemId ?? '');
    futureFormTitle = _loadFormTitle();
  }

  /// Reads the "title" key from the entity's form JSON, if any, to use as
  /// the AppBar title instead of the raw entity/table name.
  Future<String?> _loadFormTitle() async {
    final entityFormFile =
        'assets/forms/${widget.metaEntity.name.toLowerCase().replaceAll(' ', '_')}_form.json';
    final formData = await loadJsonAssetIfExists(entityFormFile);
    return formData?['title'] as String?;
  }

  Future<void> _showSetPasswordDialog(String username) async {
    final formKey = GlobalKey<FormState>();
    final newPasswordCtrl = TextEditingController();
    final confirmCtrl = TextEditingController();
    bool loading = false;

    await showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setState) => AlertDialog(
          title: Text('Set Password for $username'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: newPasswordCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'New Password'),
                  validator: (v) =>
                      (v == null || v.isEmpty) ? 'Required' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: confirmCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'Confirm Password'),
                  validator: (v) => v != newPasswordCtrl.text
                      ? 'Passwords do not match'
                      : null,
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: loading ? null : () => Navigator.pop(ctx),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: loading
                  ? null
                  : () async {
                      if (!formKey.currentState!.validate()) return;
                      setState(() => loading = true);
                      try {
                        final authService = getIt<AuthService>();
                        await authService.changePassword(
                          username,
                          null,
                          newPasswordCtrl.text,
                        );
                        Navigator.pop(ctx);
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                              content:
                                  Text('Password for $username updated')),
                        );
                      } catch (e) {
                        setState(() => loading = false);
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                              content: Text(e
                                  .toString()
                                  .replaceFirst('Exception: ', ''))),
                        );
                      }
                    },
              child: loading
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2))
                  : const Text('Set Password'),
            ),
          ],
        ),
      ),
    );
  }

  void _saveItem(FocEntity focEntity) async {
    if (_formKey.currentState?.saveAndValidate() ?? false) {
      final updatedData = Map<String, dynamic>.from(_formKey.currentState!.value);
      // Merge hidden defaults (e.g. parent FK) that are not rendered in the form
      if (widget.defaultValues != null) {
        for (final entry in widget.defaultValues!.entries) {
          updatedData.putIfAbsent(entry.key, () => entry.value);
        }
      }
      final updatedEntity = FocEntity.fromJson(focEntity.metaEntity, updatedData);
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
        title: FutureBuilder<String?>(
          future: futureFormTitle,
          builder: (context, snapshot) {
            final title = snapshot.data ??
                '${widget.metaEntity.name[0].toUpperCase()}${widget.metaEntity.name.substring(1)}';
            return Text(title);
          },
        ),
        actions: [
          if (widget.metaEntity.storageName == 'FUSER')
            FutureBuilder<FocEntity>(
              future: futureItem,
              builder: (context, snapshot) => IconButton(
                icon: const Icon(Icons.key),
                tooltip: 'Set Password',
                onPressed: snapshot.hasData
                    ? () => _showSetPasswordDialog(
                        snapshot.data!['NAME']?.toString() ?? '')
                    : null,
              ),
            ),
          IconButton(
            icon: const Icon(Icons.save),
            onPressed: () async {
              final focEntity = await futureItem;
              _saveItem(focEntity);
            },
          ),
        ],
      ),
      body: Column(
        children: [
          if (widget.statusWidget != null) widget.statusWidget!,
          Expanded(
            child: FutureBuilder<FocEntity>(
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
                        if (formSnapshot.connectionState ==
                            ConnectionState.waiting) {
                          return const Center(
                              child: CircularProgressIndicator());
                        } else if (formSnapshot.hasError) {
                          return Center(
                              child: Text(
                                  'Error loading form: ${formSnapshot.error}'));
                        } else if (formSnapshot.hasData) {
                          return formSnapshot.data!;
                        } else {
                          return const Center(
                              child: Text('No form available'));
                        }
                      },
                    ),
                  );
                }
              },
            ),
          ),
        ],
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
        hiddenValues: widget.defaultValues,
        onChanged: (values) {
          // Handle form changes if needed
        },
        autovalidateMode: AutovalidateMode.onUserInteraction,
        // The form's root "title" (if any) is already shown as the AppBar
        // title above - don't render it again inline.
        showRootTitle: false,
        state: this,
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
