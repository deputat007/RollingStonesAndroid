package com.rolling_stones.rollingstonesandroid.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.rolling_stones.rollingstonesandroid.api.SerializedNames;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.models.UserBaseWithState;
import com.rolling_stones.rollingstonesandroid.models.UserState;

import java.lang.reflect.Type;


public class UserBaseWithStateDeserializer implements JsonDeserializer<UserBaseWithState> {

    @Override
    public UserBaseWithState deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context) throws JsonParseException {
        try {
            final JsonObject user = json.getAsJsonObject();
            final UserBase userBase = new UserBaseDeserializer().deserialize(
                    user.get(SerializedNames.UserBaseWithState.USER), UserBase.class, context);

            UserState state = null;
            int stateInt = user.get(SerializedNames.UserBaseWithState.STATE).getAsInt();

            for (UserState userState :
                    UserState.values()) {
                if (userState.getState() == stateInt) {
                    state = userState;
                }
            }
            return new UserBaseWithState(userBase, state);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
