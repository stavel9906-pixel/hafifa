package com.example.pro.game

import android.app.Application
import androidx.lifecycle.ViewModel
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pro.R
import com.example.pro.database.ScoreDatabaseDao
import com.example.pro.database.SumScore
import com.example.pro.game.GamesFragment.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.get
import kotlin.collections.shuffle
import kotlin.dec
import kotlin.inc
import kotlin.let
import kotlin.text.compareTo

private val questions: MutableList<Question> = mutableListOf(
    Question(text = "What is Android Jetpack?",
        answers = listOf("all of these", "tools", "documentation", "libraries")),
    Question(text = "Base class for Layout?",
        answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot")),
    Question(text = "Layout for complex Screens?",
        answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout")),
    Question(text = "Pushing structured data into a Layout?",
        answers = listOf("Data Binding", "Data Pushing", "Set Text", "OnClick")),
    Question(text = "Inflate layout in fragments?",
        answers = listOf("onCreateView", "onViewCreated", "onCreateLayout", "onInflateLayout")),
    Question(text = "Build system for Android?",
        answers = listOf("Gradle", "Graddle", "Grodle", "Groyle")),
    Question(text = "Android vector format?",
        answers = listOf("VectorDrawable", "AndroidVectorDrawable", "DrawableVector", "AndroidVector")),
    Question(text = "Android Navigation Component?",
        answers = listOf("NavController", "NavCentral", "NavMaster", "NavSwitcher")),
    Question(text = "Registers app with launcher?",
        answers = listOf("intent-filter", "app-registry", "launcher-registry", "app-launcher")),
    Question(text = "Mark a layout for Data Binding?",
        answers = listOf("<layout>", "<binding>", "<data-binding>", "<dbinding>"))
)
class GamesViewModel(val database: ScoreDatabaseDao,
                     application: Application): ViewModel() {
    // The current word
    private val _questions = questions // משתמש ברשימה שהגדרת בחוץ

    // LiveData לנתוני המשחק
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val _questionIndex = MutableLiveData(0)
    val questionIndex: LiveData<Int> get() = _questionIndex

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> get() = _currentQuestion

    private val _answers = MutableLiveData<List<String>>()
    val answers: LiveData<List<String>> get() = _answers

    private val _onSetQuestion = MutableLiveData<Boolean>()
    val onSetQuestion: LiveData<Boolean> get() = _onSetQuestion

    val numQuestions = Math.min((_questions.size + 1) / 2, 5)
    private val _right = MutableLiveData<Boolean?>()
    val right: LiveData<Boolean?> get() = _right


    init {
        Log.i("GameViewModel", "GameViewModel created!")
        _onSetQuestion.value = false
        _right.value = null
        randomizeQuestions()
    }

    private fun setQuestion() {
        val index = _questionIndex.value ?: 0
        // randomize the answers into a copy of the array
        val question = _questions[index]
        _currentQuestion.value = question

        // ערבוב תשובות
        val shuffledAnswers = question.answers.toMutableList()
        shuffledAnswers.shuffle()
        _answers.value = shuffledAnswers
        _onSetQuestion.value = true
    }

    private suspend fun insert(score: SumScore) {
        withContext(Dispatchers.IO) {
            database.insert(score)
        }
    }

    fun onGameFinished() {
        viewModelScope.launch {
            val newScore = SumScore()
            newScore.totalScore = score.value ?: 0

            insert(newScore)
        }
    }

    fun onMistake() {
        _score.value = (score.value)?.minus(1)
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
    }

    fun onNextQuestion(answerIndex: Int) {
        // The first answer in the original question is always the correct one, so if our
        // answer matches, we have the correct answer.
        if (_answers.value?.get(answerIndex) == _currentQuestion.value?.answers?.get(0)) {
            println("correct")
            onCorrect()
            _right.value = true
        } else {
            println("mistake")
            onMistake()
            _right.value = false
        }

        val nextIndex = (_questionIndex.value ?: 0) + 1
        // Advance to the next question
        if (nextIndex < numQuestions) {
            _questionIndex.value = nextIndex
            setQuestion()
        } else {
            _questionIndex.value = nextIndex
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }

    fun onSetQuestionComplete() {
        _onSetQuestion.value = false
    }

    fun nextQuestionBackground() {
        _right.value = null
    }

    private fun randomizeQuestions() {
        questions.shuffle()
        _questionIndex.value = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.

}
