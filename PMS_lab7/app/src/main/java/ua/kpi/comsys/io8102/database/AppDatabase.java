package ua.kpi.comsys.io8102.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MovieEntities.class, PosterEntities.class, ImageEntities.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
    public abstract PosterDao posterDao();
    public abstract ImageDao imageDao();
}
