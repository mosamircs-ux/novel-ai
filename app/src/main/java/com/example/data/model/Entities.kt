package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "novels")
data class Novel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val textContent: String,
    val coverImage: String? = null,
    val status: String = "pending", // "pending", "processing", "analyzed", "failed"
    val movieUrl: String? = null,
    val movieStatus: String? = "idle", // "idle", "processing", "ready", "failed"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = Novel::class,
            parentColumns = ["id"],
            childColumns = ["novelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["novelId"])]
)
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val novelId: Int,
    val name: String,
    val aliases: String, // Comma-separated names
    val role: String, // "protagonist", "antagonist", "supporting"
    val personality: String,
    val physicalDescription: String,
    val speakingStyle: String,
    val imageUrl: String? = null,
    val relationshipsJson: String = "{}" // e.g. "{"Leila": "romantic interest"}"
)

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Novel::class,
            parentColumns = ["id"],
            childColumns = ["novelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["novelId"])]
)
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val novelId: Int,
    val title: String,
    val description: String,
    val visualDescription: String,
    val location: String,
    val emotionalTone: String, // "tense", "romantic", "mysterious", "joyful", etc.
    val atmosphere: String,
    val chapter: Int,
    val sequence: Int,
    val imageUrl: String? = null
)

@Entity(
    tableName = "dialogues",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class Dialogue(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val characterId: Int?, // Can be null if the speaker is narrator or unidentified
    val text: String,
    val speakerName: String,
    val addressedTo: String? = null,
    val audioUrl: String? = null, // Path to local generated TTS file
    val sequence: Int
)
