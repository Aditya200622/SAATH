package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SaathDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 'primary_user'")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 'primary_user'")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    // Saathi Config
    @Query("SELECT * FROM saathi_config WHERE id = 'primary_saathi'")
    fun getSaathiConfigFlow(): Flow<SaathiConfig?>

    @Query("SELECT * FROM saathi_config WHERE id = 'primary_saathi'")
    suspend fun getSaathiConfig(): SaathiConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaathiConfig(config: SaathiConfig)

    // Check-ins
    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    fun getAllCheckInsFlow(): Flow<List<CheckIn>>

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCheckInFlow(): Flow<CheckIn?>

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestCheckIn(): CheckIn?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckIn): Long

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Focus Sessions
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllFocusSessionsFlow(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession)

    // Journal Entries
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntriesFlow(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    // Trusted Contacts
    @Query("SELECT * FROM trusted_contacts ORDER BY id ASC")
    fun getAllContactsFlow(): Flow<List<TrustedContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: TrustedContact): Long

    @Update
    suspend fun updateContact(contact: TrustedContact)

    @Delete
    suspend fun deleteContact(contact: TrustedContact)

    // Support Requests
    @Query("SELECT * FROM support_requests ORDER BY timestamp DESC")
    fun getAllSupportRequestsFlow(): Flow<List<SupportRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportRequest(request: SupportRequest)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // Wearable Metrics
    @Query("SELECT * FROM wearable_metrics WHERE id = 'current'")
    fun getWearableMetricsFlow(): Flow<WearableMetrics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWearableMetrics(metrics: WearableMetrics)
}
