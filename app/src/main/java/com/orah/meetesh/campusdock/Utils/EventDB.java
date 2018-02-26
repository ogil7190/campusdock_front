package com.orah.meetesh.campusdock.Utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.orah.meetesh.campusdock.Classes.Event;

/**
 * Created by minion on 26/02/2018 AD.
 */
@Database(entities = Event.class,version = 1)
public abstract  class EventDB extends RoomDatabase{
    private static EventDB instance;

    public static EventDB getIntsance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, EventDB.class,"Ogil")
                    .allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract EventDao getEventDao();
}
