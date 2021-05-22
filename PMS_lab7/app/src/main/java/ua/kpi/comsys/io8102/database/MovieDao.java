package ua.kpi.comsys.io8102.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<MovieEntities> getAll();

    @Query("SELECT * FROM movie WHERE SearchRequest = :searchRequest")
    List<MovieEntities> getByRequest(String searchRequest);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieEntities getById(long id);

    @Insert
    void insert(MovieEntities film);

    @Update
    void update(MovieEntities film);

    @Delete
    void delete(MovieEntities film);
}

