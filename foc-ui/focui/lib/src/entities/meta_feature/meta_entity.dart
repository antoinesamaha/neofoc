import 'meta_field.dart';

class MetaEntity {
  final String module;
  final String name;
  final bool isListInCache;
  final List<MetaField> fields;
  final String storageName;

  MetaEntity({
    this.module = '',
    required this.name,
    this.isListInCache = false,
    required this.fields,
    required this.storageName,
  });

  factory MetaEntity.fromJson(Map<String, dynamic> json) {
    var fieldsFromJson = json['fields'] as List;
    List<MetaField> fieldsList = fieldsFromJson.map((field) => MetaField.fromJson(field)).toList();

    return MetaEntity(
      module: json['module'] ?? '',
      name: json['name'],
      isListInCache: json['isListInCache'] ?? false,
      fields: fieldsList,
      storageName: json['storageName'] ?? '',
    );
  }

  dynamic operator [](String key) {
    switch (key) {
      case 'module':
        return module;
      case 'name':
        return name;
      case 'isListInCache':
        return isListInCache;
      case 'fields':
        return fields;
      case 'storageName':
        return storageName;
      default:
        return null;
    }
  }
}
