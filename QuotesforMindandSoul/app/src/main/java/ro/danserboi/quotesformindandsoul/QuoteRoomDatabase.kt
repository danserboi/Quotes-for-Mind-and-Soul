package ro.danserboi.quotesformindandsoul

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Quote::class], version = 1, exportSchema = true)
abstract class QuoteRoomDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao?

    companion object {
        private var INSTANCE: QuoteRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): QuoteRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(QuoteRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                QuoteRoomDatabase::class.java, "quote_database")
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}