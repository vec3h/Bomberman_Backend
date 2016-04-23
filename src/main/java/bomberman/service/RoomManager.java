package bomberman.service;

import main.websocketconnection.MessageSendable;
import rest.UserProfile;

import java.util.List;

public interface RoomManager {
    Room assignUserToFreeRoom(UserProfile user, MessageSendable socket);
    void removeUserFromRoom(UserProfile user);
    List<Room> getAllRooms();
}
