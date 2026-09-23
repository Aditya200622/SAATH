package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.data.model.Task
import com.example.data.model.TrustedContact
import com.example.ui.SaathViewModel
import com.example.ui.components.NavTab
import com.example.ui.components.SaathBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.WarmWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SaathDestinations {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SAATHI_CHAT = "saathi_chat"
    const val TASKS = "tasks"
    const val CIRCLE = "circle"
    const val INSIGHTS = "insights"
    const val MORNING_CHECK_IN = "morning_check_in"
    const val DAILY_PLAN = "daily_plan"
    const val RESET_EXERCISE = "reset_exercise"
    const val FOCUS_SESSION = "focus_session"
    const val JOURNAL = "journal"
    const val ADD_CONTACT = "add_contact"
    const val SUPPORT_REQUEST = "support_request"
    const val TASK_DETAIL = "task_detail"
    const val SAFETY = "safety"
    const val SETTINGS = "settings"
}

@Composable
fun SaathApp(
    viewModel: SaathViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Temporary holding states for navigation parameters
    var selectedTaskForDetail by remember { mutableStateOf<Task?>(null) }
    var selectedContactForSupport by remember { mutableStateOf<TrustedContact?>(null) }
    var plannedTasksHolder by remember { mutableStateOf<List<Task>>(emptyList()) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: SaathDestinations.HOME

    val currentTab = when (currentRoute) {
        SaathDestinations.HOME -> NavTab.HOME
        SaathDestinations.SAATHI_CHAT -> NavTab.SAATHI
        SaathDestinations.TASKS, SaathDestinations.TASK_DETAIL -> NavTab.TASKS
        SaathDestinations.CIRCLE, SaathDestinations.ADD_CONTACT, SaathDestinations.SUPPORT_REQUEST -> NavTab.CIRCLE
        SaathDestinations.INSIGHTS -> NavTab.INSIGHTS
        else -> NavTab.HOME
    }

    val showBottomBar = currentRoute in listOf(
        SaathDestinations.HOME,
        SaathDestinations.SAATHI_CHAT,
        SaathDestinations.TASKS,
        SaathDestinations.CIRCLE,
        SaathDestinations.INSIGHTS
    )

    // Demo Mode Automated Runner
    fun run90SecondDemo() {
        scope.launch {
            // 1. Go to Check In
            navController.navigate(SaathDestinations.MORNING_CHECK_IN)
            delay(1500)
            // 2. Submit Check in and see AI plan
            viewModel.submitCheckIn(
                mood = "Good",
                stressLevel = 5,
                priorities = listOf("College", "Study", "Assignment", "Workout"),
                notes = "Feeling somewhat overwhelmed with the C programming assignment due tomorrow."
            ) { generated ->
                plannedTasksHolder = generated
            }
            delay(1500)
            navController.navigate(SaathDestinations.DAILY_PLAN)
            delay(2000)

            // 3. Go to Saathi Chat and see reality engine breakdown
            navController.navigate(SaathDestinations.SAATHI_CHAT)
            delay(1000)
            viewModel.sendMessage("I'm feeling really stressed about my assignment tomorrow.")
            delay(2500)

            // 4. Open 2-Minute Calming Reset
            navController.navigate(SaathDestinations.RESET_EXERCISE)
            viewModel.startResetExercise("4-7-8 Breathing")
            delay(3500)

            // 5. Open Focus Session
            navController.navigate(SaathDestinations.FOCUS_SESSION)
            viewModel.startFocusSession("Assignment Prep", 25)
            delay(3000)

            // 6. Return Home
            navController.navigate(SaathDestinations.HOME)
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                SaathBottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        val target = when (tab) {
                            NavTab.HOME -> SaathDestinations.HOME
                            NavTab.SAATHI -> SaathDestinations.SAATHI_CHAT
                            NavTab.TASKS -> SaathDestinations.TASKS
                            NavTab.CIRCLE -> SaathDestinations.CIRCLE
                            NavTab.INSIGHTS -> SaathDestinations.INSIGHTS
                        }
                        if (target != currentRoute) {
                            navController.navigate(target) {
                                popUpTo(SaathDestinations.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        containerColor = WarmWhite,
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (uiState.userProfile.isOnboarded) SaathDestinations.HOME else SaathDestinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Login
            composable(SaathDestinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(SaathDestinations.HOME) },
                    onNavigateToSignUp = { navController.navigate(SaathDestinations.SIGN_UP) }
                )
            }

            // 2. Sign Up
            composable(SaathDestinations.SIGN_UP) {
                SignUpScreen(
                    onSignUpSuccess = { navController.navigate(SaathDestinations.ONBOARDING) },
                    onNavigateToLogin = { navController.navigate(SaathDestinations.LOGIN) }
                )
            }

            // 3. Onboarding
            composable(SaathDestinations.ONBOARDING) {
                OnboardingScreen(
                    onComplete = { name, age, goals, saathiName, avatar, voice, personality, lang ->
                        viewModel.updateProfile(name, age, "Tech,Reading", goals)
                        viewModel.updateSaathiConfig(saathiName, avatar, voice, personality, lang)
                        navController.navigate(SaathDestinations.HOME)
                    },
                    onSkip = { navController.navigate(SaathDestinations.HOME) }
                )
            }

            // 4. Home
            composable(SaathDestinations.HOME) {
                HomeScreen(
                    userProfile = uiState.userProfile,
                    tasks = uiState.tasks,
                    wearableMetrics = uiState.wearableMetrics,
                    latestMood = uiState.latestCheckIn?.mood ?: "Good",
                    latestStress = uiState.latestCheckIn?.stressLevel ?: 4,
                    saathiName = uiState.saathiConfig.name,
                    avatarId = uiState.saathiConfig.avatarId,
                    onNavigateToChat = { navController.navigate(SaathDestinations.SAATHI_CHAT) },
                    onNavigateToReset = { navController.navigate(SaathDestinations.RESET_EXERCISE) },
                    onNavigateToFocus = { taskTitle ->
                        viewModel.startFocusSession(taskTitle, 25)
                        navController.navigate(SaathDestinations.FOCUS_SESSION)
                    },
                    onNavigateToTasks = { navController.navigate(SaathDestinations.TASKS) },
                    onNavigateToJournal = { navController.navigate(SaathDestinations.JOURNAL) },
                    onNavigateToCircle = { navController.navigate(SaathDestinations.CIRCLE) },
                    onNavigateToEmergency = { navController.navigate(SaathDestinations.SAFETY) },
                    onNavigateToProfile = { navController.navigate(SaathDestinations.SETTINGS) },
                    onToggleTaskComplete = { viewModel.toggleTaskComplete(it) },
                    onRunDemo = { run90SecondDemo() }
                )
            }

            // 5. Saathi Chat
            composable(SaathDestinations.SAATHI_CHAT) {
                SaathiChatScreen(
                    saathiConfig = uiState.saathiConfig,
                    messages = uiState.chatMessages,
                    isAiThinking = uiState.isAiThinking,
                    onSendMessage = { text ->
                        viewModel.sendMessage(text) { action, payload ->
                            if (action == "RESET") navController.navigate(SaathDestinations.RESET_EXERCISE)
                            if (action == "FOCUS") {
                                viewModel.startFocusSession(payload.ifEmpty { "Focus Session" }, 25)
                                navController.navigate(SaathDestinations.FOCUS_SESSION)
                            }
                            if (action == "EMERGENCY_SUPPORT") navController.navigate(SaathDestinations.SAFETY)
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onStartReset = { navController.navigate(SaathDestinations.RESET_EXERCISE) },
                    onStartFocus = { title ->
                        viewModel.startFocusSession(title, 25)
                        navController.navigate(SaathDestinations.FOCUS_SESSION)
                    },
                    onNavigateToTasks = { navController.navigate(SaathDestinations.TASKS) },
                    onNavigateToCircle = { navController.navigate(SaathDestinations.CIRCLE) },
                    onNavigateToEmergency = { navController.navigate(SaathDestinations.SAFETY) }
                )
            }

            // 6. Tasks
            composable(SaathDestinations.TASKS) {
                TasksScreen(
                    tasks = uiState.tasks,
                    onToggleTaskComplete = { viewModel.toggleTaskComplete(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onAddTask = { title, category, time, notes ->
                        viewModel.addTask(title, category, time, notes)
                    },
                    onStartFocus = { title ->
                        viewModel.startFocusSession(title, 25)
                        navController.navigate(SaathDestinations.FOCUS_SESSION)
                    },
                    onNavigateToDetail = { task ->
                        selectedTaskForDetail = task
                        navController.navigate(SaathDestinations.TASK_DETAIL)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 7. Task Detail
            composable(SaathDestinations.TASK_DETAIL) {
                val task = selectedTaskForDetail ?: uiState.tasks.firstOrNull() ?: Task(title = "Assignment", category = "Assignment")
                TaskDetailScreen(
                    task = task,
                    onToggleComplete = { viewModel.toggleTaskComplete(task) },
                    onStartFocus = {
                        viewModel.startFocusSession(task.title, 25)
                        navController.navigate(SaathDestinations.FOCUS_SESSION)
                    },
                    onBreakDownAi = {
                        navController.navigate(SaathDestinations.SAATHI_CHAT)
                        viewModel.sendMessage("Can you break down ${task.title} into 3 small steps?")
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 8. Morning Check In
            composable(SaathDestinations.MORNING_CHECK_IN) {
                MorningCheckInScreen(
                    userName = uiState.userProfile.fullName.split(" ").firstOrNull() ?: "Aditya",
                    avatarId = uiState.saathiConfig.avatarId,
                    onSubmitCheckIn = { mood, stress, priorities, notes ->
                        viewModel.submitCheckIn(mood, stress, priorities, notes) { generated ->
                            plannedTasksHolder = generated
                            navController.navigate(SaathDestinations.DAILY_PLAN)
                        }
                    },
                    onSkip = { navController.navigate(SaathDestinations.HOME) }
                )
            }

            // 9. Daily Plan
            composable(SaathDestinations.DAILY_PLAN) {
                val list = if (plannedTasksHolder.isNotEmpty()) plannedTasksHolder else uiState.tasks
                DailyPlanScreen(
                    generatedTasks = list,
                    onAcceptPlan = { tasks ->
                        viewModel.acceptDailyPlan(tasks)
                    },
                    onNavigateToHome = { navController.navigate(SaathDestinations.HOME) }
                )
            }

            // 10. 2-Min Reset Exercise
            composable(SaathDestinations.RESET_EXERCISE) {
                ResetExerciseScreen(
                    currentExerciseTitle = uiState.currentExerciseTitle,
                    exercisePhase = uiState.exercisePhase,
                    exerciseSecondsLeft = uiState.exerciseSecondsLeft,
                    exerciseCycle = uiState.exerciseCycle,
                    isExerciseRunning = uiState.isExerciseRunning,
                    onStartExercise = { viewModel.startResetExercise(it) },
                    onStopExercise = { viewModel.stopResetExercise() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 11. Focus Session
            composable(SaathDestinations.FOCUS_SESSION) {
                FocusSessionScreen(
                    taskTitle = uiState.currentFocusTaskTitle,
                    secondsLeft = uiState.focusTimerSecondsLeft,
                    totalSeconds = uiState.focusTimerTotalSeconds,
                    isRunning = uiState.isFocusTimerRunning,
                    onPlay = { viewModel.resumeFocusSession() },
                    onPause = { viewModel.pauseFocusSession() },
                    onReset = { viewModel.resetFocusSession() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 12. Journal
            composable(SaathDestinations.JOURNAL) {
                JournalScreen(
                    onSaveJournal = { mood, tags, text ->
                        viewModel.saveJournal(mood, tags, text)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 13. Circle
            composable(SaathDestinations.CIRCLE) {
                CircleScreen(
                    contacts = uiState.contacts,
                    onAddContactClick = { navController.navigate(SaathDestinations.ADD_CONTACT) },
                    onContactSupportClick = { contact ->
                        selectedContactForSupport = contact
                        navController.navigate(SaathDestinations.SUPPORT_REQUEST)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 14. Add Contact
            composable(SaathDestinations.ADD_CONTACT) {
                AddManageContactScreen(
                    onSaveContact = { name, rel, phone, email ->
                        viewModel.addTrustedContact(name, rel, phone, email)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 15. Support Request
            composable(SaathDestinations.SUPPORT_REQUEST) {
                val contact = selectedContactForSupport ?: uiState.contacts.firstOrNull() ?: TrustedContact(name = "Mom", relation = "Mom", phone = "+91 98111 22334")
                SupportRequestScreen(
                    contact = contact,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 16. Insights
            composable(SaathDestinations.INSIGHTS) {
                InsightsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 17. Safety
            composable(SaathDestinations.SAFETY) {
                SafetyScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onStartGrounding = { navController.navigate(SaathDestinations.RESET_EXERCISE) },
                    onReachCircle = { navController.navigate(SaathDestinations.CIRCLE) }
                )
            }

            // 18. Settings
            composable(SaathDestinations.SETTINGS) {
                SettingsScreen(
                    userProfile = uiState.userProfile,
                    saathiConfig = uiState.saathiConfig,
                    wearableMetrics = uiState.wearableMetrics,
                    onSyncWearable = { viewModel.syncWearable() },
                    onToggleWearable = { viewModel.toggleWearableConnection() },
                    onResetDemoData = { viewModel.resetToDemoData() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
