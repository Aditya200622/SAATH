package com.example.ai

import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class SafetyLevel {
    EVERYDAY_SUPPORT,
    PERSISTENT_SUPPORT,
    HIGH_RISK
}

data class SafetyResult(
    val level: SafetyLevel,
    val message: String,
    val emergencyResources: List<EmergencyResource> = emptyList()
)

data class EmergencyResource(
    val name: String,
    val description: String,
    val contactNumber: String,
    val isAvailable24x7: Boolean = true
)

data class TaskBreakdownResult(
    val mainTask: String,
    val identifiedStressor: String,
    val immediateSteps: List<String>,
    val focusSessionSuggestion: String,
    val resetExerciseSuggestion: String
)

data class UserContext(
    val userProfile: UserProfile,
    val saathiConfig: SaathiConfig,
    val latestCheckIn: CheckIn?,
    val tasks: List<Task>,
    val wearableMetrics: WearableMetrics?
)

interface AiProvider {
    suspend fun generateContent(prompt: String, systemInstruction: String): String
}

class GeminiProvider : AiProvider {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun generateContent(prompt: String, systemInstruction: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "" // Trigger fallback heuristic
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 1024)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ""
                val responseString = response.body?.string() ?: return@withContext ""
                val root = JSONObject(responseString)
                val candidates = root.optJSONArray("candidates") ?: return@withContext ""
                val firstCandidate = candidates.optJSONObject(0) ?: return@withContext ""
                val content = firstCandidate.optJSONObject("content") ?: return@withContext ""
                val parts = content.optJSONArray("parts") ?: return@withContext ""
                val text = parts.optJSONObject(0)?.optString("text") ?: ""
                return@withContext text
            }
        } catch (e: Exception) {
            return@withContext ""
        }
    }
}

class SafetyEngine {
    private val highRiskKeywords = listOf(
        "suicide", "suicidal", "kill myself", "killing myself", "end my life", "ending my life",
        "don't want to live", "dont want to live", "die", "harm myself", "harming myself",
        "hurt myself", "hurting myself", "cutting", "overdose", "better off dead", "no reason to live"
    )

    private val persistentKeywords = listOf(
        "depressed for weeks", "hopeless", "can't go on", "crying every day", "panic attack constantly", "chronic pain"
    )

    fun evaluate(text: String): SafetyResult {
        val lower = text.lowercase()

        for (kw in highRiskKeywords) {
            if (lower.contains(kw)) {
                return SafetyResult(
                    level = SafetyLevel.HIGH_RISK,
                    message = "I hear you, and you are not alone right now. Please reach out to someone who cares about you or a 24/7 confidential helpline immediately. There is help, and you matter.",
                    emergencyResources = listOf(
                        EmergencyResource("Tele-MANAS (Govt of India)", "Free 24x7 Mental Health Helpline", "14416"),
                        EmergencyResource("KIRAN Mental Health", "Toll-Free 24/7 Helpline", "1800-599-0019"),
                        EmergencyResource("Vandrevala Foundation", "24/7 Free Crisis Counseling", "9999 666 555"),
                        EmergencyResource("AASRA", "Crisis Intervention & Suicide Prevention", "+91 98204 66726"),
                        EmergencyResource("National Emergency", "Immediate Police / Ambulance Support", "112")
                    )
                )
            }
        }

        for (kw in persistentKeywords) {
            if (lower.contains(kw)) {
                return SafetyResult(
                    level = SafetyLevel.PERSISTENT_SUPPORT,
                    message = "It sounds like you've been carrying a heavy weight for a while. While I'm here as your daily companion, sharing this with a professional counselor or trusted doctor can provide deep support.",
                    emergencyResources = listOf(
                        EmergencyResource("Tele-MANAS", "Free National Mental Health Support", "14416"),
                        EmergencyResource("Vandrevala Foundation", "Free Counseling Support", "9999 666 555")
                    )
                )
            }
        }

        return SafetyResult(
            level = SafetyLevel.EVERYDAY_SUPPORT,
            message = "Everyday wellbeing support."
        )
    }
}

class RealityEngine {
    fun breakdownStressor(text: String, context: UserContext): TaskBreakdownResult {
        val lower = text.lowercase()
        val stressor = when {
            lower.contains("project") || lower.contains("unfinished") || lower.contains("code") || lower.contains("app") -> "Project Scope & Completion"
            lower.contains("fight") || lower.contains("friend") || lower.contains("argument") || lower.contains("relationship") -> "Interpersonal Friction & Emotional Processing"
            lower.contains("too much work") || lower.contains("don't know where to start") || lower.contains("overload") || lower.contains("paralyzed") -> "Cognitive Overwhelm & Decision Paralysis"
            lower.contains("assignment") || lower.contains("exam") || lower.contains("study") || lower.contains("class") -> "Academic Deadlines"
            lower.contains("work") || lower.contains("job") || lower.contains("boss") || lower.contains("client") -> "Workplace Pressure"
            lower.contains("workout") || lower.contains("health") || lower.contains("gym") || lower.contains("tired") -> "Physical Exhaustion"
            else -> "Routine & Tasks"
        }

        val (mainTaskTitle, steps) = when (stressor) {
            "Project Scope & Completion" -> Pair(
                "Project Milestone Sprint",
                listOf(
                    "Step 1: Write down the single blocker stopping this project from running (5 mins).",
                    "Step 2: Strip out all non-essential features and define a minimum working version (10 mins).",
                    "Step 3: Run one 25-minute uninterrupted sprint to finish that core module."
                )
            )
            "Interpersonal Friction & Emotional Processing" -> Pair(
                "Emotional Grounding & Reflection",
                listOf(
                    "Step 1: Do a 2-minute 4-7-8 breathing reset to calm the fight-or-flight surge.",
                    "Step 2: Unload what you feel in your private SAATH Journal without sending anything (10 mins).",
                    "Step 3: Give both sides 24 hours of cool-down time before deciding how to reconnect."
                )
            )
            "Cognitive Overwhelm & Decision Paralysis" -> Pair(
                "Micro-Action Momentum",
                listOf(
                    "Step 1: Brain dump everything swirling in your mind onto a blank list (3 mins).",
                    "Step 2: Circle the easiest, lowest-friction task and hide everything else (2 mins).",
                    "Step 3: Do just 10 minutes on that single item to break the paralysis."
                )
            )
            "Academic Deadlines" -> Pair(
                "Assignment Action Plan",
                listOf(
                    "Step 1: Open the file and write down 3 clear bullet points (5 mins).",
                    "Step 2: Collect your main reference sources without judging quality (10 mins).",
                    "Step 3: Start a 25-minute focus sprint on the first section."
                )
            )
            "Workplace Pressure" -> Pair(
                "High-Priority Work Sprint",
                listOf(
                    "Step 1: Write down the single most urgent deliverable for today.",
                    "Step 2: Put devices on Do Not Disturb for a 25-minute focus block.",
                    "Step 3: Draft the basic outline without perfectionism."
                )
            )
            "Physical Exhaustion" -> Pair(
                "Rest & Recovery Protocol",
                listOf(
                    "Step 1: Drink a glass of water and stretch your neck and shoulders.",
                    "Step 2: Take a 5-minute quiet resting pause with eyes closed.",
                    "Step 3: Postpone non-urgent tasks to tomorrow with zero guilt."
                )
            )
            else -> Pair(
                "Gentle Priority Focus",
                listOf(
                    "Step 1: Take 3 slow, deep grounding breaths.",
                    "Step 2: Pick just ONE small thing you can finish in 10 minutes.",
                    "Step 3: Mark it done and celebrate showing up today."
                )
            )
        }

        return TaskBreakdownResult(
            mainTask = mainTaskTitle,
            identifiedStressor = stressor,
            immediateSteps = steps,
            focusSessionSuggestion = "25-minute Pomodoro Block",
            resetExerciseSuggestion = "2-Minute Box Breathing"
        )
    }
}

class SaathAiService(
    private val provider: AiProvider = GeminiProvider(),
    private val safetyEngine: SafetyEngine = SafetyEngine(),
    private val realityEngine: RealityEngine = RealityEngine()
) {
    suspend fun processUserChat(userMessage: String, context: UserContext): ChatResponse {
        val safety = safetyEngine.evaluate(userMessage)
        if (safety.level == SafetyLevel.HIGH_RISK) {
            return ChatResponse(
                replyText = safety.message,
                safetyLevel = safety.level,
                emergencyResources = safety.emergencyResources,
                actionType = "EMERGENCY_SUPPORT"
            )
        }

        val companionName = context.saathiConfig.name
        val userName = context.userProfile.fullName.split(" ").firstOrNull() ?: "Friend"

        val lower = userMessage.lowercase()

        // Reality engine detection for stress & overwhelm
        if (lower.contains("overwhelm") || lower.contains("stress") || lower.contains("assignment") || lower.contains("too much") || lower.contains("anxious")) {
            val breakdown = realityEngine.breakdownStressor(userMessage, context)
            val reply = "I get it, $userName. Let’s break it down together. Small steps make it easier. 💚\n\n" +
                    "I noticed this connects to **${breakdown.identifiedStressor}**.\n\n" +
                    "Here is a simple 3-step action plan:\n" +
                    breakdown.immediateSteps.joinToString("\n") +
                    "\n\nShall we do a quick 2-minute reset, or jump straight into a focus timer?"

            return ChatResponse(
                replyText = reply,
                safetyLevel = safety.level,
                suggestionChips = listOf("2 Min Reset", "Set Focus Timer", "Add to Tasks", "Talk More"),
                actionType = "BREAK_TASK",
                actionPayload = breakdown.mainTask
            )
        }

        // Try Gemini if available
        val systemInstruction = "You are $companionName, a warm, grounded 24x7 personal companion in the SAATH app for $userName. " +
                "Tone: calm, friendly, empathetic, concise (max 3 sentences), practical. " +
                "Never diagnose mental health conditions, never prescribe medications. Focus on small actionable steps, gentle emotional support, and routine guidance."

        val prompt = "User says: \"$userMessage\". " +
                "Context: current mood=${context.latestCheckIn?.mood ?: "Good"}, stress=${context.latestCheckIn?.stressLevel ?: 4}/10, tasks remaining=${context.tasks.count { !it.isCompleted }}."

        val aiResult = provider.generateContent(prompt, systemInstruction)
        if (aiResult.isNotBlank()) {
            return ChatResponse(
                replyText = aiResult.trim(),
                safetyLevel = safety.level,
                suggestionChips = listOf("2 Min Reset", "Add Task", "How am I feeling?", "Give me a tip")
            )
        }

        // Heuristic fallback matching Saathi's personality
        val fallbackReply = when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Hey $userName! 👋 Great to see you. How are you feeling right now? We can plan your day, do a quick breathing reset, or just talk."
            lower.contains("tired") || lower.contains("exhausted") || lower.contains("sleep") ->
                "Listen to your body today, $userName. If you need a 10-minute pause or a cup of warm water, take it without guilt. What's one thing we can gently adjust on your schedule?"
            lower.contains("thank") ->
                "Always here for you, $userName. Remember: progress, not perfection. You're doing better than you think!"
            else ->
                "I'm listening, $userName. Whatever is on your plate today, remember you don't have to carry it all at once. What's the smallest step we can take right now?"
        }

        return ChatResponse(
            replyText = fallbackReply,
            safetyLevel = safety.level,
            suggestionChips = listOf("2 Min Reset", "Add Task", "How am I feeling?", "Give me a tip")
        )
    }

    fun generateSmartNudge(context: UserContext): String {
        val userName = context.userProfile.fullName.split(" ").firstOrNull() ?: "there"
        val pendingCount = context.tasks.count { !it.isCompleted }
        return when {
            context.latestCheckIn?.stressLevel ?: 0 >= 7 ->
                "Hey $userName 🌿 You mentioned feeling some stress earlier. How about a 2-minute calming breathing reset?"
            pendingCount > 0 ->
                "Hey $userName 🎯 Ready for your 25-minute focus session on \"${context.tasks.firstOrNull { !it.isCompleted }?.title ?: "today's task"}\"?"
            else ->
                "Hey $userName ✨ All set for today! Take a moment to reflect on your wins."
        }
    }

    fun generateDailyPlanSuggestions(checkIn: CheckIn): List<Task> {
        val priorities = checkIn.prioritiesJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val generated = mutableListOf<Task>()
        var startHour = 9

        for (priority in priorities) {
            val scheduledTime = String.format("%02d:00 AM – %02d:00 PM", startHour % 12 + 1, (startHour + 2) % 12 + 1)
            val (title, category, note) = when (priority.lowercase()) {
                "college" -> Triple("College Classes", "College", "Lecture Hall 1 • Forensic Science")
                "study" -> Triple("Study / Revision", "Study", "TA & Engineering Graphics")
                "assignment" -> Triple("Assignment Prep", "Assignment", "Complete C Programming File")
                "workout", "exercise" -> Triple("Workout", "Workout", "30 mins • Stay Active")
                "project" -> Triple("Personal Project", "Project", "Work on SAATH App features")
                "social" -> Triple("Catch up with Friend", "Personal", "Call Karan or family")
                else -> Triple("$priority Block", "Personal", "Focus on $priority")
            }
            generated.add(
                Task(
                    title = title,
                    category = category,
                    scheduledTime = scheduledTime,
                    durationMinutes = 90,
                    isCompleted = false,
                    priority = "High",
                    notes = note
                )
            )
            startHour += 2
        }

        if (generated.isEmpty()) {
            generated.add(Task(title = "Morning Deep Work", category = "Work", scheduledTime = "09:00 AM – 11:00 AM", durationMinutes = 120, notes = "Priority goal"))
            generated.add(Task(title = "Afternoon Study", category = "Study", scheduledTime = "02:00 PM – 04:00 PM", durationMinutes = 120, notes = "Core review"))
        }

        return generated
    }
}

data class ChatResponse(
    val replyText: String,
    val safetyLevel: SafetyLevel = SafetyLevel.EVERYDAY_SUPPORT,
    val suggestionChips: List<String> = emptyList(),
    val actionType: String = "",
    val actionPayload: String = "",
    val emergencyResources: List<EmergencyResource> = emptyList()
)
