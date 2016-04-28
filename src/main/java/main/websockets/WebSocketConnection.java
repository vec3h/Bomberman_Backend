package main.websockets;

import bomberman.service.ReceivedMessageHandler;
import bomberman.service.Room;
import bomberman.service.RoomManager;
import main.config.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;
import rest.UserProfile;

import java.io.IOException;

@WebSocket
public class WebSocketConnection implements MessageSendable{

    public WebSocketConnection(UserProfile owner, Context globalContext) {
        user = owner;
        roomManager = (RoomManager) globalContext.get(RoomManager.class);
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        final JSONObject message;
        try {
            message = new JSONObject(data);
        } catch (JSONException ex) {
            sendMessage("Bad json!");
            return;
        }

        if (!new ReceivedMessageHandler(user, room, message).execute())
            sendMessage("Bad message type!");
    }

    @OnWebSocketConnect
    public void onOpen(Session sess) {
        session = sess;
        room = roomManager.assignUserToFreeRoom(user, this);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        roomManager.removeUserFromRoom(user);
    }

    @Override
    public void sendMessage(String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException ex) {
            LOGGER.error("Could not send message to user #" + user.getId() + " (\"" + user.getLogin() + "\")!", ex);
        }
    }

    private final UserProfile user;
    private Session session;
    private Room room;

    private final RoomManager roomManager;

    private static final Logger LOGGER = LogManager.getLogger(WebSocketConnection.class);
}