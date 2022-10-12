package manage_library.util.email;

import lombok.*;
import manage_library.util.Constant;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EmailHandler extends Thread{
    private String action;
    private String email;
    private String content;

    private void sendEmail(String toEmail, String subject, String content) throws UnsupportedEncodingException, MessagingException {
        final String fromEmail = EmailConfig.FROM_EMAIL;
        final String password = EmailConfig.PASSWORD;

        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.HOST);
        props.put("mail.smtp.port", EmailConfig.PORT);
        props.put("mail.smtp.auth", EmailConfig.AUTH);
        props.put("mail.smtp.starttls.enable", EmailConfig.ENABLE_START_TLS);

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);


        MimeMessage msg = new MimeMessage(session);

        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        msg.setFrom(new InternetAddress(fromEmail, "LIBRARY"));

        msg.setReplyTo(InternetAddress.parse(fromEmail, false));

        msg.setSubject(subject, "UTF-8");

        msg.setText(content, "UTF-8");

        msg.setSentDate(new Date());

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        Transport.send(msg);
    }

    public void sendEmailForgetPassword(String email, String code) throws MessagingException, UnsupportedEncodingException {
        sendEmail(email, Constant.ContentEmail.SUBJECT_FORGET_PASSWORD, String.format(Constant.ContentEmail.FORGET_PASSWORD, code));
    }
    public void sendEmailTwoAuth(String email, String code) throws MessagingException, UnsupportedEncodingException {
        sendEmail(email, Constant.ContentEmail.SUBJECT_TWOAUTH, String.format(Constant.ContentEmail.TWO_AUTH, code));
    }

    public void sendEmailDueToReader(String email, String content) throws MessagingException, UnsupportedEncodingException {
        sendEmail(email, Constant.ContentEmail.SUBJECT_ANNOUNCE_DUE_DATE_BORROW_BOOK, content);
    }

    public void sendEmailExpiredToReader(String email, String content) throws MessagingException, UnsupportedEncodingException {
        sendEmail(email, Constant.ContentEmail.SUBJECT_ANNOUNCE_EXPIRED_DATE_BORROW_BOOK, content);
    }

    @SneakyThrows
    @Override
    public void run() {
        switch (action) {
            case Constant.ActionEmail.RESET_PASSWORD:
                sendEmailForgetPassword(email, content);
                break;

            case Constant.ActionEmail.TWO_AUTH:
                sendEmailTwoAuth(email, content);
                break;

            case Constant.ActionEmail.ANNOUNCE_DUE_BORROW:
                sendEmailDueToReader(email, content);
                break;

            case Constant.ActionEmail.ANNOUNCE_EXPIRED_BORROW:
                sendEmailExpiredToReader(email, content);
                break;

            default:
                break;
        }
    }
}
