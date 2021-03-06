package com.atypon.api;

import com.atypon.authorization.AccessType;
import com.atypon.authorization.DeniedAccess;
import com.atypon.authorization.DeniedAccessException;
import com.atypon.connection.DatabaseServer;
import com.atypon.files.FilesManager;
import com.atypon.files.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionRequest implements ConnectionRequestInterface {
  private final String CLIENT_USER_NAME;
  private final Integer PORT;
  private final Socket SOCKET;

  public ConnectionRequest(final String userName, final String ip) throws IOException {
    this.CLIENT_USER_NAME = userName;
    this.PORT = 2000;
    this.SOCKET = new Socket(ip, PORT);
  }

  @Override
  public AccessType login() {
    LoginRequestInterface loginRequest = new LoginRequest(CLIENT_USER_NAME);
    AccessType access = new DeniedAccess();

    try {
      synchronized (FilesManager.ACCESS_FILE) {
        sendLoginRequest(loginRequest);
        access = readLoginResponse();
      }

    } catch (IOException ioException) {
      new Log(ConnectionRequest.class.getName())
              .warning(ioException);
    }
    return access;
  }

  @Override
  public void sendLoginRequest(LoginRequestInterface loginRequest) throws IOException {
    var outputStream = new ObjectOutputStream(
            SOCKET.getOutputStream());
    outputStream.writeObject(loginRequest);
    outputStream.flush();
  }

  @Override
  public AccessType readLoginResponse()  {
    ObjectInputStream inputStream;
    try {
      inputStream = new ObjectInputStream(
              SOCKET.getInputStream());
      return (AccessType) inputStream.readObject();
    } catch (IOException | ClassNotFoundException ioException) {
      ioException.printStackTrace();
    }
    return null;
  }

  @Override
  public Integer getDatabasePort(String ip) throws DeniedAccessException {
    AccessType access = login();
    if (!access.getAccess()) {
      throw new DeniedAccessException();
    }
    return DatabaseServer.getPort();
  }
}
