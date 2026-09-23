package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "primary_user",
    val fullName: String = "Aditya Mishra",
    val email: String = "aditya.mishra@example.com",
    val phone: String = "+91 98765 43210",
    val location: String = "Lucknow, Uttar Pradesh",
    val birthday: String = "22 September 2004",
    val quote: String = "Progress, not perfection.",
    val aboutMe: String = "A learner, builder and dreamer. Interested in technology, problem solving and creating a positive impact. Believing in small steps every day.",
    val ageGroup: String = "18 – 24",
    val selectedInterests: String = "Technology,Fitness,Reading,Startups,Travel",
    val userGoals: String = "Build 5 meaningful projects,Stay fit and healthy,Learn something new every day,Pursue higher studies abroad,Create a positive impact in society",
    val isRegistered: Boolean = true,
    val isOnboarded: Boolean = true
)

@Entity(tableName = "saathi_config")
data class SaathiConfig(
    @PrimaryKey val id: String = "primary_saathi",
    val name: String = "Aarav",
    val avatarId: String = "aarav", // aarav, mira, kian, nova
    val voice: String = "Male",     // Male, Female, Neutral
    val personality: String = "Friendly", // Calm, Friendly, Motivating, Funny, Straightforward
    val language: String = "English"     // English, Hindi, Hinglish
)

@Entity(tableName = "check_ins")
data class CheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String,             // e.g. "2026-09-23"
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String,                // Great, Good, Okay, Low, Stressed
    val stressLevel: Int,            // 0 - 10
    val prioritiesJson: String,      // comma separated or JSON e.g. "College,Study,Assignment"
    val notes: String = "",
    val timeOfDay: String = "Morning" // Morning, Evening
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Personal", // College, Study, Assignment, Workout, Personal, Project
    val scheduledTime: String = "09:00 AM",
    val durationMinutes: Int = 60,
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // High, Medium, Low
    val notes: String = "",
    val dateStr: String = "2026-09-23",
    val subtasksJson: String = "" // List of subtask titles
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskTitle: String,
    val durationMinutes: Int,
    val completedMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val rating: String = "Productive"
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String,
    val helpedTags: String, // "Walking,Breathing,Talking to someone"
    val reflectionText: String,
    val saathiResponse: String = ""
)

@Entity(tableName = "trusted_contacts")
data class TrustedContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relation: String, // Mom, Dad, Sibling, Friend, Partner, Mentor
    val phone: String,
    val email: String = "",
    val avatarColor: Long = 0xFF165C45,
    val canReceiveSupport: Boolean = true,
    val canReceiveMonthlySummary: Boolean = true,
    val canReceiveUpdates: Boolean = false
)

@Entity(tableName = "support_requests")
data class SupportRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactName: String,
    val topic: String,
    val draftMessage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Prepared" // Prepared, Sent, Called
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "saathi"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestionChipsJson: String = "", // Options or quick chips
    val actionType: String = "",         // "RESET", "BREAK_TASK", "FOCUS", "CIRCLE"
    val actionPayload: String = ""
)

@Entity(tableName = "wearable_metrics")
data class WearableMetrics(
    @PrimaryKey val id: String = "current",
    val isConnected: Boolean = true,
    val deviceName: String = "Amazfit Band 7",
    val heartRate: Int = 74,
    val sleepDurationMinutes: Int = 380, // 6h 20m
    val sleepScore: Int = 85,
    val steps: Int = 3482,
    val stressScore: Int = 38,
    val activeMinutes: Int = 32,
    val lastSyncedTimestamp: Long = System.currentTimeMillis()
)
