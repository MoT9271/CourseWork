package com.matnik.game.fxcoursework.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.matnik.game.fxcoursework.server.model.BattleLogic;
import com.matnik.game.fxcoursework.server.model.ClientStreams;
import com.matnik.game.fxcoursework.server.model.Response;
import com.matnik.game.fxcoursework.server.model.UserInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Server {
    private static final Map<String, String> registeredUsers = new HashMap<>();
    private static final String USER_DATA_FILE = "userdata.json";
    private static final Map<String, ClientStreams> connectedClients = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final Queue<ClientStreams> waitingClients = new LinkedList<>();

    public static void main(String[] args) {
        loadUserData();

        int portNumber = 5555;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is running and waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Создаем поток ввода/вывода для клиента
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ClientStreams clientStreams = new ClientStreams(in, out);
                // Добавляем клиента в список подключенных
                connectedClients.put(getClientId(clientSocket), clientStreams);

                // Обработка сообщений от клиента в отдельном потоке
                new Thread(() -> handleClient(clientSocket, clientStreams)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket,ClientStreams clientStreams) {
        try {
            while (clientSocket.isConnected() && clientStreams.isActive()) {
                // Обрабатываем ввод/вывод с клиентом
                String action = (String) clientStreams.in().readObject();

                if ("registration".equalsIgnoreCase(action)) {
                    handleRegistration(clientStreams);
                } else if ("login".equalsIgnoreCase(action)) {
                    handleLogin(clientStreams);
                } else if ("findOpponent".equalsIgnoreCase(action)) {
                    handleFindOpponent(clientStreams);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

        }
    }

    private static void handleFindOpponent(ClientStreams clientStreams) throws IOException, ClassNotFoundException {
        if (waitingClients.isEmpty()) {
            // Если нет ожидающих клиентов, добавьте текущего клиента в очередь ожидания
            waitingClients.add(clientStreams);
            clientStreams.out().writeObject(new Response(false, "NoPlayersFound"));
            clientStreams.setActive(false);
        } else {
            // Если есть ожидающий клиент, объедините его с текущим клиентом
            ClientStreams opponentClientStream = waitingClients.poll();
            clientStreams.setActive(false);
            startFight(opponentClientStream, clientStreams);

        }
    }

    private static void startFight(ClientStreams player1, ClientStreams player2) throws IOException, ClassNotFoundException {
        // Здесь вы можете отправить информацию об игре обоим игрокам и т.д.
        player1.out().writeObject(new Response(true, "OpponentFound"));
        player2.out().writeObject(new Response(true, "OpponentFound"));
        BattleLogic battleLogic = new BattleLogic(player1, player2);
        new Thread(() -> {
            try {
                battleLogic.handleFight();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private static String getClientId(Socket clientSocket) {
        // В данном примере используем адрес клиента и порт
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
    private static void handleRegistration(ClientStreams clientStreams) throws IOException {
        try {
            // Expecting UserInfo for registration
            Object receivedObject = clientStreams.in().readObject();

            if (receivedObject == null) {
                clientStreams.out().writeObject(new Response(false, "Invalid UserInfo object received."));
                return;
            }

            if (receivedObject instanceof UserInfo userInfo) {

                String username = userInfo.username();
                String password = userInfo.password();

                if (isUsernameAvailable(username)) {
                    registeredUsers.put(username, password);
                    saveUserData();
                    clientStreams.out().writeObject(new Response(true, "Registration successful!"));
                } else {
                    clientStreams.out().writeObject(new Response(false, "Username is already taken!"));
                }
            } else {
                clientStreams.out().writeObject(new Response(false, "Invalid UserInfo object received."));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            clientStreams.out().writeObject(new Response(false, "Error processing registration."));
        }
    }

    private static void handleLogin(ClientStreams clientStreams) throws IOException {
        try {
            // Expecting UserInfo for login
            Object receivedObject = clientStreams.in().readObject();

            if (receivedObject == null) {
                clientStreams.out().writeObject(new Response(false, "Invalid UserInfo object received."));
                return;
            }

            if (receivedObject instanceof UserInfo userInfo) {

                String username = userInfo.username();
                String password = userInfo.password();

                if (registeredUsers.containsKey(username) && registeredUsers.get(username).equals(password)) {
                    clientStreams.out().writeObject(new Response(true, "Login successful!"));
                } else {
                    clientStreams.out().writeObject(new Response(false, "Invalid username or password!"));
                }
            } else {
                clientStreams.out().writeObject(new Response(false, "Invalid UserInfo object received."));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            clientStreams.out().writeObject(new Response(false, "Error processing login."));
        }
    }

    private static boolean isUsernameAvailable(String username) {
        return !registeredUsers.containsKey(username);
    }

    private static void loadUserData() {
        try (Reader reader = new FileReader(USER_DATA_FILE)) {
            MapType type = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class);
            registeredUsers.putAll(objectMapper.readValue(reader, type));
        } catch (FileNotFoundException e) {
            System.out.println("No existing user data file found. Creating a new one.");
            saveUserData(); // Create an empty user data file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUserData() {
        try (Writer writer = new FileWriter(USER_DATA_FILE)) {
            objectMapper.writeValue(writer, registeredUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
