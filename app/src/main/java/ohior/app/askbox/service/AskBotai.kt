package ohior.app.askbox.service

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig

object AskBotAI {
    private val queryGenerativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = ohior.app.askbox.BuildConfig.GEMINI_API_KEY,
    )
    private val chatGenerativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = ohior.app.askbox.BuildConfig.GEMINI_API_KEY,
        generationConfig =  generationConfig {
            temperature = 0.9f
            topK = 16
            topP = 0.1f
            maxOutputTokens = 200
            stopSequences = listOf("red")
        }
    )

    suspend fun getQueryFromAI(query: String): GenerateContentResponse {
        return queryGenerativeModel.generateContent(query)
    }


    fun initializeChat(history: List<Content>): Chat {
        return chatGenerativeModel.startChat(history = history)
    }

    suspend fun sendMessage(query: String, chat: Chat): GenerateContentResponse {
        return chat.sendMessage(query)
    }
}