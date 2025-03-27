package com.taskpoojithamukkollu

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.taskpoojithamukkollu.adapter.TaskAdapter
import com.taskpoojithamukkollu.adapter.onButtonClick
import com.taskpoojithamukkollu.db.Task
import com.taskpoojithamukkollu.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), onButtonClick {

    private lateinit var adapter: TaskAdapter
    private val taskViewModel: TaskViewModel by viewModels() // Using ViewModel without DI
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        val addTask: FloatingActionButton = findViewById(R.id.normalFAB)
        analytics = FirebaseAnalytics.getInstance(this)
        addTask.setOnClickListener {
            showAddTaskDialog()
        }
        lifecycleScope.launch {
            taskViewModel.allTasks.collect { taskList ->
                adapter =
                    TaskAdapter(this@MainActivity, taskList.toMutableList(), this@MainActivity)
                recyclerView.layoutManager =
                    LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = adapter

            }
        }
    }

    private fun showAddTaskDialog(task: Task? = null) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_task)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(), // 95% of screen width
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val taskTitle = dialog.findViewById<EditText>(R.id.editTextTitle)
        val taskDescription = dialog.findViewById<EditText>(R.id.editTextDescription)
        val saveTaskButton = dialog.findViewById<Button>(R.id.saveTaskButton)
        val crossImage=dialog.findViewById<ImageView>(R.id.imageCross)

        if (task != null) {
            taskTitle.setText(task.title)
            taskDescription.setText(task.description)
            saveTaskButton.text = "Update Task"
        }

        crossImage.setOnClickListener {
           dialog.dismiss()
        }
        saveTaskButton.setOnClickListener {
            val taskName = taskTitle.text.toString().trim()
            val taskDesc = taskDescription.text.toString().trim()

            if (taskName.isNotEmpty() && taskDesc.isNotEmpty()) {
                if (task == null) {

                    val bundle = Bundle().apply {
                        putString("task_name", taskName)
                        putString("task_desc", taskDesc)

                    }
                    analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                        param(FirebaseAnalytics.Param.SCREEN_NAME,taskName)
                    }

                    taskViewModel.addTask(Task(title = taskName, description = taskDesc))
                    Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
                } else {
                    val bundle = Bundle().apply {
                        putString("task_title_update", taskName)
                        putString("task_desc_update", taskDesc)
                    }
                    analytics.logEvent("task_update", bundle)

                    val updatedTask = task.copy(title = taskName, description = taskDesc)
                    taskViewModel.updateTask(updatedTask)
                    Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            } else {
                if (taskName.isEmpty()) taskTitle.error = "Task name required"
                if (taskDesc.isEmpty()) taskDescription.error = "Task description required"
            }
        }

        dialog.show()
    }


    override fun onDeleteCLick(task: Task) {
        taskViewModel.deleteTask(task)
    }

    override fun onUpdateClick(task: Task) {
        showAddTaskDialog(task)
    }

    override fun onTaskComplete(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to mark this task as completed?")
            .setTitle("Confirm Completion")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                Toast.makeText(this, "Task marked as completed!", Toast.LENGTH_SHORT).show()

                val updatedTask = task.copy(isCompleted = true)
                taskViewModel.updateTask(updatedTask)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                val updatedTask = task.copy(isCompleted = false)
                taskViewModel.updateTask(updatedTask)
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}