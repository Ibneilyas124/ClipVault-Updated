package com.sarfrazqureshi.clipvault.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sarfrazqureshi.clipvault.db.ClipDatabase
import com.sarfrazqureshi.clipvault.db.ClipItem
import com.sarfrazqureshi.clipvault.db.ClipType

class ClipListViewModel(application: Application, type: ClipType) : AndroidViewModel(application) {

    val items: LiveData<List<ClipItem>> =
        ClipDatabase.getInstance(application).clipDao().getByType(type)

    class Factory(private val app: Application, private val type: ClipType) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClipListViewModel(app, type) as T
        }
    }
}
