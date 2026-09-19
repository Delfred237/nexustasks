import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../categories/domain/providers/categories_provider.dart';
import '../../domain/models/task_filters.dart';

class TaskFiltersSheet extends ConsumerStatefulWidget {
  const TaskFiltersSheet({super.key, required this.initial});

  final TaskFilters initial;

  @override
  ConsumerState<TaskFiltersSheet> createState() => _TaskFiltersSheetState();
}

class _TaskFiltersSheetState extends ConsumerState<TaskFiltersSheet> {
  late TaskFilters _filters;

  @override
  void initState() {
    super.initState();
    _filters = widget.initial;
  }

  @override
  Widget build(BuildContext context) {
    final categories = ref.watch(categoriesProvider).value ?? const [];

    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 16, 24, 24),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(
            'Filters',
            style: Theme.of(context).textTheme.titleLarge
                ?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 20),
          Text('Status', style: Theme.of(context).textTheme.titleSmall),
          const SizedBox(height: 8),
          Wrap(
            spacing: 8,
            children: [
              ChoiceChip(
                label: const Text('To Do'),
                selected: _filters.status == 'TODO',
                onSelected: (selected) => setState(
                  () => _filters = _filters.copyWith(
                    status: selected ? 'TODO' : null,
                    clearStatus: !selected,
                  ),
                ),
              ),
              ChoiceChip(
                label: const Text('In Progress'),
                selected: _filters.status == 'IN_PROGRESS',
                onSelected: (selected) => setState(
                  () => _filters = _filters.copyWith(
                    status: selected ? 'IN_PROGRESS' : null,
                    clearStatus: !selected,
                  ),
                ),
              ),
              ChoiceChip(
                label: const Text('Completed'),
                selected: _filters.status == 'COMPLETED',
                onSelected: (selected) => setState(
                  () => _filters = _filters.copyWith(
                    status: selected ? 'COMPLETED' : null,
                    clearStatus: !selected,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Text('Priority', style: Theme.of(context).textTheme.titleSmall),
          const SizedBox(height: 8),
          Wrap(
            spacing: 8,
            children: [
              for (final entry in const {
                'LOW': 'Low',
                'MEDIUM': 'Medium',
                'HIGH': 'High',
                'URGENT': 'Urgent',
              }.entries)
                ChoiceChip(
                  label: Text(entry.value),
                  selected: _filters.priority == entry.key,
                  onSelected: (selected) => setState(
                    () => _filters = _filters.copyWith(
                      priority: selected ? entry.key : null,
                      clearPriority: !selected,
                    ),
                  ),
                ),
            ],
          ),
          if (categories.isNotEmpty) ...[
            const SizedBox(height: 16),
            Text('Category', style: Theme.of(context).textTheme.titleSmall),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              children: [
                for (final category in categories)
                  ChoiceChip(
                    label: Text(category.name),
                    selected: _filters.category == category.publicId,
                    onSelected: (selected) => setState(
                      () => _filters = _filters.copyWith(
                        category: selected ? category.publicId : null,
                        clearCategory: !selected,
                      ),
                    ),
                  ),
              ],
            ),
          ],
          const SizedBox(height: 8),
          SwitchListTile(
            title: const Text('Show archived tasks'),
            value: _filters.includeArchived,
            onChanged: (value) => setState(
              () => _filters = _filters.copyWith(includeArchived: value),
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: () =>
                      setState(() => _filters = const TaskFilters()),
                  child: const Text('Clear all'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: FilledButton(
                  onPressed: () => Navigator.of(context).pop(_filters),
                  child: const Text('Apply'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
