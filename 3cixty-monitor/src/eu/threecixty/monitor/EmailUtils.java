package eu.threecixty.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Utility class to send email.
 * @author Cong-Kinh Nguyen
 *
 */
public class EmailUtils {
	private static final String GMAIL_ACCOUNT_KEY = "GMAIL_ACCOUNT";
	private static final String GMAIL_PWD_KEY = "GMAIL_PWD";
	private static final String DESTINATIONS_KEY = "DESTINATION";
	
	
	private static String gmailAccount = null;
	private static String gmailPwd;
	private static String[] destinations;
	
	public static boolean send(String subject, String content, String... dests) {

		if (gmailAccount != null && !gmailAccount.equals("")) {
			try {
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

				// Get a Properties object
				Properties props = System.getProperties();
				/*
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");
				*/
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");
				
				Session session = Session.getDefaultInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(gmailAccount, gmailPwd);
							}
						});

				// -- Create a new message --
				final MimeMessage msg = new MimeMessage(session);

				// -- Set the FROM and TO fields --
				msg.setFrom(new InternetAddress(gmailAccount));
				if (dests == null || dests.length == 0) {
					for (String dest: destinations) {
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
					}
				} else {
					for (String dest: dests) {
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
					}
				}

				msg.setSubject(subject);
				msg.setText(content, "utf-8");
				msg.setSentDate(new Date());

				Transport.send(msg);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static synchronized void loadProperties(String reportFile) throws IOException {
		InputStream input = EmailUtils.class.getResourceAsStream(reportFile);
		Properties props = new Properties();
		props.load(input);
		input.close();
		gmailAccount = props.getProperty(GMAIL_ACCOUNT_KEY);
		gmailPwd = props.getProperty(GMAIL_PWD_KEY);
		String tmpDests = props.getProperty(DESTINATIONS_KEY);
		if (tmpDests != null && !tmpDests.equals("")) {
			String [] dests = tmpDests.split(",");
			destinations = new String[dests.length];
			for (int i = 0; i < dests.length; i++) {
				destinations[i] = dests[i].trim();
			}
		}
	}
	
	private EmailUtils() {
	}
}
