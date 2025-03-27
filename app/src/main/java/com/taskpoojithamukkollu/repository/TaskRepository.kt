package com.taskpoojithamukkollu.repository

import android.content.Context
import com.taskpoojithamukkollu.db.Task
import com.taskpoojithamukkollu.db.TaskDao
import com.taskpoojithamukkollu.db.TaskDatabase
import kotlinx.coroutines.flow.Flow

class TaskRepository(context: Context) {

    private val taskDao: TaskDao

    init {
        val database = TaskDatabase.getDatabase(context)
        taskDao = database.taskDao()
    }

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}