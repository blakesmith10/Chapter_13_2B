package com.bignerdranch.andriod.criminalintent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.andriod.criminalintent.databinding.FragmentCrimeDetailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// private const val TAG = "CrimeDetailFragment"


class CrimeDetailFragment : Fragment() {

    // private lateinit var crime: Crime
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    // private lateinit var binding:FragmentCrimeDetailBinding
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false

        )

        Log.d(TAG, "The crime ID is: ${args.crimeId}")

    }*/

    private var currentCrimeTitle: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                // crime = crime.copy(title = text.toString())
                currentCrimeTitle = text.toString()
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeDate.apply {
                // text = crime.date.toString()
                isEnabled = false
            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                // crime = crime.copy(isSolved = isChecked)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let {
                        currentCrimeTitle = it.title
                        updateUI(it)
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (currentCrimeTitle.isBlank()) {
                        Snackbar.make(view, "Please provide a description of the crime",
                            Snackbar.LENGTH_SHORT).show()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        )
    }

    private fun updateUI(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeSolved.isChecked = crime.isSolved
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



