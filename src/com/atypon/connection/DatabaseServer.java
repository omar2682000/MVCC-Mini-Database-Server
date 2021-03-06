package com.atypon.connection;

import com.atypon.api.DatabaseRequestInterface;
import com.atypon.database.ClientData;
import com.atypon.database.DatabaseInterface;
import com.atypon.files.FilesManager;
import com.atypon.files.Log;
import com.atypon.files.ObjectReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseServer {
  private static final AtomicInteger PORT = new AtomicInteger(2001);
  private static ServerSocket serverSocket;
  static ObjectInputStream objectInputStream;

  private DatabaseServer() {
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    try {
      serverSocket = new ServerSocket(PORT.get());
    } catch (IOException ioException) {
      new Log(DatabaseServer.class.getName()).
              warning(ioException);
    }

    while (true) {
      Socket socket = serverSocket.accept();
      try {
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        DatabaseRequestInterface request = (DatabaseRequestInterface) objectInputStream.readObject();
        new Thread(
                new ClientHandler(request, socket)
        ).start();
      } catch (EOFException eofException){
        new Log(DatabaseServer.class.getName())
                .warning(eofException);
      }
    }
  }

  public static synchronized DatabaseInterface getClientDatabase(String username) {
    ObjectReader reader = new ObjectReader();
    File clientsData = FilesManager.CLIENTS_DATA_FILE;
    List<Object> list = reader.readAll(clientsData);
    for (Object obj: list) {
      ClientData clientData = (ClientData) obj;
      if (clientData.getUser().equals(username)){
        return clientData.getDatabase();
      }
    }
    return null;
  }

  public static Integer getPort(){
    return PORT.get();
  }
}
