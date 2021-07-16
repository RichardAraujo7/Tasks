package com.example.tasks.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.TaskModel
import com.example.tasks.viewmodel.TaskFormViewModel
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    View.OnClickListener {

    private lateinit var mViewModel: TaskFormViewModel
    private val mPriorityListId: MutableList<Int> = arrayListOf()
    private val mDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private var mTaskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        mViewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)

        listeners()

        observe()

        loadDataFromActivity()
        mViewModel.loadPriorities()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val strDate: String = mDateFormat.format(calendar.time)
        button_date.text = strDate
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_date) {
            showDateDialog()
        } else if (id == R.id.button_save) {
            handleSave()
        }
    }

    private fun listeners() {
        button_date.setOnClickListener(this)
        button_save.setOnClickListener(this)
    }

    private fun observe() {
        mViewModel.task.observe(this, Observer {
            if (it == null) {
                finish()
            } else {
                edit_description.setText(it.description)
                check_complete.isChecked = it.complete
                spinner_priority.setSelection(getIndex(it.priorityId))

                val date = SimpleDateFormat("yyyy-MM-dd").parse(it.dueDate)
                button_date.text = mDateFormat.format(date)
            }
        })

        mViewModel.priorityList.observe(this, Observer {
            val list: MutableList<String> = ArrayList()
            for (p in it) {
                list.add(p.description)
                mPriorityListId.add(p.id)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
            spinner_priority.adapter = adapter
        })

        mViewModel.validation.observe(this, Observer {
            if (it.success()) {
                if (mTaskId == 0) {
                     toast(applicationContext.getString(R.string.task_created))
                } else {
                    toast(applicationContext.getString(R.string.task_updated))
                }
                finish()
            } else {
                toast(it.failure())
            }
        })
    }

    private fun getIndex(priorityId: Int): Int {
        var index = 0
        for (i in 0 until mPriorityListId.count()) {
            if (mPriorityListId[i] == priorityId) {
                index = i
                break
            }
        }
        return index
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras;
        if (bundle != null) {
            mTaskId = bundle.getInt(TaskConstants.BUNDLE.TASKID, 0)

            // Carrega tarefa
            if (this.mTaskId != 0) {
                button_save.setText(R.string.update_task)
                mViewModel.load(mTaskId)
            }
        }
    }

    private fun handleSave() {
        val task = TaskModel().apply {
            this.id = mTaskId
            this.description = edit_description.text.toString()
            this.complete = check_complete.isChecked
            this.dueDate = button_date.text.toString()
            this.priorityId = mPriorityListId[spinner_priority.selectedItemPosition]
        }

        // Envia informação para ViewModel
        mViewModel.save(task)
    }

    private fun showDateDialog() {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun toast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

}
