import 'foc_entity.dart';

class FocCache {
  FocCache._internal();
  static final FocCache _instance = FocCache._internal();
  factory FocCache() => _instance;

  // storageName → id → FocEntity
  final Map<String, Map<int, FocEntity>> _store = {};

  void populate(String storageName, List<FocEntity> entities) {
    _store[storageName] = {
      for (final e in entities)
        if (e.id != null) e.id as int: e,
    };
  }

  void put(String storageName, FocEntity entity) {
    if (entity.id == null) return;
    _store[storageName] ??= {};
    _store[storageName]![entity.id as int] = entity;
  }

  FocEntity? get(String storageName, int id) {
    return _store[storageName]?[id];
  }

  bool has(String storageName, int id) {
    return _store[storageName]?.containsKey(id) ?? false;
  }
}
