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
 *
 */

package com.example.android.marsrealestate.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.network.MarsApiFilter
import com.google.android.material.chip.Chip

/**
 * This fragment shows the the status of the Mars real-estate web services transaction.
 */
class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by viewModels(factoryProducer = {
        OverviewViewModel.Factory(requireActivity().application)
    })


    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        binding.photosGrid.adapter = PhotoGridAdapter(object : PhotoGridAdapter.OnClickListener {
            override fun onClick(propertyId: String) {
                viewModel.onPropertyClicked(propertyId)

            }

        })


        binding.swipeToRefreshLayout.setOnRefreshListener {
            viewModel.refreshProperties()
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            binding.swipeToRefreshLayout.isRefreshing = it == MarsApiStatus.LOADING
        })

        viewModel.shouldNavigateToPropertyDetails.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                val action = OverviewFragmentDirections.actionShowDetail(it)
                findNavController().navigate(action)
                viewModel.doneNavigateToPropertyDetails()
            }
        })


        binding.overviewToolbar.setOnMenuItemClickListener {
            onMenuItemIdClickListener(it.itemId)
        }

        viewModel.propertiesFilterStatus.observe(viewLifecycleOwner, Observer {
            binding.chipGroup.check(mappedItemIdForApiFilter(it))
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chipGroup = binding.chipGroup
        chipGroup.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        val menu = PopupMenu(requireContext(), requireView()).menu
        MenuInflater(requireContext()).inflate(R.menu.overflow_menu, menu)

        val menuIterator = menu.iterator()
        while (menuIterator.hasNext()) {
            val nextItem = menuIterator.next()
            val chip = inflater.inflate(R.layout.filter, chipGroup, false) as? Chip
            chip?.apply {
                text = nextItem.title
                id = nextItem.itemId
                setOnClickListener {
                    onMenuItemIdClickListener(id)
                }
            }
            chipGroup.addView(chip)
        }
    }

    @IdRes
    private fun mappedItemIdForApiFilter(filter: MarsApiFilter): Int {
        return when (filter) {
            MarsApiFilter.FOR_RENT_PROPERTIES -> R.id.show_rent_menu
            MarsApiFilter.FOR_BUY_PROPERTIES -> R.id.show_buy_menu
            MarsApiFilter.ALL_PROPERTIES -> R.id.show_all_menu
        }
    }

    private fun onMenuItemIdClickListener(itemId: Int): Boolean {
        return when (itemId) {
            R.id.show_all_menu -> {
                viewModel.refreshProperties(MarsApiFilter.ALL_PROPERTIES)
                true
            }

            R.id.show_buy_menu -> {
                viewModel.refreshProperties(MarsApiFilter.FOR_BUY_PROPERTIES)
                true
            }

            R.id.show_rent_menu -> {
                viewModel.refreshProperties(MarsApiFilter.FOR_RENT_PROPERTIES)
                true
            }
            else -> false
        }
    }
}
