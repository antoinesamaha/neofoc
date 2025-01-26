class FocEntity {
  Map<String, dynamic> properties;

  FocEntity(this.properties);

  factory FocEntity.fromJson(Map<String, dynamic> json) {
    return FocEntity(json);
  }

  dynamic get id {
    var idValue = properties['id'] ?? properties['REF'];
    if (idValue is String) {
      return int.tryParse(idValue);
    }
    return idValue;
  }

  dynamic operator [](String key) {
    return properties[key];
  }

  Map<String, dynamic> toJson() {
    return properties;
  }
}
