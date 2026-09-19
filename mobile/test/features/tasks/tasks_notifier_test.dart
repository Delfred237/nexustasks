import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:nexustasks_mobile/features/tasks/data/repositories/task_repository.dart';
import 'package:nexustasks_mobile/features/tasks/domain/models/task.dart';
import 'package:nexustasks_mobile/features/tasks/domain/models/task_filters.dart';
import 'package:nexustasks_mobile/features/tasks/domain/providers/tasks_provider.dart';

class MockTaskRepository extends Mock implements TaskRepository {}

Task buildTask(String id, {String status = 'TODO'}) => Task(
      publicId: id,
      title: 'Task $id',
      status: status,
      priority: 'MEDIUM',
      archived: false,
      createdAt: DateTime(2026),
      updatedAt: DateTime(2026),
    );

void main() {
  late MockTaskRepository repo;
  late ProviderContainer container;

  setUpAll(() {
    registerFallbackValue(const TaskFilters());
  });

  setUp(() {
    repo = MockTaskRepository();
    container = ProviderContainer(
      overrides: [taskRepositoryProvider.overrideWithValue(repo)],
    );
    addTearDown(container.dispose);
  });

  test('chargement initial récupère la page 0', () async {
    when(() => repo.getTasks(
          page: 0,
          size: 20,
          filters: any(named: 'filters'),
          search: any(named: 'search'),
        )).thenAnswer((_) async => TaskPageResult(
          tasks: [buildTask('1'), buildTask('2')],
          last: false,
          totalElements: 25,
        ));

    final state = await container.read(tasksProvider.future);

    expect(state.tasks.length, 2);
    expect(state.page, 0);
    expect(state.hasMore, isTrue);
  });

  test('loadMore ajoute la page suivante et détecte la fin', () async {
    when(() => repo.getTasks(
          page: 0,
          size: 20,
          filters: any(named: 'filters'),
          search: any(named: 'search'),
        )).thenAnswer((_) async => TaskPageResult(
          tasks: [buildTask('1')],
          last: false,
          totalElements: 2,
        ));
    when(() => repo.getTasks(
          page: 1,
          size: 20,
          filters: any(named: 'filters'),
          search: any(named: 'search'),
        )).thenAnswer((_) async => TaskPageResult(
          tasks: [buildTask('2')],
          last: true,
          totalElements: 2,
        ));

    await container.read(tasksProvider.future);
    await container.read(tasksProvider.notifier).loadMore();

    final state = container.read(tasksProvider).value;
    expect(state?.tasks.map((t) => t.publicId).toList(), ['1', '2']);
    expect(state?.hasMore, isFalse);
  });

  test('setArchived appelle archive puis recharge', () async {
    when(() => repo.getTasks(
          page: 0,
          size: 20,
          filters: any(named: 'filters'),
          search: any(named: 'search'),
        )).thenAnswer((_) async => TaskPageResult(
          tasks: [buildTask('1')],
          last: true,
          totalElements: 1,
        ));
    when(() => repo.archiveTask('1')).thenAnswer((_) async {});

    await container.read(tasksProvider.future);
    await container.read(tasksProvider.notifier).setArchived('1', true);

    verify(() => repo.archiveTask('1')).called(1);
    verify(() => repo.getTasks(
          page: 0,
          size: 20,
          filters: any(named: 'filters'),
          search: any(named: 'search'),
        )).called(2); // load initial + refresh
  });
}