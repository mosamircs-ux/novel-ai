package com.example.data.dao

import androidx.room.*
import com.example.data.model.Novel
import com.example.data.model.Character
import com.example.data.model.Event
import com.example.data.model.Dialogue
import kotlinx.coroutines.flow.Flow

@Dao
interface NovelDao {
    @Query("SELECT * FROM novels ORDER BY createdAt DESC")
    fun getAllNovels(): Flow<List<Novel>>

    @Query("SELECT * FROM novels WHERE id = :id LIMIT 1")
    suspend fun getNovelById(id: Int): Novel?

    @Query("SELECT * FROM novels WHERE id = :id LIMIT 1")
    fun getNovelByIdFlow(id: Int): Flow<Novel?>

    @Query("SELECT * FROM novels WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteNovels(): Flow<List<Novel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovel(novel: Novel): Long

    @Update
    suspend fun updateNovel(novel: Novel)

    @Delete
    suspend fun deleteNovel(novel: Novel)
}

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE novelId = :novelId ORDER BY role ASC, name ASC")
    fun getCharactersForNovel(novelId: Int): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE novelId = :novelId")
    suspend fun getCharactersForNovelList(novelId: Int): List<Character>

    @Query("SELECT * FROM characters WHERE id = :characterId LIMIT 1")
    suspend fun getCharacterById(characterId: Int): Character?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<Character>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character): Long

    @Update
    suspend fun updateCharacter(character: Character)

    @Query("UPDATE characters SET imageUrl = :imageUrl WHERE id = :characterId")
    suspend fun updateCharacterImage(characterId: Int, imageUrl: String)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE novelId = :novelId ORDER BY chapter ASC, sequence ASC")
    fun getEventsForNovel(novelId: Int): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isFavorite = 1 ORDER BY chapter ASC, sequence ASC")
    fun getAllFavoriteEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE novelId = :novelId AND isFavorite = 1 ORDER BY chapter ASC, sequence ASC")
    fun getFavoriteEventsForNovel(novelId: Int): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE novelId = :novelId ORDER BY chapter ASC, sequence ASC")
    suspend fun getEventsForNovelList(novelId: Int): List<Event>

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: Int): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Query("UPDATE events SET imageUrl = :imageUrl WHERE id = :eventId")
    suspend fun updateEventImage(eventId: Int, imageUrl: String)
}

@Dao
interface DialogueDao {
    @Query("SELECT * FROM dialogues WHERE eventId = :eventId ORDER BY sequence ASC")
    fun getDialoguesForEvent(eventId: Int): Flow<List<Dialogue>>

    @Query("SELECT * FROM dialogues WHERE eventId = :eventId ORDER BY sequence ASC")
    suspend fun getDialoguesForEventList(eventId: Int): List<Dialogue>

    @Query("SELECT d.* FROM dialogues d INNER JOIN events e ON d.eventId = e.id WHERE e.novelId = :novelId ORDER BY e.chapter ASC, e.sequence ASC, d.sequence ASC")
    fun getDialoguesForNovel(novelId: Int): Flow<List<Dialogue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDialogues(dialogues: List<Dialogue>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDialogue(dialogue: Dialogue): Long

    @Update
    suspend fun updateDialogue(dialogue: Dialogue)
}
