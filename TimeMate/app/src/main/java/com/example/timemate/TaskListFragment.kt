package com.example.timemate

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class TaskListFragment : Fragment() {
    private var tasks: ArrayList<String>? = null
    private var adapter: ArrayAdapter<String>? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        val taskListView = view.findViewById<ListView>(R.id.task_list_view)
        val addTaskButton = view.findViewById<Button>(R.id.add_task_button)

        sharedPreferences = requireActivity().getSharedPreferences("tasks", Context.MODE_PRIVATE)
        tasks = loadTasksFromSharedPreferences()
        adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_list_item_1,
            tasks!!
        )

        taskListView.adapter = adapter

        addTaskButton.setOnClickListener { showAddTaskDialog() }

        taskListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> showEditTaskDialog(position) }

        return view
    }

    private fun showAddTaskDialog() {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_task, null)
        val input = dialogView.findViewById<EditText>(R.id.edit_text_task)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val task = input.text.toString().trim()
                if (task.isNotEmpty()) {
                    tasks!!.add(task)
                    adapter!!.notifyDataSetChanged()
                    saveTasksToSharedPreferences()
                } else {
                    Toast.makeText(activity, "Task cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showEditTaskDialog(position: Int) {
        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_task, null)
        val input = dialogView.findViewById<EditText>(R.id.edit_text_task)
        input.setText(tasks!![position])

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val task = input.text.toString().trim()
                if (task.isNotEmpty()) {
                    tasks!![position] = task
                    adapter!!.notifyDataSetChanged()
                    saveTasksToSharedPreferences()
                } else {
                    Toast.makeText(activity, "Task cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Delete") { _, _ ->
                tasks!!.removeAt(position)
                adapter!!.notifyDataSetChanged()
                saveTasksToSharedPreferences()
            }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun saveTasksToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("task_keys", tasks?.toSet())
        editor.apply()
    }

    private fun loadTasksFromSharedPreferences(): ArrayList<String> {
        val savedTasks = sharedPreferences.getStringSet("task_keys", emptySet())
        return ArrayList(savedTasks ?: emptySet())
    }
}
