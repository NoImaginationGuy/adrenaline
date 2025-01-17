package it.polimi.deib.newdem.adrenaline.view.inet.sockets;

import it.polimi.deib.newdem.adrenaline.view.inet.IncomingUserModule;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Implementation of {@code IncomingUserModule} involving the acceptance of
 * new TCP connections through the use of Java Sockets.
 */
public class SocketUserModule implements IncomingUserModule {

    private ServerSocket serverSocket;

    public SocketUserModule(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void init() {
        // nothing to init.
    }

    @Override
    public User newUser() {

        try {
            Socket clientSocket = serverSocket.accept();

            User user = new User();

            SocketUserConnection newConnection = new SocketUserConnection(clientSocket, user);
            newConnection.start();

            return user;
        } catch (IOException x) {
            return null;
        }
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException x) {
            // no problem
        } finally {
            serverSocket = null;
        }
    }
}
