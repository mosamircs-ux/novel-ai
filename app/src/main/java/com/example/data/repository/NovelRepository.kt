package com.example.data.repository

import com.example.data.dao.NovelDao
import com.example.data.dao.CharacterDao
import com.example.data.dao.EventDao
import com.example.data.dao.DialogueDao
import com.example.data.model.Novel
import com.example.data.model.Character
import com.example.data.model.Event
import com.example.data.model.Dialogue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import android.util.Log

class NovelRepository(
    private val novelDao: NovelDao,
    private val characterDao: CharacterDao,
    private val eventDao: EventDao,
    private val dialogueDao: DialogueDao
) {
    val allNovels: Flow<List<Novel>> = novelDao.getAllNovels()

    fun getNovelByIdFlow(id: Int): Flow<Novel?> = novelDao.getNovelByIdFlow(id)

    suspend fun getNovelById(id: Int): Novel? = novelDao.getNovelById(id)

    fun getCharactersForNovel(novelId: Int): Flow<List<Character>> =
        characterDao.getCharactersForNovel(novelId)

    fun getEventsForNovel(novelId: Int): Flow<List<Event>> =
        eventDao.getEventsForNovel(novelId)

    fun getDialoguesForEvent(eventId: Int): Flow<List<Dialogue>> =
        dialogueDao.getDialoguesForEvent(eventId)

    fun getDialoguesForNovel(novelId: Int): Flow<List<Dialogue>> =
        dialogueDao.getDialoguesForNovel(novelId)

    suspend fun insertNovel(novel: Novel): Int {
        return novelDao.insertNovel(novel).toInt()
    }

    suspend fun updateNovel(novel: Novel) {
        novelDao.updateNovel(novel)
    }

    suspend fun deleteNovel(novel: Novel) {
        novelDao.deleteNovel(novel)
    }

    suspend fun insertCharacters(characters: List<Character>) {
        characterDao.insertCharacters(characters)
    }

    suspend fun insertCharacter(character: Character): Int {
        return characterDao.insertCharacter(character).toInt()
    }

    suspend fun updateCharacter(character: Character) {
        characterDao.updateCharacter(character)
    }

    suspend fun insertEvents(events: List<Event>) {
        eventDao.insertEvents(events)
    }

    suspend fun insertEvent(event: Event): Int {
        return eventDao.insertEvent(event).toInt()
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun insertDialogues(dialogues: List<Dialogue>) {
        dialogueDao.insertDialogues(dialogues)
    }

    // Seed data function to prepopulate classic starter novel
    suspend fun seedMockDataIfEmpty() {
        val currentNovels = allNovels.first()
        if (currentNovels.isEmpty()) {
            Log.d("NovelRepository", "Database is empty. Seeding starter novel...")

            // 1. Core Novel Entry
            val novelId = insertNovel(
                Novel(
                    title = "Sherlock Holmes: The Blue Carbuncle",
                    author = "Sir Arthur Conan Doyle",
                    textContent = """
                        I called upon my friend Sherlock Holmes upon the second morning after Christmas, with the intention of wishing him the compliments of the season. He was lounging upon the sofa in a purple dressing-gown, a pipe-rack within his reach upon the right, and a pile of crumpled morning papers, evidently newly studied, near at hand. Beside the couch was a wooden chair, and on the angle of the back hung a very seedy and disreputable hard-felt hat, much the worse for wear, and cracked in several places. A lens and a forceps lying upon the seat of the chair suggested that the hat had been suspended in this manner for the purpose of examination.
                    """.trimIndent(),
                    coverImage = "https://images.unsplash.com/photo-1513001900722-370f803f498d?w=600",
                    status = "analyzed",
                    movieStatus = "ready"
                )
            )

            // 2. Core Character Entries
            val characterList = listOf(
                Character(
                    novelId = novelId,
                    name = "Sherlock Holmes",
                    aliases = "Holmes, Mr. Holmes, Detective Holmes",
                    role = "protagonist",
                    personality = "Brilliant, highly analytical, observant, calm, slightly dramatic, eccentric.",
                    physicalDescription = "Tall, lean, sharp gaze, hawkish nose, wearing a purple dressing gown or classic tweed coat.",
                    speakingStyle = "Precise, baritone, rapid, intellectual, direct.",
                    imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                    relationshipsJson = """{"Dr. John Watson": "Loyal partner and friend", "Peterson": "Acquaintance Commissioner"}"""
                ),
                Character(
                    novelId = novelId,
                    name = "Dr. John Watson",
                    aliases = "Watson, John, Dr. Watson",
                    role = "supporting",
                    personality = "Loyal, courageous, sensible, practical, inquisitive but grounded.",
                    physicalDescription = "Sturdy build, mustache, wearing a classic warm winter coat, bowler hat.",
                    speakingStyle = "Warm, articulate, respectful, slightly slower, inquiring.",
                    imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                    relationshipsJson = """{"Sherlock Holmes": "Brilliant close friend and flatmate"}"""
                ),
                Character(
                    novelId = novelId,
                    name = "Peterson",
                    aliases = "Peterson the Commissioner, Commissionaire",
                    role = "supporting",
                    personality = "Honest, simple, duty-bound, easily astonished.",
                    physicalDescription = "Broad-shouldered, official blue uniform, wearing a commissioner hat.",
                    speakingStyle = "Excited, direct, humble, breathless.",
                    imageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300",
                    relationshipsJson = """{"Sherlock Holmes": "Asks for investigative assistance"}"""
                )
            )
            insertCharacters(characterList)

            // Fetch newly generated database ids to associate dialogues properly
            val dbCharacters = characterDao.getCharactersForNovelList(novelId)
            val holmesId = dbCharacters.find { it.name == "Sherlock Holmes" }?.id
            val watsonId = dbCharacters.find { it.name == "Dr. John Watson" }?.id
            val petersonId = dbCharacters.find { it.name == "Peterson" }?.id

            // 3. Core Event Entries
            val event1Id = insertEvent(
                Event(
                    novelId = novelId,
                    title = "The Christmas Morning Visit",
                    description = "Watson enters Baker Street on a chilly morning to find Holmes examining a battered felt hat hung on a chair with a lens.",
                    visualDescription = "A warm, fire-lit study inside 221B Baker Street filled with books, maps, and pipes. Holmes lounging on a green armchair with a dark purple dressing-gown, looking through a magnifying lens at a worn-out brown hat.",
                    location = "221B Baker Street - Living Room",
                    emotionalTone = "mysterious",
                    atmosphere = "Cozy, curious, academic",
                    chapter = 1,
                    sequence = 1,
                    imageUrl = "https://images.unsplash.com/photo-1544644181-1484b3fdfc62?w=600"
                )
            )

            val event2Id = insertEvent(
                Event(
                    novelId = novelId,
                    title = "The Secret of Peterson's Goose",
                    description = "Peterson rushes into the room carrying a dazzling blue gemstone, describing how it was found in the crop of a Christmas goose.",
                    visualDescription = "Holmes, Watson, and Peterson crowding around a dark mahogany table. Under a brass reading lamp, Holmes holds up a brilliant, glittering ocean-blue gemstone reflecting thousands of points of light in his palm.",
                    location = "221B Baker Street - Study",
                    emotionalTone = "tense",
                    atmosphere = "Dazzling, electrifying, urgent",
                    chapter = 1,
                    sequence = 2,
                    imageUrl = "https://images.unsplash.com/photo-1515688594390-b649af70d282?w=600"
                )
            )

            // 4. Core Dialogue Entries
            val dialoguesEvent1 = listOf(
                Dialogue(
                    eventId = event1Id,
                    characterId = watsonId,
                    text = "Good morning, Holmes! Examining that battered felt hat, I see. What mystery lies behind it?",
                    speakerName = "Dr. John Watson",
                    addressedTo = "Sherlock Holmes",
                    sequence = 1
                ),
                Dialogue(
                    eventId = event1Id,
                    characterId = holmesId,
                    text = "Ah, Watson! Yes, this hat is an instructive lesson in observation. It represents a very unique case indeed.",
                    speakerName = "Sherlock Holmes",
                    addressedTo = "Dr. John Watson",
                    sequence = 2
                ),
                Dialogue(
                    eventId = event1Id,
                    characterId = watsonId,
                    text = "A lesson? It looks quite ordinary and exceptionally dirty to me.",
                    speakerName = "Dr. John Watson",
                    addressedTo = "Sherlock Holmes",
                    sequence = 3
                ),
                Dialogue(
                    eventId = event1Id,
                    characterId = holmesId,
                    text = "To you, it is dirty. To me, it highlights the owner is highly intellectual, middle-aged, and has gone through a severe decline in fortunes recently.",
                    speakerName = "Sherlock Holmes",
                    addressedTo = "Dr. John Watson",
                    sequence = 4
                )
            )
            insertDialogues(dialoguesEvent1)

            val dialoguesEvent2 = listOf(
                Dialogue(
                    eventId = event2Id,
                    characterId = petersonId,
                    text = "Mr. Holmes! Look at what my wife found in the crop of the goose! A blue stone, sir!",
                    speakerName = "Peterson",
                    addressedTo = "Sherlock Holmes",
                    sequence = 1
                ),
                Dialogue(
                    eventId = event2Id,
                    characterId = holmesId,
                    text = "By Jove! Peterson! Is this... Watson, look closely. Can it be the Countess of Morcar’s blue carbuncle?",
                    speakerName = "Sherlock Holmes",
                    addressedTo = "Dr. John Watson",
                    sequence = 2
                ),
                Dialogue(
                    eventId = event2Id,
                    characterId = watsonId,
                    text = "Good heavens, Holmes! The precious jewel that vanished last week at the Hotel Cosmopolitan? The reward is one thousand pounds!",
                    speakerName = "Dr. John Watson",
                    addressedTo = "Sherlock Holmes",
                    sequence = 3
                )
            )
            insertDialogues(dialoguesEvent2)
        }
    }
}
