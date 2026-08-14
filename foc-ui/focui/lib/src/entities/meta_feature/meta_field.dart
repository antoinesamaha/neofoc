class MetaField {
  final int sqlType;
  final String dbName;
  final String name;
  final int type;
  final String? storageName;

  MetaField({
    required this.name,
    required this.type,
    required this.sqlType,
    required this.dbName,
    this.storageName,
  });

  bool get isForeignKey => storageName != null && storageName!.isNotEmpty;

  factory MetaField.fromJson(Map<String, dynamic> json) {
    return MetaField(
      sqlType: json['sqlType'],
      dbName: json['dbName'],
      name: json['name'],
      type: json['type'],
      storageName: json['storageName'],
    );
  }
}
