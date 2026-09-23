package com.example.data.repository

import com.example.data.local.SaathDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class SaathRepository(private val dao: SaathDao) {
    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()
    val saathiConfig: Flow<SaathiConfig?> = dao.getSaathiConfigFlow()
    val checkIns: Flow<List<CheckIn>> = dao.getAllCheckInsFlow()
    val latestCheckIn: Flow<CheckIn?> = dao.getLatestCheckInFlow()
    val tasks: Flow<List<Task>> = dao.getAllTasksFlow()
    val focusSessions: Flow<List<FocusSession>> = dao.getAllFocusSessionsFlow()
    val journalEntries: Flow<List<JournalEntry>> = dao.getAllJournalEntriesFlow()
    val contacts: Flow<List<TrustedContact>> = dao.getAllContactsFlow()
    val supportRequests: Flow<List<SupportRequest>> = dao.getAllSupportRequestsFlow()
    val chatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessagesFlow()
    val wearableMetrics: Flow<WearableMetrics?> = dao.getWearableMetricsFlow()

    suspend fun getUserProfile(): UserProfile? = dao.getUserProfile()
    suspend fun saveUserProfile(profile: UserProfile) = dao.insertUserProfile(profile)

    suspend fun getSaathiConfig(): SaathiConfig? = dao.getSaathiConfig()
    suspend fun saveSaathiConfig(config: SaathiConfig) = dao.insertSaathiConfig(config)

    suspend fun saveCheckIn(checkIn: CheckIn): Long = dao.insertCheckIn(checkIn)
    suspend fun getLatestCheckIn(): CheckIn? = dao.getLatestCheckIn()

    suspend fun addTask(task: Task): Long = dao.insertTask(task)
    suspend fun addTasks(tasks: List<Task>) = dao.insertTasks(tasks)
    suspend fun updateTask(task: Task) = dao.updateTask(task)
    suspend fun deleteTask(task: Task) = dao.deleteTask(task)
    suspend fun getTaskById(id: Long): Task? = dao.getTaskById(id)

    suspend fun addFocusSession(session: FocusSession) = dao.insertFocusSession(session)
    suspend fun addJournalEntry(entry: JournalEntry) = dao.insertJournalEntry(entry)

    suspend fun addContact(contact: TrustedContact): Long = dao.insertContact(contact)
    suspend fun updateContact(contact: TrustedContact) = dao.updateContact(contact)
    suspend fun deleteContact(contact: TrustedContact) = dao.deleteContact(contact)

    suspend fun addSupportRequest(request: SupportRequest) = dao.insertSupportRequest(request)

    suspend fun addChatMessage(message: ChatMessage): Long = dao.insertChatMessage(message)
    suspend fun clearChat() = dao.clearChatMessages()

    suspend fun updateWearableMetrics(metrics: WearableMetrics) = dao.insertWearableMetrics(metrics)

    suspend fun resetDemoData() {
        SaathDatabase.populateDatabase(dao)
    }
}
