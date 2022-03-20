package ro.danserboi.quotesformindandsoul;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Quote.class}, version = 1)
public abstract class QuoteRoomDatabase extends RoomDatabase {

    public abstract QuoteDao quoteDao();

    private static QuoteRoomDatabase INSTANCE;

    static QuoteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuoteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QuoteRoomDatabase.class, "quote_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
