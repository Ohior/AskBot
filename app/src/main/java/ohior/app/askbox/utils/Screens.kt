package ohior.app.askbox.utils

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object AskHome : Screens()

    @Serializable
    data object ChatBot : Screens()
}