package com.example.natour21.chat.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.natour21.chat.room.DAOs.ChatDAO;
import com.example.natour21.chat.room.DAOs.MessaggioDAO;
import com.example.natour21.chat.room.entities.ChatDBEntity;
import com.example.natour21.chat.room.entities.MessaggioDBEntity;
import com.example.natour21.chat.room.typeconverters.OffsetDateTimeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ChatDBEntity.class, MessaggioDBEntity.class}, version = 1, exportSchema = false)
@TypeConverters({OffsetDateTimeConverter.class})
public abstract class ChatDatabase extends RoomDatabase {

    public abstract ChatDAO chatDAO();
    public abstract MessaggioDAO messaggioDAO();

    private static volatile ChatDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static final  String databaseName = "chat_database";

    public static ChatDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChatDatabase.class, "chat_database")
                            .addCallback(CALLBACK)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback CALLBACK =  new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            db.execSQL("CREATE TRIGGER IF NOT EXISTS CHAT_MESSAGE_COUNT_INCREMENT " +
                    "AFTER INSERT ON Messaggio " +
                    "BEGIN " +
                    "UPDATE Chat " +
                        "SET messageCount = messageCount + 1, " +
                        "unreadMessageCount = " +
                        "CASE " +
                            "WHEN NEW.isMainUtenteSender = false THEN unreadMessageCount + 1 " +
                            "ELSE unreadMessageCount " +
                        "END " +
                    "WHERE otherUserId = NEW.otherUserId; " +
                    "END;");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    public static ExecutorService getExecutorService(){
        return executorService;
    }

    public static String getDatabaseName() { return databaseName;}


}
