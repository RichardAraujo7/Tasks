package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.repository.local.TaskDatabase
import com.example.tasks.service.repository.remote.PriorityService
import com.example.tasks.service.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(context: Context) : BaseRepository(context) {

    private val mRemote = RetrofitClient.createService(PriorityService::class.java)
    private val mDatabase = TaskDatabase.getDatabase(context).priorityDAO()

    fun all(listener: APIListener<List<PriorityModel>>) {

        if (!isConnectionAvailable(mContext)) {
            listener.onFailure("Dispositivo sem conexão. Conecte-se com à internet.")
            return
        }

        val call: Call<List<PriorityModel>> = mRemote.all()
        call.enqueue(object : Callback<List<PriorityModel>> {
            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {
                listener.onFailure(mContext.getString(R.string.ERROR_UNEXPECTED))
            }

            override fun onResponse(
                call: Call<List<PriorityModel>>,
                response: Response<List<PriorityModel>>
            ) {
                val code = response.code()
                if (fail(code)) {
                    listener.onFailure(failRespose(response.errorBody()!!.string()))
                } else {
                    response.body()?.let { listener.onSuccess(it, response.code()) }
                }
            }

        })
    }

    fun list() = mDatabase.list()

    fun save(list: List<PriorityModel>) {
        mDatabase.clear()
        mDatabase.save(list)
    }

    fun getDescription(id: Int) = mDatabase.getDescription(id)

}