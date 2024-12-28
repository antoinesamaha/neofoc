// lib/src/sample_feature/sample_item_list_view.dart
import 'package:flutter/material.dart';
import '../../settings/settings_view.dart';
import 'foc_entity.dart';
import 'foc_service.dart';

class CountryListView extends StatefulWidget {
  const CountryListView({super.key});

  static const routeName = '/countries';

  @override
  _CountryListViewState createState() => _CountryListViewState();
}

class _CountryListViewState extends State<CountryListView> {
  late Future<List<FocEntity>> futureItems;

  @override
  void initState() {
    super.initState();
    futureItems = FocService().fetchItems();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Countries'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.restorablePushNamed(context, SettingsView.routeName);
            },
          ),
        ],
      ),
      body: FutureBuilder<List<FocEntity>>(
        future: futureItems,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('No items found'));
          } else {
            return DataTable(
              columns: const [
                DataColumn(label: Text('ID')),
                DataColumn(label: Text('Name')),
              ],
              rows: snapshot.data!.map((item) {
                return DataRow(cells: [
                  DataCell(Text(item['REF'].toString())),
                  DataCell(Text(item['COUNTRY'])),
                ]);
              }).toList(),
            );
          }
        },
      ),
    );
  }
}
