package com.rolling_stones.rollingstonesandroid.utils.deserializers;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.rolling_stones.rollingstonesandroid.api.SerializedNames;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException {
        try {
            final JsonObject jsonObject = json.getAsJsonObject();

            final Message message = new Message(jsonObject.get(SerializedNames.Message.ID) != null ?
                    jsonObject.get(SerializedNames.Message.ID).getAsInt() : 0,
                    jsonObject.get(SerializedNames.Message.TEXT).getAsString(),
                    new UserBaseDeserializer().deserialize(jsonObject.get(SerializedNames.Message.SENDER),
                            UserBase.class, context), null,
                    DateFormatter.parseString(jsonObject.get(SerializedNames.Message.DATE_AND_TIME).getAsString(),
                            DateFormatter.DEFAULT_PATTERN_WITH_TIME), null);

            if (jsonObject.get(SerializedNames.Message.GROUP) != null &&
                    !jsonObject.get(SerializedNames.Message.GROUP).isJsonNull()) {
                final JsonObject group = jsonObject.get(SerializedNames.Message.GROUP).getAsJsonObject();

                message.setGroup(new GroupDeserializer().deserialize(group, Group.class, context));
            }

            if (jsonObject.get(SerializedNames.Message.RECIPIENT) != null &&
                    !jsonObject.get(SerializedNames.Message.RECIPIENT).isJsonNull()) {
                final JsonElement recipient = jsonObject.get(SerializedNames.Message.RECIPIENT);

                message.setRecipient(
                        new UserBaseDeserializer().deserialize(recipient, UserBase.class, context));
            }
            return message;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
