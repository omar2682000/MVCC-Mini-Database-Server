package com.atypon.connection;

import com.atypon.api.DatabaseRequest;
import com.atypon.api.DatabaseRequestInterface;
import com.atypon.database.ClientData;
import com.atypon.database.Database;
import com.atypon.files.FilesManager;
import com.atypon.files.Log;
import com.atypon.files.ObjectReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class DatabaseServer {
  private static final Integer PORT = 2001;
  private static ServerSocket serverSocket;
  static ObjectInputStream objectInputStream;
  private DatabaseServer() {

  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    try {
      serverSocket = new ServerSocket(PORT);
    } catch (IOException ioException) {
      new Log(DatabaseServer.class.getName()).
              warning(ioException);
    }

    while (true) {
      Socket socket = serverSocket.accept();
      try {
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        var request = (DatabaseRequestInterface) objectInputStream.readObject();
        new Thread(
                new ClientHandler(request, socket)
        ).start();
        System.out.println(request.getSender() + " " + request.getRequest());
      } catch (EOFException eofException){
        continue;
      }
    }
  }

  public synchronized static Database getClientDatabase(String username) {
    var reader = new ObjectReader();
    var clientsData = FilesManager.CLIENTS_DATA_FILE;
    List<Object> list = reader.readAll(clientsData);
    for (Object obj: list){
      ClientData map = (ClientData) obj;
      System.out.println("User = " + map.getUser() + " ---- username = " + username);
      if (map.getUser().equals(username)){
        System.out.println("Found database");
        return map.getDatabase();
      }
    }
    return null;
  }


  public static Integer getPort(){
    return PORT;
  }
}