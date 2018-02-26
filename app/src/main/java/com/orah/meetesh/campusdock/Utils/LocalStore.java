package com.orah.meetesh.campusdock.Utils;

import java.util.HashMap;

/**
 * Created by ogil7190 on 19/02/18.
 */

public class LocalStore {
    private static HashMap<String, Object> store = new HashMap<>();

    public static void putObject(String key, Object object){
        store.put(key, object);
    }

    public static Object getObject(String key){
        return store.get(key);
    }
}
