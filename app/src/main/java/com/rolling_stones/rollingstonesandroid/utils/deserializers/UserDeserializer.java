package com.rolling_stones.rollingstonesandroid.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.rolling_stones.rollingstonesandroid.api.SerializedNames;
import com.rolling_stones.rollingstonesandroid.models.Gender;
import com.rolling_stones.rollingstonesandroid.models.User;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;

import java.lang.reflect.Type;


public class UserDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        try {
            final JsonObject user = json.getAsJsonObject();

            return new User(user.get(SerializedNames.UserBase.ID).getAsInt(),
                    user.get(SerializedNames.UserBase.FIRST_NAME).getAsString(),
                    user.get(SerializedNames.UserBase.LAST_NAME).getAsString(),
                    user.get(SerializedNames.UserBase.LOGIN).getAsString(),
                    user.get(SerializedNames.UserBase.GENDER).getAsString().equals(Gender.MALE.getGender()) ?
                            Gender.MALE : Gender.FEMALE,
                    DateFormatter.parseString(user.get(SerializedNames.UserBase.DATE_OF_BIRTH).getAsString(),
                            DateFormatter.DATE_FORMAT_PATTERN),
                    user.get(SerializedNames.UserBase.RECEIVE_ONLY_FRIENDS_MESSAGES).getAsBoolean(),
                    user.get(SerializedNames.UserBase.PHOTO_URL).getAsString(),
                    user.get(SerializedNames.UserBase.PHOTO_SMALL_URL).getAsString(),
                    user.get(SerializedNames.UserBase.IS_DELETED).getAsBoolean(),
                    user.get(SerializedNames.User.PASSWORD).getAsString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
