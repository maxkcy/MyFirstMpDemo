package com.max.myfirstmpdemo.headless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.max.myfirstmpdemo.Packets.RoomEnum;
import com.max.myfirstmpdemo.Packets.RoomPacket;
import com.max.myfirstmpdemo.Packets.TouchDownPacket;
import com.max.myfirstmpdemo.headless.Entities.PlayerEntity;


import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class HandleFrame {
ServerMain serverMain;
    Object request;
    public HandleFrame(ServerMain serverMain) {
        this.serverMain = serverMain;
    }


    public void handleFrame(ServerWebSocket webSocket, final WebSocketFrame frame){
        request = serverMain.manualSerializer.deserialize(frame.binaryData().getBytes());
        System.out.println("Received packet: " + request + " from: RA " + webSocket.remoteAddress());

        if (request instanceof RoomPacket){
            //System.out.println("RoomPact RoomEnum: " + ((RoomPacket) request).roomEnum);
            Gdx.app.log(this.toString(), "RoomPact RoomEnum: " + ((RoomPacket) request).roomEnum);
            if(((RoomPacket) request).roomEnum == RoomEnum.QUE){
                serverMain.waitingForGameQueue.addLast(webSocket);
                //System.out.println("client " + webSocket + " added to que");
                Gdx.app.log(this.toString(), "client " + webSocket + " added to que");
                final RoomPacket response = new RoomPacket(RoomEnum.QUE);
                response.roomEnum = RoomEnum.QUE;
                webSocket.writeFinalBinaryFrame(Buffer.buffer(serverMain.manualSerializer.serialize(response)));
                //System.out.println("sent response confirming client added to que");
                Gdx.app.log(this.toString(), "sent response confirming client added to que");
            }
        }

    }

    public void handleGame(ServerWebSocket webSocket, final WebSocketFrame frame){
        if (request instanceof TouchDownPacket){
            Gdx.app.log(this.toString(), "TouchDownPacket from: " + webSocket);
            ClientID clientID = ServerMain.clientHash.get(webSocket);
            float angle = MathUtils.atan2(((TouchDownPacket) request).getY() - clientID.getClientPlayerItem().userData.position.y,
                    ((TouchDownPacket) request).getX() - clientID.getClientPlayerItem().userData.position.x)
                    * MathUtils.radiansToDegrees;
            angle = (((angle % 360) + 360) % 360);

            float newX;
            float newY;

            if(((TouchDownPacket) request).getX() - clientID.getClientPlayerItem().userData.position.x < -5 ||
                    ((TouchDownPacket) request).getX() - clientID.getClientPlayerItem().userData.position.x > 5){
                newX = clientID.getClientPlayerItem().userData.position.x + MathUtils.cosDeg(angle) * 5;
            }else {newX = ((TouchDownPacket) request).getX();}

            if(((TouchDownPacket) request).getY() - clientID.getClientPlayerItem().userData.position.y < -5 ||
                    ((TouchDownPacket) request).getY() - clientID.getClientPlayerItem().userData.position.y > 5){
                newY = clientID.getClientPlayerItem().userData.position.y + MathUtils.sinDeg(angle) * 5;
            }else{newY = ((TouchDownPacket) request).getY();}

            clientID.getClientGameRoom().gameWorld.world.move(clientID.getClientPlayerItem(), newX, newY,
                    ((PlayerEntity)clientID.getClientPlayerItem().userData).collisionFilter);

            //clientID.getClientPlayerItem().userData.position.x = clientID.getClientGameRoom().gameWorld.world.getRect(clientID.getClientPlayerItem()).x;
            //clientID.getClientPlayerItem().userData.position.y = clientID.getClientGameRoom().gameWorld.world.getRect(clientID.getClientPlayerItem()).y;
        }
    }
}
