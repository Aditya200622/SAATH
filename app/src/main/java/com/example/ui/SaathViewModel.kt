package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.*
import com.example.data.local.SaathDatabase
import com.example.data.model.*
import com.example.data.repository.SaathRepository
import com.example.wearable.WearableService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val userProfile: UserProfile = UserProfile(),
    val saathiConfig: SaathiConfig = SaathiConfig(),
    val checkIns: List<CheckIn> = emptyList(),
    val latestCheckIn: CheckIn? = null,
    val tasks: List<Task> = emptyList(),
    val focusSessions: List<FocusSession> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val contacts: List<TrustedContact> = emptyList(),
    val supportRequests: List<SupportRequest> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val wearableMetrics: WearableMetrics = WearableMetrics(),
    val isWearableConnected: Boolean = true,
    val isAiThinking: Boolean = false,
    val smartNudgeText: String = "“Progress, not perfection. Keep going, you're doing great!”",
    // Focus Timer State
    val focusTimerSecondsLeft: Int = 25 * 60,
    val focusTimerTotalSeconds: Int = 25 * 60,
    val isFocusTimerRunning: Boolean = false,
    val currentFocusTaskTitle: String = "Assignment",
    // Reset Exercise State
    val currentExerciseTitle: String = "4-7-8 Deep Breathing",
    val exercisePhase: String = "Inhale", // Inhale, Hold, Exhale
    val exerciseSecondsLeft: Int = 4,
    val isExerciseRunning: Boolean = false,
    val exerciseCycle: Int = 1,
    // Demo Mode State
    val isDemoRunning: Boolean = false,
    val demoStepMessage: String = ""
)

class SaathViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SaathDatabase.getDatabase(application, viewModelScope)
    private val repository = SaathRepository(database.saathDao())
    private val aiService = SaathAiService()
    private val wearableService = WearableService()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var focusTimerJob: Job? = null
    private var exerciseTimerJob: Job? = null

    init {
        viewModelScope.launch {
            // Combine flows
            combine(
                repository.userProfile,
                repository.saathiConfig,
                repository.tasks,
                repository.latestCheckIn,
                repository.contacts,
                repository.chatMessages,
                repository.wearableMetrics
            ) { profile, saathi, tasks, checkIn, contacts, chat, wearable ->
                _uiState.update { current ->
                    current.copy(
                        userProfile = profile ?: current.userProfile,
                        saathiConfig = saathi ?: current.saathiConfig,
                        tasks = tasks,
                        latestCheckIn = checkIn ?: current.latestCheckIn,
                        contacts = contacts,
                        chatMessages = chat,
                        wearableMetrics = wearable ?: current.wearableMetrics
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            repository.checkIns.collect { list ->
                _uiState.update { it.copy(checkIns = list) }
            }
        }

        viewModelScope.launch {
            repository.focusSessions.collect { list ->
                _uiState.update { it.copy(focusSessions = list) }
            }
        }

        viewModelScope.launch {
            repository.journalEntries.collect { list ->
                _uiState.update { it.copy(journalEntries = list) }
            }
        }
    }

    // --- Onboarding & Auth Actions ---
    fun updateProfile(
        name: String,
        ageGroup: String,
        interests: String,
        goals: String
    ) {
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(
                fullName = name,
                ageGroup = ageGroup,
                selectedInterests = interests,
                userGoals = goals,
                isOnboarded = true,
                isRegistered = true
            )
            repository.saveUserProfile(updated)
        }
    }

    fun updateSaathiConfig(
        name: String,
        avatarId: String,
        voice: String,
        personality: String,
        language: String
    ) {
        viewModelScope.launch {
            val config = SaathiConfig(
                name = name,
                avatarId = avatarId,
                voice = voice,
                personality = personality,
                language = language
            )
            repository.saveSaathiConfig(config)
        }
    }

    // --- Morning Check-In ---
    fun submitCheckIn(
        mood: String,
        stressLevel: Int,
        priorities: List<String>,
        notes: String,
        onGenerated: (List<Task>) -> Unit
    ) {
        viewModelScope.launch {
            val checkIn = CheckIn(
                dateStr = "2026-09-23",
                mood = mood,
                stressLevel = stressLevel,
                prioritiesJson = priorities.joinToString(","),
                notes = notes,
                timeOfDay = "Morning"
            )
            repository.saveCheckIn(checkIn)

            // Generate daily plan tasks via AI
            val plannedTasks = aiService.generateDailyPlanSuggestions(checkIn)
            onGenerated(plannedTasks)
        }
    }

    fun acceptDailyPlan(tasks: List<Task>) {
        viewModelScope.launch {
            repository.addTasks(tasks)
        }
    }

    // --- Tasks Management ---
    fun addTask(title: String, category: String, scheduledTime: String, notes: String = "") {
        viewModelScope.launch {
            val task = Task(
                title = title,
                category = category,
                scheduledTime = scheduledTime,
                notes = notes
            )
            repository.addTask(task)
        }
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- AI Chat & Reality Engine ---
    fun sendMessage(userText: String, onActionTriggered: ((String, String) -> Unit)? = null) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Save user message
            val userMsg = ChatMessage(
                sender = "user",
                text = userText,
                timestamp = System.currentTimeMillis()
            )
            repository.addChatMessage(userMsg)

            _uiState.update { it.copy(isAiThinking = true) }

            val context = UserContext(
                userProfile = _uiState.value.userProfile,
                saathiConfig = _uiState.value.saathiConfig,
                latestCheckIn = _uiState.value.latestCheckIn,
                tasks = _uiState.value.tasks,
                wearableMetrics = _uiState.value.wearableMetrics
            )

            val response = aiService.processUserChat(userText, context)

            val saathiMsg = ChatMessage(
                sender = "saathi",
                text = response.replyText,
                timestamp = System.currentTimeMillis(),
                suggestionChipsJson = response.suggestionChips.joinToString(","),
                actionType = response.actionType,
                actionPayload = response.actionPayload
            )
            repository.addChatMessage(saathiMsg)

            _uiState.update { it.copy(isAiThinking = false) }

            if (response.actionType.isNotEmpty()) {
                onActionTriggered?.invoke(response.actionType, response.actionPayload)
            }
        }
    }

    // --- Focus Timer ---
    fun startFocusSession(taskTitle: String, durationMinutes: Int = 25) {
        focusTimerJob?.cancel()
        _uiState.update {
            it.copy(
                currentFocusTaskTitle = taskTitle,
                focusTimerTotalSeconds = durationMinutes * 60,
                focusTimerSecondsLeft = durationMinutes * 60,
                isFocusTimerRunning = true
            )
        }
        focusTimerJob = viewModelScope.launch {
            while (_uiState.value.focusTimerSecondsLeft > 0 && _uiState.value.isFocusTimerRunning) {
                delay(1000)
                _uiState.update { it.copy(focusTimerSecondsLeft = it.focusTimerSecondsLeft - 1) }
            }
            if (_uiState.value.focusTimerSecondsLeft == 0) {
                completeFocusSession()
            }
        }
    }

    fun pauseFocusSession() {
        focusTimerJob?.cancel()
        _uiState.update { it.copy(isFocusTimerRunning = false) }
    }

    fun resumeFocusSession() {
        if (_uiState.value.focusTimerSecondsLeft <= 0) return
        _uiState.update { it.copy(isFocusTimerRunning = true) }
        focusTimerJob = viewModelScope.launch {
            while (_uiState.value.focusTimerSecondsLeft > 0 && _uiState.value.isFocusTimerRunning) {
                delay(1000)
                _uiState.update { it.copy(focusTimerSecondsLeft = it.focusTimerSecondsLeft - 1) }
            }
            if (_uiState.value.focusTimerSecondsLeft == 0) {
                completeFocusSession()
            }
        }
    }

    fun resetFocusSession() {
        focusTimerJob?.cancel()
        _uiState.update {
            it.copy(
                isFocusTimerRunning = false,
                focusTimerSecondsLeft = it.focusTimerTotalSeconds
            )
        }
    }

    private fun completeFocusSession() {
        viewModelScope.launch {
            val totalMins = _uiState.value.focusTimerTotalSeconds / 60
            repository.addFocusSession(
                FocusSession(
                    taskTitle = _uiState.value.currentFocusTaskTitle,
                    durationMinutes = totalMins,
                    completedMinutes = totalMins
                )
            )
            _uiState.update { it.copy(isFocusTimerRunning = false) }
        }
    }

    // --- Reset Exercise ---
    fun startResetExercise(exerciseName: String) {
        exerciseTimerJob?.cancel()
        _uiState.update {
            it.copy(
                currentExerciseTitle = exerciseName,
                isExerciseRunning = true,
                exercisePhase = "Inhale",
                exerciseSecondsLeft = 4,
                exerciseCycle = 1
            )
        }

        exerciseTimerJob = viewModelScope.launch {
            // 4-7-8 breathing sequence: Inhale (4s), Hold (7s), Exhale (8s)
            for (cycle in 1..4) {
                _uiState.update { it.copy(exerciseCycle = cycle, exercisePhase = "Inhale") }
                for (s in 4 downTo 1) {
                    _uiState.update { it.copy(exerciseSecondsLeft = s) }
                    delay(1000)
                }

                _uiState.update { it.copy(exercisePhase = "Hold") }
                for (s in 7 downTo 1) {
                    _uiState.update { it.copy(exerciseSecondsLeft = s) }
                    delay(1000)
                }

                _uiState.update { it.copy(exercisePhase = "Exhale") }
                for (s in 8 downTo 1) {
                    _uiState.update { it.copy(exerciseSecondsLeft = s) }
                    delay(1000)
                }
            }
            _uiState.update { it.copy(isExerciseRunning = false, exercisePhase = "Completed") }
        }
    }

    fun stopResetExercise() {
        exerciseTimerJob?.cancel()
        _uiState.update { it.copy(isExerciseRunning = false) }
    }

    // --- Journal ---
    fun saveJournal(mood: String, helpedTags: List<String>, text: String) {
        viewModelScope.launch {
            repository.addJournalEntry(
                JournalEntry(
                    dateStr = "2026-09-23",
                    mood = mood,
                    helpedTags = helpedTags.joinToString(","),
                    reflectionText = text,
                    saathiResponse = "Thank you for reflecting today. Writing it down takes great courage and self-awareness."
                )
            )
        }
    }

    // --- Trusted Contacts & Circle ---
    fun addTrustedContact(name: String, relation: String, phone: String, email: String) {
        viewModelScope.launch {
            repository.addContact(
                TrustedContact(
                    name = name,
                    relation = relation,
                    phone = phone,
                    email = email
                )
            )
        }
    }

    fun deleteContact(contact: TrustedContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun createSupportRequest(contactName: String, topic: String, draftMessage: String) {
        viewModelScope.launch {
            repository.addSupportRequest(
                SupportRequest(
                    contactName = contactName,
                    topic = topic,
                    draftMessage = draftMessage
                )
            )
        }
    }

    // --- Wearable Sync ---
    fun syncWearable() {
        viewModelScope.launch {
            val updated = wearableService.syncData()
            repository.updateWearableMetrics(updated)
        }
    }

    fun toggleWearableConnection() {
        viewModelScope.launch {
            val connected = wearableService.toggleConnection()
            val metrics = _uiState.value.wearableMetrics.copy(isConnected = connected)
            repository.updateWearableMetrics(metrics)
        }
    }

    // --- Reset Demo Data ---
    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
        }
    }
}
