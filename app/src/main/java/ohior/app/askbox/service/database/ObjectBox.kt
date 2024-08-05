package ohior.app.askbox.service.database

import android.content.Context
import io.objectbox.BoxStore
import ohior.app.askbox.model.MyObjectBox

object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context)
            .build()
    }
}