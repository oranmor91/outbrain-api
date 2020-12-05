//package com.example.outbrainApi.outbrain;
//
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import javax.swing.JOptionPane;
//import java.util.List;
//import java.util.Properties;
//
//public class Email {
//
//    public final void prepareAndSendEmail(String htmlMessage, String toMailId) {
//
//        final OneMethod oneMethod = new OneMethod();
//        final List<char[]> resourceList = oneMethod.getValidatorResource();
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(465);
//
//        mailSender.setUsername(String.valueOf(resourceList.get(0)));
//        mailSender.setPassword(String.valueOf(resourceList.get(1)));
//
//        //from email id and password
//        System.out.println("Username is : " + String.valueOf(resourceList.get(0)).split("@")[0]);
//        System.out.println("Password is : " + String.valueOf(resourceList.get(1)));
//
//        Properties mailProp = mailSender.getJavaMailProperties();
//        mailProp.put("mail.transport.protocol", "smtp");
//        mailProp.put("mail.smtp.auth", "true");
//        mailProp.put("mail.smtp.starttls.enable", "true");
//        mailProp.put("mail.smtp.starttls.required", "true");
//        mailProp.put("mail.debug", "true");
//        mailProp.put("mail.smtp.ssl.enable", "true");
//
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.setTo(toMailId);
//            helper.setSubject("Welcome to Subject Part");
//            helper.setText(htmlMessage, true);
//
//            //Checking Internet Connection and then sending the mail
//            if(OneMethod.isNetConnAvailable())
//                mailSender.send(mimeMessage);
//            else
//                JOptionPane.showMessageDialog(null, "No Internet Connection Found...");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}
