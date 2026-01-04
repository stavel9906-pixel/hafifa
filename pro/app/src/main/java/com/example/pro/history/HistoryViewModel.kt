/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pro.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope
import com.example.pro.database.ScoreDatabaseDao
import com.example.pro.database.SumScore
import com.example.pro.formatScores

/**
 * ViewModel for SleepTrackerFragment.
 */
class HistoryViewModel(
    val database: ScoreDatabaseDao,
    application: Application) : AndroidViewModel(application) {



    private var tonight = MutableLiveData<SumScore?>()

    val scores = database.getAllScores()

    val scoresString = scores.map { scores ->
        formatScores(scores, application.resources)
    }

    /**
     * If tonight has not been set, then the START button should be visible.
     */
    val startButtonVisible = tonight.map {
        null == it
    }

    /**
     * If tonight has been set, then the STOP button should be visible.
     */
    val stopButtonVisible = tonight.map {
        null != it
    }

    /**
     * If there are any nights in the database, show the CLEAR button.
     */
    val clearButtonVisible = scores.map {
        it.isNotEmpty()
    }

    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackbar()`.
     */
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    /**
     * Variable that tells the Fragment to navigate to a specific [SleepQualityFragment]
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */

    private val _navigateToSleepQuality = MutableLiveData<SumScore?>()
    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the user rotates their phone it won't show a duplicate
     * toast.
     */

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * If this is non-null, immediately navigate to [SleepQualityFragment] and call [doneNavigating]
     */
    val navigateToSleepQuality: LiveData<SumScore?>
        get() = _navigateToSleepQuality

    /**
     * Call this immediately after navigating to [SleepQualityFragment]
     *
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    private val _navigateToSleepDataQuality = MutableLiveData<Long?>()
    val navigateToSleepDataQuality
        get() = _navigateToSleepDataQuality

    fun onSleepNightClicked(id: Long) {
        _navigateToSleepDataQuality.value = id
    }

    fun onSleepDataQualityNavigated() {
        _navigateToSleepDataQuality.value = null
    }

    init {

    }


    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.j
     *
     *  If the start time and end time are not the same, then we do not have an unfinished
     *  recording.
     */

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    private suspend fun update(night: SumScore) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    /**
     * Executes when the START button is clicked.
     */

    /**
     * Executes when the STOP button is clicked.
     */

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        viewModelScope.launch {
            // Clear the database table.
            clear()

            // And clear tonight since it's no longer in the database
            tonight.value = null
        }

        // Show a snackbar message, because it's friendly.
        _showSnackbarEvent.value = true
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.

    override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
    }
     */
}
