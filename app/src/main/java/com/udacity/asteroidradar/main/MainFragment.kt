package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.detail.DetailFragmentArgs

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }

        ViewModelProvider(this, MainViewModel.Factory(activity.application))
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            viewModel.setAsteroidDetailsToNavigate(it)
        })

        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, Observer { asteroid ->
            if(asteroid != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.navigationToAsteroidDetailsDone()
            }
        })

        viewModel.networkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if(isNetworkError){
                Toast.makeText(activity, "A network error occurred, please check your connection status.", Toast.LENGTH_LONG).show()
                viewModel.networkErrorShown()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.week_asteroids ->
                viewModel.showWeekAsteroids()
            R.id.today_asteroids ->
                viewModel.showTodayAsteroids()
            R.id.saved_asteroids ->
                viewModel.showSavedAsteroids()
        }

        return false
    }

}
