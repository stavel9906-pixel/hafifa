package com.example.pro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.pro.databinding.FragmentStartBinding

class TitleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStartBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_start, container, false
        )
        binding.playButton.setOnClickListener { view: View ->
            println("here")
            findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGamesFragment())
        }
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        setHasOptionsMenu(true)
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.overflow_menu, menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }
}