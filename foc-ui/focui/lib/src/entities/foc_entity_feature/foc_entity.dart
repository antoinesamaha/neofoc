import 'package:focui/src/entities/meta_feature/meta_entity.dart';

class FocEntity {
  MetaEntity metaEntity;
  Map<String, dynamic> properties;

  FocEntity(this.metaEntity, this.properties);

  factory FocEntity.fromJson(MetaEntity metaEntity, Map<String, dynamic> json) {
    //We clone the properties to avoid modifying the original map
    return FocEntity(metaEntity, Map<String, dynamic>.from(json));
  }

  dynamic get id {
    var idValue = properties['id'] ?? properties['REF'];
    if (idValue is String) {
      return int.tryParse(idValue);
    }
    return idValue;
  }

  set id(dynamic value) {
    properties['id'] = value;
  }

  dynamic operator [](String key) {
    return properties[key];
  }

  Map<String, dynamic> toJson() {
    return properties;
  }
}
