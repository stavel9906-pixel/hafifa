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

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.pro.convertLongToDateString
import com.example.pro.convertScoreToString
import com.example.pro.database.SumScore

@BindingAdapter("dateFormatted")
fun TextView.setDateFormatted(item: SumScore?) {
    item?.let {
        text = convertLongToDateString(it.dateGame)
    }
}

@BindingAdapter("formattedScore")
fun TextView.setFormattedScore(item: SumScore?) {
    item?.let {
        text = convertScoreToString(resources, it.totalScore)
    }
}
