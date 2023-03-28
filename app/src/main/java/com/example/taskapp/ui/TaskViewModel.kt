package com.example.taskapp.ui

import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.model.Status
import com.example.taskapp.model.Task
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.StateView
import com.example.taskapp.util.showBottomSheet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TaskViewModel : ViewModel() {

    private val _taskList = MutableLiveData<StateView<List<Task>>>()
    val taskList: LiveData<StateView<List<Task>>> = _taskList

    private val _insertTask = MutableLiveData<StateView<Task>>()
    val insertTask: LiveData<StateView<Task>> = _insertTask

    private val _deleteTask = MutableLiveData<StateView<Task>>()
    val deleteTask: LiveData<StateView<Task>> = _deleteTask

    private val _updateTask = MutableLiveData<StateView<Task>>()
    val updateTask: LiveData<StateView<Task>> = _updateTask

    fun getTasks() {
        try {
            _taskList.postValue(StateView.OnLoading())

            FirebaseHelper.getDatabase().reference
                .child("tasks")
                .child(FirebaseHelper.getUserId())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val taskList = mutableListOf<Task>()
                        for (ds in snapshot.children) {
                            val task = ds.getValue(Task::class.java) as Task
                            taskList.add(task)
                        }

                        taskList.reverse()
                        _taskList.postValue(StateView.OnSucess(taskList))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("INFOTESTE", "onCancelled:")
                    }
                })
        } catch (ex: Exception) {
            _taskList.postValue(StateView.OnError(ex.message.toString()))
        }
    }

    fun insertTask(task: Task) {
        try {
            _insertTask.postValue(StateView.OnLoading())
            FirebaseHelper.getDatabase().reference
                .child("tasks")
                .child(FirebaseHelper.getAuth().currentUser?.uid ?: "")
                .child(task.id)
                .setValue(task).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        _insertTask.postValue(StateView.OnSucess(task))
                    }
                }
        } catch (ex: Exception) {
            _insertTask.postValue(StateView.OnError(ex.message))
        }
    }

    fun deleteTask(task: Task) {

        _deleteTask.postValue(StateView.OnLoading())

        try {
            FirebaseHelper.getDatabase().reference
                .child("tasks")
                .child(FirebaseHelper.getUserId())
                .child(task.id)
                .removeValue().addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        _deleteTask.postValue(StateView.OnSucess(task))
                    }
                }
        }catch (ex: Exception) {
            _deleteTask.postValue(StateView.OnError(ex.message))
        }
    }

    fun updateTask(task: Task) {

        _updateTask.postValue(StateView.OnLoading())

        try {
            val map = mapOf(
                "description" to task.description,
                "status" to task.status
            )
            FirebaseHelper.getDatabase().reference
                .child("tasks")
                .child(FirebaseHelper.getAuth().currentUser?.uid ?: "")
                .child(task.id)
                .updateChildren(map).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        _updateTask.postValue(StateView.OnSucess(task))
                    }
                }
        } catch (ex: Exception) {
            _updateTask.postValue(StateView.OnError(ex.message))
        }

    }
}