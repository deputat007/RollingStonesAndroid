package com.rolling_stones.rollingstonesandroid.utils.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rolling_stones.rollingstonesandroid.api.SerializedNames;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;

import java.lang.reflect.Type;


public class UserBaseSerializer implements JsonSerializer<UserBase> {

    @Override
    public JsonElement serialize(UserBase src, Type typeOfSrc, JsonSerializationContext context) {
        if (src != null) {
            final JsonObject jsonElement = new JsonObject();

            jsonElement.addProperty(SerializedNames.UserBase.ID, src.getId());
            jsonElement.addProperty(SerializedNames.UserBase.FIRST_NAME, src.getFirstName());
            jsonElement.addProperty(SerializedNames.UserBase.LAST_NAME, src.getLastName());
            jsonElement.addProperty(SerializedNames.UserBase.DATE_OF_BIRTH,
                    DateFormatter.parseDate(src.getDateOfBirth(), DateFormatter.DATE_FORMAT_PATTERN));
            jsonElement.addProperty(SerializedNames.UserBase.LOGIN, src.getLogin());
            jsonElement.addProperty(SerializedNames.UserBase.GENDER,
                    (src.getGender() == null ? null : src.getGender().getGender()));
            jsonElement.addProperty(SerializedNames.UserBase.RECEIVE_ONLY_FRIENDS_MESSAGES,
                    src.isReceiveOnlyFriendsMessages());

            if (src.getPhotoUrl() != null) {
                jsonElement.addProperty(SerializedNames.UserBase.PHOTO_URL, src.getPhotoUrl());
            }

            if (src.getPhotoSmallUrl() != null) {
                jsonElement.addProperty(SerializedNames.UserBase.PHOTO_SMALL_URL, src.getPhotoSmallUrl());
            }

            jsonElement.addProperty(SerializedNames.UserBase.IS_DELETED, src.isDeleted());
            jsonElement.addProperty(SerializedNames.UserBase.IS_ONLINE, src.isOnline());

            return jsonElement;
        }

        return JsonNull.INSTANCE;
    }
}
