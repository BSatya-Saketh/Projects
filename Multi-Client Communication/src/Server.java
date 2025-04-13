import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    private static JTextArea logArea;
    private static JFrame frame;
    private static JButton startButton;
    private static JButton stopButton;
    private static JLabel statusLabel;
    private static ServerSocket ss;
    private static boolean serverRunning = false;
    private static final int PORT = 5000;
    private static ServerThread serverThread;

    public static void main(String args[]) throws Exception {
        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        // Create the main frame
        frame = new JFrame("Chat Server Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Create the log panel
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        // Create control panel
        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        statusLabel = new JLabel("Server Status: Stopped", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        // Create status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Add action listeners
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        // Add components to frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.add(statusPanel, BorderLayout.NORTH);

        // Display the frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        logMessage("Server control panel initialized. Click 'Start Server' to begin.");
    }

    private static void startServer() {
        if (!serverRunning) {
            serverThread = new ServerThread();
            serverThread.start();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            statusLabel.setText("Server Status: Running");
            statusLabel.setForeground(Color.GREEN);
        }
    }

    private static void stopServer() {
        if (serverRunning && serverThread != null) {
            try {
                serverRunning = false;
                if (ss != null && !ss.isClosed()) {
                    ss.close();
                }
                serverThread.interrupt();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                statusLabel.setText("Server Status: Stopped");
                statusLabel.setForeground(Color.RED);
                logMessage("Server stopped");
            } catch (IOException e) {
                logMessage("Error stopping server: " + e.getMessage());
            }
        }
    }

    public static void logMessage(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    static class ServerThread extends Thread {
        public void run() {
            try {
                ss = new ServerSocket(PORT);
                serverRunning = true;
                logMessage("Server started on port " + PORT);

                while (serverRunning) {
                    try {
                        Socket cs = ss.accept();
                        String clientAddress = cs.getInetAddress().getHostAddress();
                        logMessage("Client connected from " + clientAddress);
                        new ClientHandler(cs).start();
                    } catch (SocketException se) {
                        if (!serverRunning) {
                            break; // Expected exception when closing server
                        }
                        logMessage("Socket error: " + se.getMessage());
                    }
                }
            } catch (IOException e) {
                logMessage("Server error: " + e.getMessage());
            }
        }
    }
}

class ClientHandler extends Thread {
    private static Hashtable<String, PrintWriter> users = new Hashtable<>();
    private Socket cs = null;
    private String username;
    private String mssg;

    ClientHandler(Socket cs) {
        this.cs = cs;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
        ) {
            out.println("Enter username: ");
            username = in.readLine();
            while (true) {
                if (username != null && users.containsKey(username)) {
                    out.println("Username exists! Try Again. Username: ");
                    username = in.readLine();
                } else {
                    users.put(username, out);
                    Server.logMessage("User: " + username + " joined the chat");
                    out.println("Welcome to the chat!");
                    break;
                }
            }

            for (PrintWriter writer : users.values()) {
                if (writer != out)
                    writer.println("Username: " + username + " joined the chat.");
            }

            // Maintaining connection
            while ((mssg = in.readLine()) != null) {
                if (mssg.equalsIgnoreCase("exit"))
                    break;
                broadcast(mssg, out);
                Server.logMessage(username + ": " + mssg);
            }

            // Removing user
            removeUser(out);

        } catch (IOException ex) {
            Server.logMessage("IO Exception with client " + username + ": " + ex.getMessage());
        } finally {
            if (username != null)
                users.remove(username);
        }
    }

    private void broadcast(String mssg, PrintWriter exclude) {
        for (PrintWriter writer : users.values()) {
            if (writer != exclude) {
                writer.println("\n" + username + ": " + mssg);
                writer.flush();
            }
        }
    }

    private void removeUser(PrintWriter out) {
        if (username != null) {
            for (PrintWriter writer : users.values()) {
                if (writer != out) {
                    writer.println(username + " left the chat");
                }
            }
            Server.logMessage(username + " left the chat");
        }

        try {
            cs.close();
        } catch (IOException e) {
            Server.logMessage("Error closing socket: " + e.getMessage());
        }
    }
}