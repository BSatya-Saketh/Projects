import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends JFrame {
    private JTextPane chatArea;
    private JTextField messageField;
    private JTextField usernameField;
    private JButton connectButton;
    private JButton sendButton;
    private JLabel statusLabel;
    private JPanel inputPanel;
    private JPanel loginPanel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;
    private String username;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final Color MY_MESSAGE_COLOR = new Color(230, 240, 255);
    private static final Color OTHERS_MESSAGE_COLOR = new Color(240, 240, 240);
    private static final Color SYSTEM_MESSAGE_COLOR = new Color(255, 255, 200);

    public Client() {
        super("Chat Client");
        setupUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI() {
        // Set up the main content panel with BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        // Set up the chat display area
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Create login panel for connection details
        loginPanel = new JPanel(new BorderLayout(5, 5));
        JPanel loginInputPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        usernameField = new JTextField(20);
        connectButton = new JButton("Connect");
        loginInputPanel.add(usernameField);
        loginInputPanel.add(connectButton);
        loginPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        loginPanel.add(loginInputPanel, BorderLayout.CENTER);
        
        // Create message input panel
        inputPanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        messageField.setEnabled(false);
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Stack both panels in the south region with a card layout
        JPanel southPanel = new JPanel(new CardLayout());
        southPanel.add(loginPanel, "login");
        southPanel.add(inputPanel, "chat");
        contentPanel.add(southPanel, BorderLayout.SOUTH);
        
        // Status label at the top
        statusLabel = new JLabel("Not connected", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        contentPanel.add(statusLabel, BorderLayout.NORTH);

        // Add action listeners
        connectButton.addActionListener(e -> connectToServer());
        
        sendButton.addActionListener(e -> sendMessage());
        
        messageField.addActionListener(e -> sendMessage());
        
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
    }

    private void connectToServer() {
        username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Start the message receiving thread
            new Thread(this::receiveMessages).start();
            
            // Update UI state
            usernameField.setEnabled(false);
            connectButton.setEnabled(false);
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            statusLabel.setText("Connected as: " + username);
            statusLabel.setForeground(new Color(0, 128, 0));
            
            // Switch from login panel to chat input panel
            CardLayout cl = (CardLayout) (inputPanel.getParent().getLayout());
            cl.next(inputPanel.getParent());
            
            messageField.requestFocus();
            isConnected = true;
            
            // Clear the chat area and add welcome message
            clearChatArea();
            appendSystemMessage("Connecting to server...");
            
        } catch (IOException e) {
            appendSystemMessage("Failed to connect: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Could not connect to server: " + e.getMessage(), 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        if (!isConnected) return;
        
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            if (message.equalsIgnoreCase("exit")) {
                disconnect();
                return;
            }
            
            out.println(message);
            appendMyMessage(message);
            messageField.setText("");
        }
        messageField.requestFocus();
    }

    private void receiveMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String message = line;
                
                // Special handling for the first message (username prompt)
                if (message.equals("Enter username: ")) {
                    out.println(username);
                    continue;
                }
                
                // Handle username exists message
                if (message.startsWith("Username exists!")) {
                    SwingUtilities.invokeLater(() -> {
                        disconnect();
                        JOptionPane.showMessageDialog(this, "Username already exists. Try another one.", 
                                "Username Taken", JOptionPane.WARNING_MESSAGE);
                        reconnect();
                    });
                    break;
                }
                
                // Add message to chat area
                SwingUtilities.invokeLater(() -> appendSystemMessage(message));
            }
        } catch (IOException e) {
            if (isConnected) {
                SwingUtilities.invokeLater(() -> {
                    appendSystemMessage("Connection lost: " + e.getMessage());
                    disconnect();
                });
            }
        }
    }

    private void disconnect() {
        if (isConnected) {
            isConnected = false;
            try {
                if (out != null) {
                    out.println("exit");
                    out.close();
                }
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                // Ignore, we're disconnecting anyway
            }
            
            // Reset UI state
            SwingUtilities.invokeLater(() -> {
                usernameField.setEnabled(true);
                connectButton.setEnabled(true);
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
                statusLabel.setText("Disconnected");
                statusLabel.setForeground(Color.RED);
                
                // Switch back to login panel
                CardLayout cl = (CardLayout) (inputPanel.getParent().getLayout());
                cl.first(inputPanel.getParent());
            });
        }
    }
    
    private void reconnect() {
        // Reset UI for reconnection
        usernameField.setEnabled(true);
        connectButton.setEnabled(true);
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        statusLabel.setText("Not connected");
        statusLabel.setForeground(Color.RED);
        
        // Switch back to login panel
        CardLayout cl = (CardLayout) (inputPanel.getParent().getLayout());
        cl.first(inputPanel.getParent());
    }
    
    private void clearChatArea() {
        SwingUtilities.invokeLater(() -> {
            chatArea.setText("");
        });
    }
    
    private void appendSystemMessage(String message) {
        appendStyledMessage(message, SYSTEM_MESSAGE_COLOR, null);
    }
    
    private void appendMyMessage(String message) {
        appendStyledMessage("You: " + message, MY_MESSAGE_COLOR, null);
    }
    
    private void appendOtherMessage(String sender, String message) {
        appendStyledMessage(sender + ": " + message, OTHERS_MESSAGE_COLOR, null);
    }
    
    private void appendStyledMessage(String message, Color bgColor, Color textColor) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("MessageStyle", null);
        StyleConstants.setBackground(style, bgColor);
        if (textColor != null) {
            StyleConstants.setForeground(style, textColor);
        }
        
        // Add timestamp
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        message = "[" + timestamp + "] " + message;
        
        try {
            // Insert message at the end
            int length = doc.getLength();
            doc.insertString(length, message + "\n", style);
            
            // Scroll to the bottom
            chatArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            System.err.println("Error appending message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(Client::new);
    }
}
