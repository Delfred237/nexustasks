package com.nexustasks.task.entity;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    ARCHIVED // Note : Ceci est un statut "terminal" de workflow, différent du booléen 'archived'
}