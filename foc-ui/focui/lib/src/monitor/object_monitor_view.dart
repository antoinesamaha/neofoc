import 'package:flutter/material.dart';
import 'monitor_service.dart';

class ObjectMonitorView extends StatefulWidget {
  static const routeName = '/monitor/objects';

  const ObjectMonitorView({super.key});

  @override
  State<ObjectMonitorView> createState() => _ObjectMonitorViewState();
}

class _ObjectMonitorViewState extends State<ObjectMonitorView> {
  late Future<MonitorResult> _future;
  final _service = MonitorService();

  static const _headerColor = Color(0xFF232946);
  static const _accentHigh = Color(0xFF4A00E0);
  static const _accentLow = Color(0xFF00C6FF);
  static const _bgColor = Color(0xFFF4F6FB);

  @override
  void initState() {
    super.initState();
    _future = _service.fetchObjectCounts();
  }

  void _refresh() => setState(() => _future = _service.fetchObjectCounts());

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _bgColor,
      appBar: AppBar(
        backgroundColor: _headerColor,
        foregroundColor: Colors.white,
        title: const Text(
          'Object Monitor',
          style: TextStyle(fontWeight: FontWeight.bold, letterSpacing: 0.5),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh',
            onPressed: _refresh,
          ),
        ],
      ),
      body: FutureBuilder<MonitorResult>(
        future: _future,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  CircularProgressIndicator(color: _accentHigh),
                  SizedBox(height: 16),
                  Text('Loading object counts…', style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }

          if (snapshot.hasError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(32),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.error_outline, color: Colors.redAccent, size: 56),
                    const SizedBox(height: 16),
                    Text(
                      snapshot.error.toString().replaceFirst('Exception: ', ''),
                      textAlign: TextAlign.center,
                      style: const TextStyle(color: Colors.redAccent),
                    ),
                    const SizedBox(height: 24),
                    ElevatedButton.icon(
                      onPressed: _refresh,
                      icon: const Icon(Icons.refresh),
                      label: const Text('Retry'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _accentHigh,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
            );
          }

          final result = snapshot.data!;
          final tracked = result.items.where((e) => e.count >= 0).toList();
          final untracked = result.items.where((e) => e.count < 0).toList();
          final maxCount = tracked.isEmpty ? 1 : tracked.first.count;

          return ListView(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            children: [
              _SummaryCard(
                total: result.total,
                trackedCount: tracked.length,
                untrackedCount: untracked.length,
              ),
              const SizedBox(height: 8),
              if (tracked.isNotEmpty) ...[
                const _SectionLabel('Tracked Objects'),
                ...tracked.asMap().entries.map(
                      (e) => _TrackedRow(
                        item: e.value,
                        rank: e.key + 1,
                        maxCount: maxCount,
                        accentHigh: _accentHigh,
                        accentLow: _accentLow,
                      ),
                    ),
              ],
              if (untracked.isNotEmpty) ...[
                const SizedBox(height: 8),
                const _SectionLabel('Not Tracked'),
                ...untracked.map((e) => _UntrackedRow(item: e)),
              ],
            ],
          );
        },
      ),
    );
  }
}

// ─── Summary Card ─────────────────────────────────────────────────────────────

class _SummaryCard extends StatelessWidget {
  final int total;
  final int trackedCount;
  final int untrackedCount;

  const _SummaryCard({
    required this.total,
    required this.trackedCount,
    required this.untrackedCount,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 4),
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF232946), Color(0xFF4A00E0)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF232946).withOpacity(0.35),
            blurRadius: 14,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Row(
        children: [
          const Icon(Icons.memory_rounded, color: Colors.white60, size: 44),
          const SizedBox(width: 18),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '$total',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 40,
                  fontWeight: FontWeight.bold,
                  height: 1,
                ),
              ),
              const Text(
                'Total Objects in Memory',
                style: TextStyle(color: Colors.white60, fontSize: 13),
              ),
            ],
          ),
          const Spacer(),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              _StatBadge(value: '$trackedCount', label: 'tracked', bright: true),
              const SizedBox(height: 6),
              _StatBadge(value: '$untrackedCount', label: 'untracked', bright: false),
            ],
          ),
        ],
      ),
    );
  }
}

class _StatBadge extends StatelessWidget {
  final String value;
  final String label;
  final bool bright;

  const _StatBadge({required this.value, required this.label, required this.bright});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Text(
          value,
          style: TextStyle(
            color: bright ? Colors.white : Colors.white54,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        Text(
          label,
          style: TextStyle(
            color: bright ? Colors.white60 : Colors.white38,
            fontSize: 11,
          ),
        ),
      ],
    );
  }
}

// ─── Section Label ─────────────────────────────────────────────────────────────

class _SectionLabel extends StatelessWidget {
  final String label;

  const _SectionLabel(this.label);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 4, top: 12, bottom: 6),
      child: Text(
        label.toUpperCase(),
        style: const TextStyle(
          color: Color(0xFF9099B7),
          fontSize: 11,
          fontWeight: FontWeight.bold,
          letterSpacing: 1.4,
        ),
      ),
    );
  }
}

// ─── Tracked Row ──────────────────────────────────────────────────────────────

class _TrackedRow extends StatelessWidget {
  final FocDescCount item;
  final int rank;
  final int maxCount;
  final Color accentHigh;
  final Color accentLow;

  const _TrackedRow({
    required this.item,
    required this.rank,
    required this.maxCount,
    required this.accentHigh,
    required this.accentLow,
  });

  @override
  Widget build(BuildContext context) {
    final fraction = maxCount > 0 ? item.count / maxCount : 0.0;
    final barColor = Color.lerp(accentLow, accentHigh, fraction)!;

    return Card(
      margin: const EdgeInsets.symmetric(vertical: 4),
      elevation: 0,
      color: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(12, 12, 16, 12),
        child: Row(
          children: [
            // Rank badge
            Container(
              width: 28,
              height: 28,
              decoration: BoxDecoration(
                color: barColor.withOpacity(0.12),
                borderRadius: BorderRadius.circular(8),
              ),
              alignment: Alignment.center,
              child: Text(
                '$rank',
                style: TextStyle(
                  color: barColor,
                  fontWeight: FontWeight.bold,
                  fontSize: 12,
                ),
              ),
            ),
            const SizedBox(width: 12),
            // Name + bar
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    item.name,
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 14,
                      color: Color(0xFF232946),
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 7),
                  ClipRRect(
                    borderRadius: BorderRadius.circular(4),
                    child: LinearProgressIndicator(
                      value: fraction,
                      backgroundColor: const Color(0xFFE8ECF4),
                      valueColor: AlwaysStoppedAnimation<Color>(barColor),
                      minHeight: 5,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 14),
            // Count badge
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 5),
              decoration: BoxDecoration(
                color: barColor.withOpacity(0.12),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '${item.count}',
                style: TextStyle(
                  color: barColor,
                  fontWeight: FontWeight.bold,
                  fontSize: 14,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── Untracked Row ────────────────────────────────────────────────────────────

class _UntrackedRow extends StatelessWidget {
  final FocDescCount item;

  const _UntrackedRow({required this.item});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 3),
      elevation: 0,
      color: const Color(0xFFEEEFF3),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        child: Row(
          children: [
            Expanded(
              child: Text(
                item.name,
                style: const TextStyle(fontSize: 13, color: Color(0xFFAAAAAA)),
                overflow: TextOverflow.ellipsis,
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 3),
              decoration: BoxDecoration(
                color: Colors.grey.shade300,
                borderRadius: BorderRadius.circular(20),
              ),
              child: const Text(
                'N/A',
                style: TextStyle(
                  color: Colors.grey,
                  fontSize: 11,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
