class FocEntity {
  Map<String, dynamic> properties;

  FocEntity(this.properties);

  factory FocEntity.fromJson(Map<String, dynamic> json) {
    return FocEntity(json);
  }

  get id => properties['id'];

  dynamic operator [](String key) {
    return properties[key];
  }
}
