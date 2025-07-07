class FocEntity {
  Map<String, dynamic> properties;

  FocEntity(this.properties);

  factory FocEntity.fromJson(Map<String, dynamic> json) {
    //We clone the properties to avoid modifying the original map
    return FocEntity(Map<String, dynamic>.from(json));
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
