import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexustasks_mobile/features/tasks/presentation/widgets/task_from_sheet.dart';

import '../../../../core/utils/error_utils.dart';
import '../../domain/models/task.dart';
import '../../domain/models/task_filters.dart';
import '../../domain/providers/tasks_provider.dart';
import '../widgets/task_card.dart';
import '../widgets/task_filters_sheet.dart';

class TasksScreen extends ConsumerStatefulWidget {
  const TasksScreen({super.key});

  @override
  ConsumerState<TasksScreen> createState() => _TasksScreenState();
}

class _TasksScreenState extends ConsumerState<TasksScreen> {
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _searchController = TextEditingController();
  bool _searchMode = false;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.removeListener(_onScroll);
    _scrollController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(tasksProvider.notifier).loadMore();
    }
  }

  void _toggleSearch() {
    setState(() {
      _searchMode = !_searchMode;
      if (!_searchMode) {
        _searchController.clear();
        ref.read(tasksProvider.notifier).setSearch('');
      }
    });
  }

  Future<void> _openFilters(TaskFilters current) async {
    final result = await showModalBottomSheet<TaskFilters>(
      context: context,
      builder: (_) => TaskFiltersSheet(initial: current),
    );
    if (result != null) {
      ref.read(tasksProvider.notifier).setFilters(result);
    }
  }

  Future<void> _openForm({Task? task}) async {
    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      builder: (_) => TaskFormSheet(task: task),
    );
  }

  @override
  Widget build(BuildContext context) {
    final tasksAsync = ref.watch(tasksProvider);
    final filters = tasksAsync.value?.filters ?? const TaskFilters();

    return Scaffold(
      appBar: AppBar(
        title: _searchMode
            ? TextField(
                controller: _searchController,
                autofocus: true,
                decoration: const InputDecoration(
                  hintText: 'Search tasks...',
                  border: InputBorder.none,
                ),
                onChanged: (value) =>
                    ref.read(tasksProvider.notifier).setSearch(value),
              )
            : const Text('Tasks'),
        actions: [
          IconButton(
            icon: Icon(_searchMode ? Icons.close : Icons.search),
            onPressed: _toggleSearch,
            tooltip: _searchMode ? 'Close search' : 'Search',
          ),
          filters.hasActiveFilters
              ? Badge.count(
                  count: filters.activeCount,
                  child: IconButton(
                    icon: const Icon(Icons.tune),
                    onPressed: () => _openFilters(filters),
                    tooltip: 'Filters',
                  ),
                )
              : IconButton(
                  icon: const Icon(Icons.tune),
                  onPressed: () => _openFilters(filters),
                  tooltip: 'Filters',
                ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openForm(),
        icon: const Icon(Icons.add),
        label: const Text('New Task'),
      ),
      body: tasksAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, _) => Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.error_outline,
                  size: 64,
                  color: Theme.of(context).colorScheme.error,
                ),
                const SizedBox(height: 16),
                Text(
                  describeError(error),
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
                const SizedBox(height: 16),
                FilledButton(
                  onPressed: () => ref.invalidate(tasksProvider),
                  child: const Text('Retry'),
                ),
              ],
            ),
          ),
        ),
        data: (state) {
          if (state.tasks.isEmpty) {
            return RefreshIndicator(
              onRefresh: () => ref.read(tasksProvider.notifier).refresh(),
              child: ListView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.all(24),
                children: [
                  const SizedBox(height: 80),
                  Icon(
                    Icons.checklist_rounded,
                    size: 72,
                    color: Theme.of(context).colorScheme.primary
                        .withValues(alpha: 0.5),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    filters.hasActiveFilters || state.search.isNotEmpty
                        ? 'No tasks match your filters'
                        : 'No tasks yet',
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 16),
                  Center(
                    child: FilledButton.icon(
                      onPressed: () => _openForm(),
                      icon: const Icon(Icons.add),
                      label: const Text('Create your first task'),
                    ),
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: () => ref.read(tasksProvider.notifier).refresh(),
            child: ListView.builder(
              controller: _scrollController,
              physics: const AlwaysScrollableScrollPhysics(),
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 96),
              itemCount: state.tasks.length + (state.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index == state.tasks.length) {
                  return const Padding(
                    padding: EdgeInsets.symmetric(vertical: 16),
                    child: Center(child: CircularProgressIndicator()),
                  );
                }
                final task = state.tasks[index];
                return TaskCard(
                  task: task,
                  onEdit: (t) => _openForm(task: t),
                );
              },
            ),
          );
        },
      ),
    );
  }
}
