package com.example.tasks.service.listener

import com.example.tasks.service.model.HeaderModel

interface APIListener<T> {
    fun onSuccess(result: T, statusCode: Int)

    fun onFailure(message: String)
}