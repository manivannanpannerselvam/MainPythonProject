package com.prod.tab.email;

import com.prod.tap.email.TapEmail;
import com.prod.tap.config.Configvariable;
import org.testng.annotations.Test;

import javax.mail.Session;

public class TapEmailTest {


    private TapEmail tapEmail = new TapEmail();

    public void setGlobalProperties() {
        Configvariable.globalPropertyMap.put("email.smtp.host", "smtp.gmail.com");
        Configvariable.globalPropertyMap.put("email.smtp.socket.port", "465");
        Configvariable.globalPropertyMap.put("email.smtp.socket.class", "javax.net.ssl.SSLSocketFactory");
        Configvariable.globalPropertyMap.put("email.smtp.auth", "true");
        Configvariable.globalPropertyMap.put("email.smtp.port", "465");
        Configvariable.globalPropertyMap.put("email.username", "pulsesgqa@gmail.com");
        Configvariable.globalPropertyMap.put("email.password", "pulsesgqa@123");
        Configvariable.globalPropertyMap.put("email.fromUser", "pulsesgqa@gmail.com");
        Configvariable.globalPropertyMap.put("email.toUser", "testAutomation@gmail.com");
        Configvariable.globalPropertyMap.put("email.subject", "Test Automation Execution Report");
        Configvariable.globalPropertyMap.put("email.message.body", "Please find attached the cucumber report.");
    }


    @Test
    public void testCreateSession() {
        setGlobalProperties();
        Session session = tapEmail.createEmailSession();
    }

//    @Test
//    public void testSendEmail() {
//        String filepath = "target/test-classes/testFile/cucumber.json";
//        setGlobalProperties();
//        tapEmail.sendEmail(filepath);
//    }


}
