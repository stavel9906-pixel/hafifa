package com.example.pro.game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pro.R
import com.example.pro.database.ScoreDatabase
import com.example.pro.databinding.FragmentGamesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GamesFragment : Fragment() {
    data class Question(
        val text: String,
        val answers: List<String>)

    private lateinit var viewModel: GamesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGamesBinding>(
            inflater, R.layout.fragment_games, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = ScoreDatabase.getInstance(application).scoreDatabaseDao

        val viewModelFactory = GameViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(GamesViewModel::class.java)

        // Bind this fragment class to the layout
        binding.gamesViewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.onSetQuestion.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished){
                (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question,
                    viewModel.questionIndex.value?.plus(1), viewModel.numQuestions)
                viewModel.onSetQuestionComplete()
            }
        })

        viewModel.right.observe(viewLifecycleOwner, Observer { right ->
            if (right != null){
                when (right) {
                    true -> binding.root.background = resources.getDrawable(R.color.green)
                    false -> binding.root.background = resources.getDrawable(R.color.red_700)
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    delay(100)

                    binding.root.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                }

                viewModel.nextQuestionBackground()
            }
        })

        // Set the onClickListener for the submitButton
        binding.nextButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId = binding.optionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.option2RadioButton -> answerIndex = 1
                    R.id.option3RadioButton -> answerIndex = 2
                    R.id.option4RadioButton -> answerIndex = 3
                }
                viewModel.onNextQuestion(answerIndex)

                if (viewModel.numQuestions == viewModel.questionIndex.value?.plus(1)) {
                    binding.nextButton.text = "Submit"
                } else if (viewModel.questionIndex.value == viewModel.numQuestions) {
                    gameFinished()
                }
                binding.optionRadioGroup.clearCheck()
            }
        }
        return binding.root
    }

    private fun gameFinished() {
        val action = GamesFragmentDirections.actionGamesFragmentToScoreFragment(viewModel.score.value ?: 0)
        findNavController().navigate(action)
        viewModel.onGameFinished()
    }
}
