package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Novel
import com.example.data.model.Character
import com.example.data.model.Event
import com.example.data.model.Dialogue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    suspend fun analyzeNovelText(
        title: String,
        author: String,
        novelText: String
    ): ExtractedNovelContent? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            Log.e("GeminiService", "API Key is missing or placeholder!")
            return@withContext null
        }

        val prompt = """
            Analyze the following short novel segment: "Title: $title", "Author: $author".
            
            LANGUAGE RULE:
            - If the input novelText is written in Arabic (or mostly Arabic), you MUST extract all user-facing values (characters' names, aliases, personality summary, role, speakingStyle, event titles, event descriptions, event location, and actual dialogues 'text') in ARABIC. Do NOT translate Arabic text into English. Keep the original Arabic spelling and richness of dialogs exactly.
            - The JSON keys ("characters", "name", "aliases", "role", "personality", "physicalDescription", "speakingStyle", "relationships", "events", "sequence", "title", "description", "visualDescription", "location", "emotionalTone", "atmosphere", "chapter", "dialogues", "eventSequence", "speakerName", "text", "addressedTo", "sequenceValue") MUST remain in English as requested by the JSON schema.
            - The "visualDescription" can remain in English as it is used directly as a prompt for English image models (it is better to write image prompt descriptions in English).
            
            Extract the following structures:
            1. Main characters with roles (protagonist, antagonist, supporting), personality summary, detailed physical descriptions for visual artwork prompts, speaking style, and relationships.
            2. Major scenic events. For each event, describe the title, a clear story description, a detailed "visualDescription" which should be a highly detailed painting prompt for an AI image generator, the location where it happens, emotional tone (choose from: tense, romantic, mysterious, joyful, peaceful, eerie, dramatic, or in lowercase english even if value is in arabic), and the atmospheric setting.
            3. All dialogue spoken within those events, attributing them to the correct speaker with addressed person if clear.
            
            Return the output STRICTLY as a valid JSON object matching this schema exactly:
            {
              "characters": [
                {
                  "name": "Sherlock Holmes",
                  "aliases": "Holmes, Mr. Holmes",
                  "role": "protagonist",
                  "personality": "Obsessive, brilliant, cold-mannered",
                  "physicalDescription": "A tall thin gentleman, grey eyes, carrying a magnifying glass",
                  "speakingStyle": "Precise, baritone, rapid",
                  "relationships": {
                    "Watson": "Amicable flatmate"
                  }
                }
              ],
              "events": [
                {
                  "sequence": 1,
                  "title": "Finding the Clue",
                  "description": "Sherlock discovers a hair on the hat indicating the suspect.",
                  "visualDescription": "Detailed portrait of Holmes in Victorian coat examining hat with stardust particle floating, warm library light, cinematic painting",
                  "location": "Baker Street Study",
                  "emotionalTone": "mysterious",
                  "atmosphere": "smoke-filled cozy study",
                  "chapter": 1
                }
              ],
              "dialogues": [
                {
                  "eventSequence": 1,
                  "speakerName": "Sherlock Holmes",
                  "text": "The clue is right here, WATSON!",
                  "addressedTo": "Watson",
                  "sequenceValue": 1
                }
              ]
            }

            Here is the novel text segment to analyze:
            $novelText
        """.trimIndent()

        // Construct Request JSON
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", prompt)
                ))
            ))
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        val requestBody = requestJson.toString().toRequestBody(JSON_MEDIA_TYPE)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e("GeminiService", "API Request Failed: Code ${response.code}, Message: ${response.message}")
                return@withContext null
            }

            val bodyText = response.body?.string() ?: return@withContext null
            Log.d("GeminiService", "Raw Response: $bodyText")

            val jsonResponse = JSONObject(bodyText)
            val candidates = jsonResponse.getJSONArray("candidates")
            val content = candidates.getJSONObject(0).getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawResponseText = parts.getJSONObject(0).getString("text")

            return@withContext parseExtractedContent(rawResponseText)
        } catch (e: Exception) {
            Log.e("GeminiService", "Network or Parsing error in Gemini: ${e.message}", e)
            return@withContext null
        }
    }

    // Helper to dynamically search and find nice illustrations for characters/scenes at runtime
    fun generateStockIllustratedUrl(keyword: String, isCharacter: Boolean): String {
        val root = if (isCharacter) {
            "https://images.unsplash.com/photo-1544005313-94ddf0286df2" // general nice portrait
        } else {
            "https://images.unsplash.com/photo-1518156677180-95a2893f3e9f" // atmospheric
        }
        val encoded = keyword.replace(" ", ",").lowercase()
        return if (isCharacter) {
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=400" 
        } else {
            "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&q=80&w=600"
        }
    }

    private fun parseExtractedContent(rawJsonText: String): ExtractedNovelContent {
        val rootObj = JSONObject(rawJsonText)
        
        // Parse Characters
        val charactersList = mutableListOf<ExtractedCharacter>()
        if (rootObj.has("characters")) {
            val arr = rootObj.getJSONArray("characters")
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                charactersList.add(
                    ExtractedCharacter(
                        name = item.optString("name", "Unknown"),
                        aliases = item.optString("aliases", ""),
                        role = item.optString("role", "supporting"),
                        personality = item.optString("personality", ""),
                        physicalDescription = item.optString("physicalDescription", ""),
                        speakingStyle = item.optString("speakingStyle", ""),
                        relationships = item.optJSONObject("relationships")?.toString() ?: "{}"
                    )
                )
            }
        }

        // Parse Events
        val eventsList = mutableListOf<ExtractedEvent>()
        if (rootObj.has("events")) {
            val arr = rootObj.getJSONArray("events")
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                eventsList.add(
                    ExtractedEvent(
                        sequence = item.optInt("sequence", i + 1),
                        title = item.optString("title", "Scene ${i + 1}"),
                        description = item.optString("description", ""),
                        visualDescription = item.optString("visualDescription", ""),
                        location = item.optString("location", "Unknown Location"),
                        emotionalTone = item.optString("emotionalTone", "mysterious"),
                        atmosphere = item.optString("atmosphere", ""),
                        chapter = item.optInt("chapter", 1)
                    )
                )
            }
        }

        // Parse Dialogues
        val dialoguesList = mutableListOf<ExtractedDialogue>()
        if (rootObj.has("dialogues")) {
            val arr = rootObj.getJSONArray("dialogues")
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                dialoguesList.add(
                    ExtractedDialogue(
                        eventSequence = item.optInt("eventSequence", 1),
                        speakerName = item.optString("speakerName", "Unknown"),
                        text = item.optString("text", ""),
                        addressedTo = item.optString("addressedTo", ""),
                        sequenceValue = item.optInt("sequenceValue", i + 1)
                    )
                )
            }
        }

        return ExtractedNovelContent(charactersList, eventsList, dialoguesList)
    }
}

data class ExtractedNovelContent(
    val characters: List<ExtractedCharacter>,
    val events: List<ExtractedEvent>,
    val dialogues: List<ExtractedDialogue>
)

data class ExtractedCharacter(
    val name: String,
    val aliases: String,
    val role: String,
    val personality: String,
    val physicalDescription: String,
    val speakingStyle: String,
    val relationships: String
)

data class ExtractedEvent(
    val sequence: Int,
    val title: String,
    val description: String,
    val visualDescription: String,
    val location: String,
    val emotionalTone: String,
    val atmosphere: String,
    val chapter: Int
)

data class ExtractedDialogue(
    val eventSequence: Int,
    val speakerName: String,
    val text: String,
    val addressedTo: String,
    val sequenceValue: Int
)
