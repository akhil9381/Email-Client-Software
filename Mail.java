import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mail extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login Fields
    private JTextField usernameField;
    private JPasswordField passwordField;

    private String loggedInUser;
    private String loggedInPassword;

    public Mail() {
        setTitle("Modern Mail App");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        addLoginPage();

        add(mainPanel);
        setVisible(true);
    }

    private void addLoginPage() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        usernameField = new JTextField(25);
        passwordField = new JPasswordField(25);
        styleTextField(usernameField);
        stylePasswordField(passwordField);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);

        loginButton.addActionListener(e -> {
            loggedInUser = usernameField.getText();
            loggedInPassword = new String(passwordField.getPassword());
            if (!loggedInUser.matches("^.+@.+\\..+$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!loggedInUser.isEmpty() && !loggedInPassword.isEmpty()) {
                if (authenticateUser(loggedInUser, loggedInPassword)) {
                    openMailWindows();
                } else {
                    JOptionPane.showMessageDialog(this, "Credentials are wrong!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter username and password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        });


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        gbc.gridy = 3;
        loginPanel.add(loginButton, gbc);

        mainPanel.add(loginPanel, "LoginPage");
    }
    private boolean authenticateUser(String email, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, password);
            store.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void openMailWindows() {
        SwingUtilities.invokeLater(() -> {
            createInboxWindow();
            this.dispose();
        });
    }

    private void createSendWindow() {
        JFrame sendFrame = new JFrame("Send Email");
        sendFrame.setSize(600, 400);
        sendFrame.setLocationRelativeTo(null);

        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField sendToField = new JTextField(30);
        JTextField subjectField = new JTextField(30);
        JTextArea messageArea = new JTextArea(8, 30);
        JButton sendButton = new JButton("Send");
        JButton chooseFileButton = new JButton("Choose File");
        JLabel selectedFileLabel = new JLabel("No file selected");

        styleTextField(sendToField);
        styleTextField(subjectField);
        messageArea.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        styleButton(sendButton);
        styleButton(chooseFileButton);
        selectedFileLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        selectedFileLabel.setForeground(Color.GRAY);

        final File[] selectedFile = {null};

        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(sendFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                selectedFileLabel.setText("Selected: " + selectedFile[0].getName());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        sendPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        sendPanel.add(sendToField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        sendPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        sendPanel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        sendPanel.add(new JScrollPane(messageArea), gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        sendPanel.add(chooseFileButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        sendPanel.add(selectedFileLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(sendButton, gbc);

        sendButton.addActionListener(e -> sendEmail(sendToField, subjectField, messageArea, selectedFile[0], sendFrame));

        sendFrame.add(sendPanel);
        sendFrame.setVisible(true);
    }



    private void createInboxWindow() {
        JFrame inboxFrame = new JFrame("Inbox");
        inboxFrame.setSize(600, 400);
        inboxFrame.setLocationRelativeTo(null);

        JPanel inboxPanel = new JPanel(new BorderLayout());

        JList<String> emailList = new JList<>();
        JScrollPane listScrollPane = new JScrollPane(emailList);
        listScrollPane.setBorder(new TitledBorder(new LineBorder(new Color(180, 180, 180)), "Inbox"));

        JTextArea emailContentArea = new JTextArea();
        emailContentArea.setEditable(false);
        emailContentArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        emailContentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane contentScrollPane = new JScrollPane(emailContentArea);
        contentScrollPane.setBorder(new TitledBorder(new LineBorder(new Color(180, 180, 180)), "Email Content"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, contentScrollPane);
        splitPane.setResizeWeight(0.3);

        inboxPanel.add(splitPane, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        styleButton(sendButton);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(sendButton);
        inboxPanel.add(buttonPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> createSendWindow());

        loadInbox(emailList, emailContentArea);

        emailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = emailList.getSelectedIndex();
                if (selectedIndex != -1) {
                    displayEmailContent(selectedIndex, emailContentArea);
                }
            }
        });

        inboxFrame.add(inboxPanel);
        inboxFrame.setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        field.setBackground(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void stylePasswordField(JPasswordField field) {
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        field.setBackground(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(50, 150, 250));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
    }

    private void sendEmail(JTextField toField, JTextField subjectField, JTextArea messageArea, File attachment, JFrame frame) {
        String to = toField.getText();
        String subject = subjectField.getText();
        String messageText = messageArea.getText();

        if (to.isEmpty() || subject.isEmpty() || messageText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(loggedInUser, loggedInPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(loggedInUser));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // Text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(messageText);
            multipart.addBodyPart(textPart);

            // Attachment part (if any)
            if (attachment != null) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(attachment);
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            JOptionPane.showMessageDialog(frame, "Email Sent Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();

        } catch (AuthenticationFailedException e) {
            JOptionPane.showMessageDialog(frame, "Invalid Email or Password!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, e);
        } catch (MessagingException | IOException e) {
            JOptionPane.showMessageDialog(frame, "Error sending email: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    private void loadInbox(JList<String> emailList, JTextArea contentArea) {
        new Thread(() -> {
            Store store = null;
            Folder inbox = null;
            try {
                Properties props = new Properties();
                props.put("mail.store.protocol", "imaps");

                Session session = Session.getInstance(props);
                store = session.getStore("imaps");
                store.connect("imap.gmail.com", loggedInUser, loggedInPassword);

                inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                Message[] messages = inbox.getMessages();
                DefaultListModel<String> listModel = new DefaultListModel<>();

                for (int i = messages.length - 1; i >= Math.max(0, messages.length - 5); i--) {
                    listModel.addElement("From: " + messages[i].getFrom()[0] + " - Subject: " + messages[i].getSubject());
                }

                SwingUtilities.invokeLater(() -> emailList.setModel(listModel));

            } catch (AuthenticationFailedException e) {
                SwingUtilities.invokeLater(() -> contentArea.setText("Invalid Email or Password!"));
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, e);
            } catch (MessagingException e) {
                SwingUtilities.invokeLater(() -> contentArea.setText("Error loading emails: " + e.getMessage()));
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, e);
            }
            finally {
                try {
                    if (inbox != null) inbox.close(false);
                    if (store != null) store.close();
                } catch (MessagingException ex) {
                    Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private void displayEmailContent(int index, JTextArea contentArea) {
        new Thread(() -> {
            Store store = null;
            Folder inbox = null;
            try {
                Properties props = new Properties();
                props.put("mail.store.protocol", "imaps");

                Session session = Session.getInstance(props);
                store = session.getStore("imaps");
                store.connect("imap.gmail.com", loggedInUser, loggedInPassword);

                inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                Message[] messages = inbox.getMessages();
                Message message = messages[messages.length - 1 - index];
                Object content = message.getContent();

                String emailContent = "";

                if (content instanceof String) {
                    emailContent = (String) content;
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content; 
                    emailContent = getTextFromMultipart(multipart);
                } else {
                    emailContent = "Unsupported content type";
                }

                String finalEmailContent = emailContent; // Make it final for SwingUtilities.invokeLater
                SwingUtilities.invokeLater(() -> contentArea.setText(finalEmailContent));

            } catch (MessagingException |IOException e) {
                SwingUtilities.invokeLater(() -> contentArea.setText("Error displaying email content: " + e.getMessage()));
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    if (inbox != null) inbox.close(false);
                    if (store != null) store.close();
                } catch (MessagingException ex) {
                    Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private String getTextFromMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                content.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                content.append(getTextFromHtml(bodyPart.getContent().toString()));
            } else if (bodyPart.getContent() instanceof Multipart) {
                content.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return content.toString();
    }

    private String getTextFromHtml(String html) {
        // Basic HTML to text conversion (remove tags)
        return html.replaceAll("<[^>]*>", "");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mail::new);
    }
}