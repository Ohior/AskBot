package ohior.app.askbox.service

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse

object AskBotAI {
    private val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = ohior.app.askbox.BuildConfig.GEMINI_API_KEY,
    )

    suspend fun getQueryFromAI(query: String): GenerateContentResponse {
        return generativeModel.generateContent(query)
    }


    fun initializeChat(history: List<Content>): Chat {
        return generativeModel.startChat(history = history)
    }

    suspend fun sendMessage(query: String, chat: Chat): GenerateContentResponse {
        return chat.sendMessage(query)
    }
}