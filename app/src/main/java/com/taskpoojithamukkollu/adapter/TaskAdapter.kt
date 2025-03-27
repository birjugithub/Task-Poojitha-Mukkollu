package com.taskpoojithamukkollu.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taskpoojithamukkollu.R
import com.taskpoojithamukkollu.db.Task

class TaskAdapter(
    val context: Context,
    val taskList: MutableList<Task>,
    private val listener: onButtonClick
) : RecyclerView.Adapter<TaskAdapter.TaskViewModel>() {

    class TaskViewModel(item: View) : RecyclerView.ViewHolder(item) {

        val taskTitle: TextView = item.findViewById(R.id.taskTitle)
        val taskDescription: TextView = item.findViewById(R.id.taskDescription)
        val deleteButton: ImageView = item.findViewById(R.id.deleteButton)
        val editImage: ImageView = item.findViewById(R.id.editButton)
        val taskCheckBox: CheckBox = item.findViewById(R.id.taskCheckBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TaskViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewModel(view)
    }

    override fun onBindViewHolder(holder: TaskAdapter.TaskViewModel, position: Int) {
        Log.d("TAG", "onBindViewHolder: $taskList")
        val task = taskList[position]

        holder.taskTitle.text = task.title
        holder.taskDescription.text = task.description

        // Set the checkbox state
        holder.taskCheckBox.isChecked = task.isCompleted

        holder.deleteButton.setOnClickListener {
            listener.onDeleteCLick(task)
        }

        holder.editImage.setOnClickListener {
            listener.onUpdateClick(task)
        }

        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedTask = task.copy(isCompleted = isChecked)
            listener.onTaskComplete(updatedTask)
        }

    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}

interface onButtonClick {
    fun onDeleteCLick(task: Task)
    fun onUpdateClick(task: Task)
    fun onTaskComplete(task: Task)
}