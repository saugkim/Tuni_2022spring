package org.tuni.taskukirja;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 2, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    private static volatile BookDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static BookDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BookDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), BookDatabase.class, "taskukirja")
                            .addCallback(startRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback startRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // If you want to keep data through app restarts, comment out the following block
            databaseWriteExecutor.execute(() -> {
                BookDao dao = INSTANCE.bookDao();
                dao.deleteAll();

                Book book = new Book(1,"Mikki kiipelissä", "1970", "254", "12.12.2000");
                dao.insert(book);
            });
        }
    };
}
