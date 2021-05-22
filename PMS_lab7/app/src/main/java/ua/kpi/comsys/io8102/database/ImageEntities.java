package ua.kpi.comsys.io8102.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image")
public class ImageEntities {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String url, fileName;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }
}