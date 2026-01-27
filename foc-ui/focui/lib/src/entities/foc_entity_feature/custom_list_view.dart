import 'package:flutter/material.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';
import 'foc_list_view.dart';

/// Example custom widget class that wraps FocListView
/// This demonstrates how to create specialized views for specific entities
class CustomListView extends StatelessWidget {
  final MetaEntity metaEntity;

  const CustomListView({super.key, required this.metaEntity});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Custom ${metaEntity.name} View'),
        backgroundColor: Colors.purple.shade100,
      ),
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(16.0),
            margin: const EdgeInsets.all(8.0),
            decoration: BoxDecoration(
              color: Colors.purple.shade50,
              borderRadius: BorderRadius.circular(8.0),
              border: Border.all(color: Colors.purple.shade200),
            ),
            child: Row(
              children: [
                Icon(Icons.info, color: Colors.purple.shade600),
                const SizedBox(width: 8.0),
                Expanded(
                  child: Text(
                    'This is a custom view for ${metaEntity.name}',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.purple.shade700,
                    ),
                  ),
                ),
              ],
            ),
          ),
          // Embed the standard FocListView
          Expanded(
            child: FocListView(metaEntity: metaEntity),
          ),
        ],
      ),
    );
  }
}
