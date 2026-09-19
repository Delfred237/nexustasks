import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nexustasks_mobile/features/tasks/domain/models/task.dart';
import 'package:nexustasks_mobile/features/tasks/presentation/widgets/task_card.dart';

void main() {
  final task = Task(
    publicId: 't1',
    title: 'Finalize report',
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    description: 'Include charts',
    archived: false,
    createdAt: DateTime(2026, 1, 1),
    updatedAt: DateTime(2026, 1, 2),
    dueDate: DateTime(2026, 9, 30),
    category: const TaskCategory(
      publicId: 'c1',
      name: 'Work',
      color: '#4f46e5',
    ),
  );

  testWidgets('affiche titre, badges statut/priorité et catégorie', (
    tester,
  ) async {
    await tester.pumpWidget(
      ProviderScope(
        child: MaterialApp(
          home: Scaffold(
            body: TaskCard(task: task, onEdit: (_) {}),
          ),
        ),
      ),
    );

    expect(find.text('Finalize report'), findsOneWidget);
    expect(find.text('In Progress'), findsOneWidget);
    expect(find.text('High'), findsOneWidget);
    expect(find.text('Work'), findsOneWidget);
  });
}
