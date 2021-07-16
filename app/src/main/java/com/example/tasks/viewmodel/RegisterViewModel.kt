package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.local.SecurityPreferences

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val mCreate = MutableLiveData<ValidationListener>()
    val create: LiveData<ValidationListener> = mCreate

    private val mSecurityPreferences = SecurityPreferences(application)
    private val mRepository = PersonRepository(application)

    fun create(name: String, email: String, password: String) {
        mRepository.create(name, email, password, object : APIListener<HeaderModel>{
            override fun onSuccess(result: HeaderModel, statusCode: Int) {

                // Salvar dados do usuário no SharePreferences
                mSecurityPreferences.store(TaskConstants.SHARED.PERSON_KEY, result.personKey)
                mSecurityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, result.token)
                mSecurityPreferences.store(TaskConstants.SHARED.PERSON_NAME, result.name)

                mCreate.value = ValidationListener()
            }

            override fun onFailure(message: String) {
                mCreate.value = ValidationListener(message)
            }

        })
    }

}