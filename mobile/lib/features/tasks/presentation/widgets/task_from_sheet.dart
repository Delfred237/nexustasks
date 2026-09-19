import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:reactive_forms/reactive_forms.dart';

import '../../../../core/utils/error_utils.dart';
import '../../../categories/domain/providers/categories_provider.dart';
import '../../data/repositories/task_repository.dart';
import '../../domain/models/task.dart';
import '../../domain/providers/tasks_provider.dart';

class TaskFormSheet extends ConsumerStatefulWidget {
  const TaskFormSheet({super.key, this.task});

  final Task? task;

  @override
  ConsumerState<TaskFormSheet> createState() => _TaskFormSheetState();
}

class _TaskFormSheetState extends ConsumerState<TaskFormSheet> {
  late final FormGroup form;
  late String _status;
  late String _priority;
  late String _categoryPublicId;
  DateTime? _dueDate;
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    final task = widget.task;
    form = FormGroup({
      'title': FormControl<String>(
        value: task?.title,
        validators: [Validators.required],
      ),
      'description': FormControl<String>(value: task?.description),
    });
    _status = task?.status ?? 'TODO';
    _priority = task?.priority ?? 'MEDIUM';
    _categoryPublicId = task?.category?.publicId ?? '';
    _dueDate = task?.dueDate;
  }

  @override
  void dispose() {
    form.dispose();
    super.dispose();
  }

  Future<void> _pickDueDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: _dueDate ?? now,
      firstDate: DateTime(2020),
      lastDate: DateTime(2035),
    );
    if (picked != null && mounted) {
      setState(() => _dueDate = picked);
    }
  }

  Future<void> _submit() async {
    if (!form.valid) {
      form.markAllAsTouched();
      return;
    }

    setState(() => _submitting = true);
    try {
      final description = form.control('description').value as String?;
      final request = CreateTaskRequest(
        title: form.control('title').value as String,
        description: (description == null || description.isEmpty)
            ? null
            : description,
        status: _status,
        priority: _priority,
        dueDate: _dueDate,
        categoryPublicId: _categoryPublicId.isEmpty ? null : _categoryPublicId,
      );

      final notifier = ref.read(tasksProvider.notifier);
      final task = widget.task;
      if (task != null) {
        await notifier.updateTask(task.publicId, request);
      } else {
        await notifier.createTask(request);
      }

      if (mounted) Navigator.of(context).pop();
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text(describeError(error))));
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final categoriesAsync = ref.watch(categoriesProvider);
    final categories = categoriesAsync.value ?? const [];

    // Garde-fou : la valeur du dropdown doit exister dans les items
    final categoryExists = categories.any(
      (c) => c.publicId == _categoryPublicId,
    );
    final categoryValue = categoryExists ? _categoryPublicId : '';

    return Padding(
      padding: EdgeInsets.fromLTRB(
        24,
        16,
        24,
        MediaQuery.of(context).viewInsets.bottom + 24,
      ),
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              widget.task != null ? 'Edit Task' : 'New Task',
              style: Theme.of(context).textTheme.titleLarge
                  ?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            ReactiveForm(
              formGroup: form,
              child: Column(
                children: [
                  ReactiveTextField(
                    formControlName: 'title',
                    textInputAction: TextInputAction.next,
                    decoration: const InputDecoration(labelText: 'Title *'),
                    validationMessages: {
                      ValidationMessage.required: (_) => 'Title is required',
                    },
                  ),
                  const SizedBox(height: 16),
                  ReactiveTextField(
                    formControlName: 'description',
                    maxLines: 3,
                    decoration: const InputDecoration(labelText: 'Description'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              initialValue: _status,
              decoration: const InputDecoration(labelText: 'Status'),
              items: const [
                DropdownMenuItem(value: 'TODO', child: Text('To Do')),
                DropdownMenuItem(
                  value: 'IN_PROGRESS',
                  child: Text('In Progress'),
                ),
                DropdownMenuItem(value: 'COMPLETED', child: Text('Completed')),
              ],
              onChanged: (value) => setState(() => _status = value ?? _status),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              initialValue: _priority,
              decoration: const InputDecoration(labelText: 'Priority'),
              items: const [
                DropdownMenuItem(value: 'LOW', child: Text('Low')),
                DropdownMenuItem(value: 'MEDIUM', child: Text('Medium')),
                DropdownMenuItem(value: 'HIGH', child: Text('High')),
                DropdownMenuItem(value: 'URGENT', child: Text('Urgent')),
              ],
              onChanged: (value) =>
                  setState(() => _priority = value ?? _priority),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              initialValue: categoryValue,
              decoration: const InputDecoration(labelText: 'Category'),
              items: [
                const DropdownMenuItem(value: '', child: Text('No category')),
                ...categories.map(
                  (c) =>
                      DropdownMenuItem(value: c.publicId, child: Text(c.name)),
                ),
              ],
              onChanged: (value) =>
                  setState(() => _categoryPublicId = value ?? ''),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _pickDueDate,
                    icon: const Icon(Icons.event_outlined, size: 18),
                    label: Text(
                      _dueDate == null
                          ? 'Set due date'
                          : DateFormat('MMM d, yyyy').format(_dueDate!),
                    ),
                  ),
                ),
                if (_dueDate != null)
                  IconButton(
                    onPressed: () => setState(() => _dueDate = null),
                    icon: const Icon(Icons.close),
                    tooltip: 'Clear due date',
                  ),
              ],
            ),
            const SizedBox(height: 24),
            FilledButton(
              onPressed: _submitting ? null : _submit,
              child: _submitting
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : Text(widget.task != null ? 'Update Task' : 'Create Task'),
            ),
          ],
        ),
      ),
    );
  }
}
