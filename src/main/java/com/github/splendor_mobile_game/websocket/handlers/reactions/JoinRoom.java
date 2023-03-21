package com.github.splendor_mobile_game.websocket.handlers.reactions;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.handlers.DataClass;
import com.github.splendor_mobile_game.websocket.handlers.Messenger;
import com.github.splendor_mobile_game.websocket.handlers.Reaction;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.InvalidUUIDException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomDoesntExistException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.RoomFullException;
import com.github.splendor_mobile_game.websocket.handlers.exceptions.UserAlreadyInRoomException;
import com.github.splendor_mobile_game.websocket.response.ErrorResponse;
import com.github.splendor_mobile_game.websocket.response.ResponseType;
import com.github.splendor_mobile_game.websocket.response.Result;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinRoom extends Reaction {

    public JoinRoom(int connectionHashCode) {
        super(connectionHashCode);
    }


    private class RoomDTO {
        public UUID uuid;
    }

    private class UserDTO {
        public UUID uuid;
        public String name;
    }


    @DataClass
    private class DataDTO {

        private UserDTO userDTO;
        private RoomDTO roomDTO;

    }


    /* ----> EXAMPLE USER REQUEST <----
    {
         "messageContextId": "80bdc250-5365-4caf-8dd9-a33e709a0116",
         "type": "JoinRoom",
         "data": {
             "userDTO": {
                 "uuid": "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3456",
                 "name": "Jacuch"
             },
             "roomDTO": {
                 "uuid": "494fdfda-aa14-42f3-9569-4cde39b1f63b"
             }
         }
     }
     */

    @Override
    public void react(ReceivedMessage parsedMessage, Messenger messenger, Database database) {

        DataDTO receivedMessage = (DataDTO) parsedMessage.getData();

        try {

            validateData(receivedMessage, database);

            Room room = database.getRoom(receivedMessage.roomDTO.uuid);
            User user = new User(receivedMessage.userDTO.uuid, receivedMessage.userDTO.name, this.connectionHashCode);
            database.addUser(user);
            room.joinGame(user);

            JsonObject userJson = new JsonObject();
            userJson.addProperty("uuid", receivedMessage.userDTO.uuid.toString());
            userJson.addProperty("name", user.getName());

            JsonObject roomJson = new JsonObject();
            roomJson.addProperty("uuid", receivedMessage.roomDTO.uuid.toString());

            JsonObject data = new JsonObject();
            data.add("user", userJson);
            data.add("room", roomJson);

            JsonObject response = new JsonObject();
            response.addProperty("messageContextId", parsedMessage.getMessageContextId());
            response.addProperty("type", ResponseType.JOIN_ROOM_RESPONSE.toString());
            response.addProperty("result", Result.OK.toString());
            response.add("data", data);


            // Send join information to other players
            for (User u : room.getAllUsers()) {
                messenger.addMessageToSend(u.getConnectionHasCode(), (new Gson()).toJson(response));
            }


        } catch(Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(Result.FAILURE,e.getMessage(), ResponseType.JOIN_ROOM_RESPONSE, parsedMessage.getMessageContextId());
            messenger.addMessageToSend(connectionHashCode, errorResponse.ToJson());

        }

    }


    private void validateData(DataDTO dataDTO, Database database) throws InvalidUUIDException, RoomDoesntExistException, UserAlreadyInRoomException, RoomFullException {
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        // Check if user UUID matches the pattern
        Matcher uuidMatcher = uuidPattern.matcher(dataDTO.userDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format."); // Check if user UUID matches the pattern


        // Check if room UUID matches the pattern
        uuidMatcher = uuidPattern.matcher(dataDTO.roomDTO.uuid.toString());
        if (!uuidMatcher.find())
            throw new InvalidUUIDException("Invalid UUID format.");


        // Check if room exists
        Room room = database.getRoom(dataDTO.roomDTO.uuid);
        if (room == null)
            throw new RoomDoesntExistException("Could not find a room with specified UUID.");


        // Check players count reached maximum number
        if (room.getPlayerCount() == 4)
            throw new RoomFullException("Room has already reached maximum player count!");


        // Check if user is already a member of any room
        User user = database.getUser(dataDTO.userDTO.uuid);
        if (user != null) {
            for (Room r : database.getAllRooms())
                if (r.getAllUsers().contains(user))
                    throw new UserAlreadyInRoomException("Leave your current room before joining another.");
        }
    }


}