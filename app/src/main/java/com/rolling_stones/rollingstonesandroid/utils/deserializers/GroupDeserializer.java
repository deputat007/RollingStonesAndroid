package com.rolling_stones.rollingstonesandroid.utils.deserializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.UserBase;

import java.lang.reflect.Type;


public class GroupDeserializer implements JsonDeserializer<Group> {

    @Override
    public Group deserialize(JsonElement json, Type typeOfT,
                             JsonDeserializationContext context) throws JsonParseException {
        try {
            final JsonObject group = json.getAsJsonObject();

            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(UserBase.class, new UserBaseDeserializer())
                    .create();

            return gson.fromJson(group, Group.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
