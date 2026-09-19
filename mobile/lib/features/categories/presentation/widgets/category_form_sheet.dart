import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:reactive_forms/reactive_forms.dart';

import '../../../../core/utils/color_utils.dart';
import '../../../../core/utils/error_utils.dart';
import '../../../tasks/domain/providers/tasks_provider.dart';
import '../../data/repositories/category_repository.dart';
import '../../domain/models/category.dart';
import '../../domain/providers/categories_provider.dart';

const List<String> _presetColors = [
  '#6b7280',
  '#ef4444',
  '#f97316',
  '#f59e0b',
  '#10b981',
  '#06b6d4',
  '#3b82f6',
  '#6366f1',
  '#8b5cf6',
  '#ec4899',
];

class CategoryFormSheet extends ConsumerStatefulWidget {
  const CategoryFormSheet({super.key, this.category});

  final Category? category;

  @override
  ConsumerState<CategoryFormSheet> createState() => _CategoryFormSheetState();
}

class _CategoryFormSheetState extends ConsumerState<CategoryFormSheet> {
  late final FormGroup form;
  late String _color;
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    final category = widget.category;
    form = FormGroup({
      'name': FormControl<String>(
        value: category?.name,
        validators: [Validators.required],
      ),
      'description': FormControl<String>(value: category?.description),
    });
    _color = category?.color ?? _presetColors[7];
  }

  @override
  void dispose() {
    form.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!form.valid) {
      form.markAllAsTouched();
      return;
    }

    setState(() => _submitting = true);
    try {
      final description = form.control('description').value as String?;
      final request = CategoryRequest(
        name: form.control('name').value as String,
        description: (description == null || description.isEmpty)
            ? null
            : description,
        color: _color,
      );

      final repo = ref.read(categoryRepositoryProvider);
      final category = widget.category;
      if (category != null) {
        await repo.updateCategory(category.publicId, request);
      } else {
        await repo.createCategory(request);
      }

      ref.invalidate(categoriesProvider);
      ref.invalidate(tasksProvider);

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
              widget.category != null ? 'Edit Category' : 'New Category',
              style: Theme.of(context).textTheme.titleLarge
                  ?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            ReactiveForm(
              formGroup: form,
              child: Column(
                children: [
                  ReactiveTextField(
                    formControlName: 'name',
                    decoration: const InputDecoration(labelText: 'Name *'),
                    validationMessages: {
                      ValidationMessage.required: (_) => 'Name is required',
                    },
                  ),
                  const SizedBox(height: 16),
                  ReactiveTextField(
                    formControlName: 'description',
                    maxLines: 2,
                    decoration: const InputDecoration(labelText: 'Description'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),
            Text('Color', style: Theme.of(context).textTheme.titleSmall),
            const SizedBox(height: 8),
            Wrap(
              spacing: 10,
              runSpacing: 10,
              children: [
                for (final hex in _presetColors)
                  GestureDetector(
                    onTap: () => setState(() => _color = hex),
                    child: Container(
                      width: 40,
                      height: 40,
                      decoration: BoxDecoration(
                        color: colorFromHex(hex),
                        shape: BoxShape.circle,
                        border: _color == hex
                            ? Border.all(
                                color: Theme.of(context).colorScheme.onSurface,
                                width: 3,
                              )
                            : null,
                      ),
                      child: _color == hex
                          ? const Icon(
                              Icons.check,
                              color: Colors.white,
                              size: 20,
                            )
                          : null,
                    ),
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
                  : Text(widget.category != null ? 'Update' : 'Create'),
            ),
          ],
        ),
      ),
    );
  }
}
