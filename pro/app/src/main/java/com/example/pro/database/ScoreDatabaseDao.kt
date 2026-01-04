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

package com.example.pro.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Defines methods for using the SleepNight class with Room.
 */
@Dao
interface ScoreDatabaseDao {

    @Insert
    suspend fun insert(score: SumScore)
    @Update
    suspend fun update(score: SumScore)
    @Query("SELECT * from score_sum_table WHERE scoreId = :key")
    suspend fun get(key: Long): SumScore?
    @Query("DELETE FROM score_sum_table")
    suspend fun clear()
    @Query("SELECT * FROM score_sum_table ORDER BY scoreId DESC")
    fun getAllScores(): LiveData<List<SumScore>>
//    @Query("SELECT * FROM score_sum_table ORDER BY scoreId DESC LIMIT 1")
//    suspend fun getTonight(): SleepNight?
//    @Query("SELECT * from score_sum_table WHERE nightId = :key")
//    fun getNightWithId(key: Long): LiveData<SleepNight>
}

