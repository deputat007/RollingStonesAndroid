package com.rolling_stones.rollingstonesandroid.utils.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rolling_stones.rollingstonesandroid.api.SerializedNames;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;

import java.lang.reflect.Type;


public class MessageSerializer implements JsonSerializer<Message> {

    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty(SerializedNames.Message.ID, src.getId());
        jsonElement.addProperty(SerializedNames.Message.TEXT, src.getText());
        jsonElement.add(SerializedNames.Message.RECIPIENT,
                new UserBaseSerializer().serialize(src.getRecipient(), UserBase.class, context));

        if (src.getGroup() != null) {
            jsonElement.add(SerializedNames.Message.GROUP,
                    new GroupSerializer().serialize(src.getGroup(), Group.class, context));
        } else {
            jsonElement.add(SerializedNames.Message.GROUP, JsonNull.INSTANCE);
        }

        jsonElement.addProperty(SerializedNames.Message.DATE_AND_TIME,
                DateFormatter.parseDate(src.getDateAndTime(), DateFormatter.DEFAULT_PATTERN_WITH_TIME));
        jsonElement.add(SerializedNames.Message.SENDER,
                new UserBaseSerializer().serialize(src.getSender(), UserBase.class, context));
        return jsonElement;
    }
}
