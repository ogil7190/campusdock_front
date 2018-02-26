package com.orah.meetesh.campusdock.Utils;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.orah.meetesh.campusdock.Classes.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM  event")
    List<Event> getAllEvents();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Event user);

    @Update
    void update(Event user);

    @Delete
    void delete(Event user);
}
