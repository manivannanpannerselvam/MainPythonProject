package com.prod.tap.email;

import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Component
public class TapEmail {
    private static final Logger logger = Logger.getLogger(TapEmail.class);

    public Session createEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", Configvariable.globalPropertyMap.get("email.smtp.host"));

        // set the port of socket factory
        props.put("mail.smtp.socketFactory.port", Configvariable.globalPropertyMap.get("email.smtp.socket.port"));

        // set socket factory
        props.put("mail.smtp.socketFactory.class", Configvariable.globalPropertyMap.get("email.smtp.socket.class"));

        // set the authentication to true
        props.put("mail.smtp.auth", Configvariable.globalPropertyMap.get("email.smtp.auth"));

        // set the port of SMTP server
        props.put("mail.smtp.port", Configvariable.globalPropertyMap.get("email.smtp.port"));

        // This will handle the complete authentication
        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Configvariable.globalPropertyMap.get("email.username"), Configvariable.globalPropertyMap.get("email.password"));
                    }
                });
        return session;
    }

    public void sendEmail(String filepath) {
        Session session = createEmailSession();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Configvariable.globalPropertyMap.get("email.fromUser")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Configvariable.globalPropertyMap.get("email.toUser")));
            message.setSubject(Configvariable.globalPropertyMap.get("email.subject"));

            // Create object to add multimedia type content
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(Configvariable.globalPropertyMap.get("email.message.body"));

            // Create another object to add another content
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();
            // Mention the file which you want to send
            String filename = filepath;
            // Create data source and pass the filename
            DataSource source = new FileDataSource(filename);
            // set the handler
            messageBodyPart2.setDataHandler(new DataHandler(source));
            // set the file
            messageBodyPart2.setFileName(filename);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart2);
            multipart.addBodyPart(messageBodyPart1);
            message.setContent(multipart);
            Transport.send(message);

            logger.info("Email has been send to " + Configvariable.globalPropertyMap.get("email.toUser"));

        } catch (MessagingException e) {

            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to send email {}", e.getMessage());

        }

    }

    private Properties getServerProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", Configvariable.globalPropertyMap.get("email.smtp.host"));
        // set the port of socket factory
        props.put("mail.smtp.socketFactory.port", Configvariable.globalPropertyMap.get("email.smtp.socket.port"));
        // set socket factory
        props.put("mail.smtp.socketFactory.class", Configvariable.globalPropertyMap.get("email.smtp.socket.class"));
        // set the authentication to true
        props.put("mail.smtp.auth", Configvariable.globalPropertyMap.get("email.smtp.auth"));
        // set the port of SMTP server
        props.put("mail.smtp.port", Configvariable.globalPropertyMap.get("email.smtp.port"));
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", Configvariable.globalPropertyMap.get("email.smtp.host"));
        return props;
    }

    public Session createSession() {
        return Session.getDefaultInstance(getServerProperties(),null);
    }

    public Store createStoreConnection() {
        Session session = createSession();
        // connects to the message store
        Store store = null;
        try {
            store = session.getStore();
            store.connect(Configvariable.globalPropertyMap.get("email.username"), Configvariable.globalPropertyMap.get("email.password"));
        } catch (NoSuchProviderException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to get smtp store {}", e.getMessage());
        } catch (MessagingException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to connect with email store {}", e.getMessage());
        }
        return store;
    }

    public void downloadEmails() {

        try {
            // connects to the message store
            Store store = createStoreConnection();

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);

            // fetches new messages from server
            Message[] messages = folderInbox.getMessages();

            for (int i = 0; i < messages.length; i++) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg
                        .getRecipients(MimeMessage.RecipientType.TO));
                String ccList = parseAddresses(msg
                        .getRecipients(MimeMessage.RecipientType.CC));
                String sentDate = msg.getSentDate().toString();

                String contentType = msg.getContentType();
                String messageContent = "";

                if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    try {
                        Object content = msg.getContent();
                        if (content != null) {
                            messageContent = content.toString();
                        }
                    } catch (Exception ex) {
                        messageContent = "[Error downloading content]";
                        ex.printStackTrace();
                    }
                }

                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t To: " + toList);
                System.out.println("\t CC: " + ccList);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);
            }

            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
    }

    private String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }


}
