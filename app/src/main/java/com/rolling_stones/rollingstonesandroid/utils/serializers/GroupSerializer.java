package com.rolling_stones.rollingstonesandroid.utils.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.UserBase;

import java.lang.reflect.Type;


public class GroupSerializer implements JsonSerializer<Group> {

    @Override
    public JsonElement serialize(Group src, Type typeOfSrc, JsonSerializationContext context) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(UserBase.class, new UserBaseSerializer())
                .create();

        return gson.toJsonTree(src, Group.class);
    }
}
