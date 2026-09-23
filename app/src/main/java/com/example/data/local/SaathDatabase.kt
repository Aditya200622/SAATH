package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        SaathiConfig::class,
        CheckIn::class,
        Task::class,
        FocusSession::class,
        JournalEntry::class,
        TrustedContact::class,
        SupportRequest::class,
        ChatMessage::class,
        WearableMetrics::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SaathDatabase : RoomDatabase() {
    abstract fun saathDao(): SaathDao

    companion object {
        @Volatile
        private var INSTANCE: SaathDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SaathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaathDatabase::class.java,
                    "saath_database"
                )
                .addCallback(SaathDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class SaathDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.saathDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: SaathDao) {
            // Seed User Profile
            dao.insertUserProfile(
                UserProfile(
                    id = "primary_user",
                    fullName = "Aditya Mishra",
                    email = "aditya.mishra@example.com",
                    phone = "+91 98765 43210",
                    location = "Lucknow, Uttar Pradesh",
                    birthday = "22 September 2004",
                    quote = "Progress, not perfection.",
                    aboutMe = "A learner, builder and dreamer. Interested in technology, problem solving and creating a positive impact. Believing in small steps every day.",
                    ageGroup = "18 – 24",
                    selectedInterests = "Technology,Fitness,Reading,Startups,Travel",
                    userGoals = "Build 5 meaningful projects,Stay fit and healthy,Learn something new every day,Pursue higher studies abroad,Create a positive impact in society",
                    isRegistered = true,
                    isOnboarded = true
                )
            )

            // Seed Saathi Config
            dao.insertSaathiConfig(
                SaathiConfig(
                    id = "primary_saathi",
                    name = "Aarav",
                    avatarId = "aarav",
                    voice = "Male",
                    personality = "Friendly",
                    language = "English"
                )
            )

            // Seed Check-in
            dao.insertCheckIn(
                CheckIn(
                    dateStr = "2026-09-23",
                    mood = "Good",
                    stressLevel = 4,
                    prioritiesJson = "College,Study,Assignment,Workout,Project",
                    notes = "Feeling steady today. Need to wrap up the C programming file.",
                    timeOfDay = "Morning"
                )
            )

            // Seed Tasks matching screenshot
            val initialTasks = listOf(
                Task(
                    title = "College Classes",
                    category = "College",
                    scheduledTime = "09:00 AM – 11:00 AM",
                    durationMinutes = 120,
                    isCompleted = true,
                    priority = "High",
                    notes = "Forensic Science - Lecture Hall 1",
                    subtasksJson = "Attend lecture,Take notes,Review lab report"
                ),
                Task(
                    title = "Study / Revision",
                    category = "Study",
                    scheduledTime = "11:30 AM – 01:00 PM",
                    durationMinutes = 90,
                    isCompleted = false,
                    priority = "High",
                    notes = "TA & Engineering Graphics",
                    subtasksJson = "Review projection formulas,Solve sample paper 2"
                ),
                Task(
                    title = "Assignment",
                    category = "Assignment",
                    scheduledTime = "02:00 PM – 04:00 PM",
                    durationMinutes = 120,
                    isCompleted = false,
                    priority = "High",
                    notes = "Complete C Programming File",
                    subtasksJson = "Draft flowchart,Write file I/O module,Verify test cases"
                ),
                Task(
                    title = "Workout",
                    category = "Workout",
                    scheduledTime = "05:00 PM – 05:30 PM",
                    durationMinutes = 30,
                    isCompleted = false,
                    priority = "Medium",
                    notes = "30 mins • Stay Active",
                    subtasksJson = "5 min warm-up,20 min HIIT,5 min cool-down"
                ),
                Task(
                    title = "Personal Project",
                    category = "Project",
                    scheduledTime = "08:00 PM – 10:00 PM",
                    durationMinutes = 120,
                    isCompleted = false,
                    priority = "Medium",
                    notes = "Work on SAATH App",
                    subtasksJson = "Design check-in flow,Connect AI provider,Test room database"
                )
            )
            dao.insertTasks(initialTasks)

            // Seed Trusted Contacts
            dao.insertContact(
                TrustedContact(
                    name = "Mom",
                    relation = "Mom",
                    phone = "+91 98111 22334",
                    email = "mom@example.com",
                    avatarColor = 0xFF38A169,
                    canReceiveSupport = true,
                    canReceiveMonthlySummary = true,
                    canReceiveUpdates = true
                )
            )
            dao.insertContact(
                TrustedContact(
                    name = "Dad",
                    relation = "Dad",
                    phone = "+91 98111 55667",
                    email = "dad@example.com",
                    avatarColor = 0xFF4F7CFF,
                    canReceiveSupport = true,
                    canReceiveMonthlySummary = true,
                    canReceiveUpdates = false
                )
            )
            dao.insertContact(
                TrustedContact(
                    name = "Karan",
                    relation = "Friend",
                    phone = "+91 98222 33445",
                    email = "karan@example.com",
                    avatarColor = 0xFFE6A23C,
                    canReceiveSupport = true,
                    canReceiveMonthlySummary = false,
                    canReceiveUpdates = true
                )
            )
            dao.insertContact(
                TrustedContact(
                    name = "Riya",
                    relation = "Study Partner",
                    phone = "+91 98333 44556",
                    email = "riya@example.com",
                    avatarColor = 0xFF8B5CF6,
                    canReceiveSupport = true,
                    canReceiveMonthlySummary = false,
                    canReceiveUpdates = false
                )
            )

            // Seed Chat Messages
            dao.insertChatMessage(
                ChatMessage(
                    sender = "saathi",
                    text = "Hey Aditya! 👋 How are you feeling right now?",
                    timestamp = System.currentTimeMillis() - 120000
                )
            )
            dao.insertChatMessage(
                ChatMessage(
                    sender = "user",
                    text = "A bit stressed. I have an assignment due tomorrow and I'm not sure where to start.",
                    timestamp = System.currentTimeMillis() - 100000
                )
            )
            dao.insertChatMessage(
                ChatMessage(
                    sender = "saathi",
                    text = "I get it. Let's break it down together. Small steps make it easier. 💚\n\nWould you like me to:",
                    timestamp = System.currentTimeMillis() - 80000,
                    suggestionChipsJson = "Break the task into smaller steps,Find resources (notes / videos),Set a focus timer,Or just talk about it"
                )
            )
            dao.insertChatMessage(
                ChatMessage(
                    sender = "user",
                    text = "Let's break it into smaller steps.",
                    timestamp = System.currentTimeMillis() - 60000
                )
            )
            dao.insertChatMessage(
                ChatMessage(
                    sender = "saathi",
                    text = "Great! 🎯 Here's a simple plan for your assignment:\n\n1. Define project goal (5 mins)\n2. Outline the 3 main parts (10 mins)\n3. Start the first section with a 25-minute focus session.\n\nYou've got this! Want to start a 25-minute focus timer right now?",
                    timestamp = System.currentTimeMillis() - 40000,
                    actionType = "FOCUS",
                    actionPayload = "Assignment"
                )
            )

            // Seed Wearable Metrics
            dao.insertWearableMetrics(
                WearableMetrics(
                    id = "current",
                    isConnected = true,
                    deviceName = "Amazfit Band 7",
                    heartRate = 74,
                    sleepDurationMinutes = 380, // 6h 20m
                    sleepScore = 85,
                    steps = 3482,
                    stressScore = 38,
                    activeMinutes = 32,
                    lastSyncedTimestamp = System.currentTimeMillis()
                )
            )

            // Seed Focus Session
            dao.insertFocusSession(
                FocusSession(
                    taskTitle = "College Classes",
                    durationMinutes = 60,
                    completedMinutes = 60,
                    timestamp = System.currentTimeMillis() - 7200000,
                    rating = "Great focus"
                )
            )

            // Seed Journal Entry
            dao.insertJournalEntry(
                JournalEntry(
                    dateStr = "2026-09-22",
                    timestamp = System.currentTimeMillis() - 86400000,
                    mood = "Better",
                    helpedTags = "Breathing,Completing a task,Walking",
                    reflectionText = "Felt overwhelmed in the morning, but doing the 2-minute reset helped me calm down and finish the graphics assignment.",
                    saathiResponse = "Proud of how you noticed the overwhelm and took a 2-minute reset. That's real progress!"
                )
            )
        }
    }
}
