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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pro.R
import com.example.pro.database.ScoreDatabase
import com.example.pro.databinding.FragmentHistoryBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class HistoryFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentHistoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_history, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ScoreDatabase.getInstance(application).scoreDatabaseDao

        val viewModelFactory = HistoryViewModelFactory(dataSource, application)

        val historyViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(HistoryViewModel::class.java)

        binding.historyViewModel = historyViewModel

        val manager = GridLayoutManager(activity, 1)
//        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() { // גורם לזה לא לתפוס מקום כאחד האיברים אלא ככותרת
//            override fun getSpanSize(position: Int): Int {
//                return when (position) {
//                    0 -> 3
//                    else -> 1
//                }
//            }
//
//        }
        binding.historyList.layoutManager = manager


        val adapter = ScoreAdapter()

        binding.historyList.adapter = adapter
        historyViewModel.scores.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        binding.lifecycleOwner = this


        return binding.root
    }
}
