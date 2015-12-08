/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.log4j.Logger;

import eu.threecixty.Configuration;

/**
 * Utility class to send email.
 *
 */
public class EmailUtils {
	private static final String ACCOUNT_KEY = "ACCOUNT";
	private static final String PWD_KEY = "PWD";
	private static final String EMAIL = "EMAIL";
	private static final String DESTINATIONS_KEY = "DESTINATION";
	
	 private static final Logger LOGGER = Logger.getLogger(
			 EmailUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static String accountUser = null;
	private static String accountPwd;
	private static String email;
	private static String[] destinations;
	
	public static boolean send(String subject, String content, String... dests) {
		if (accountUser == null) {
			synchronized (EmailUtils.class) {
				if (accountUser == null) {
					try {
						loadProperties();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (accountUser != null && !accountUser.equals("")) {
			try {
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

				// Get a Properties object
				Properties props = System.getProperties();
				props.put("mail.smtp.host", "80.237.132.17");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");
				
				Session session = Session.getDefaultInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(accountUser, accountPwd);
							}
						});

				// -- Create a new message --
				final MimeMessage msg = new MimeMessage(session);

				// -- Set the FROM and TO fields --
				msg.setFrom(new InternetAddress(email));
				if (dests == null || dests.length == 0) {
					for (String dest: destinations) {
						if (DEBUG_MOD) LOGGER.info(dest);
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
					}
				} else {
					for (String dest: dests) {
						if (DEBUG_MOD) LOGGER.info(dest);
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
					}
				}

				msg.setSubject(subject);
				msg.setText(content, "utf-8");
				msg.setSentDate(new Date());

				Transport.send(msg);
				return true;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Loads user, password and email from a properties file.
	 * @throws IOException
	 */
	private static synchronized void loadProperties() throws IOException {
		InputStream input = new FileInputStream(Configuration.path + File.separatorChar + "WEB-INF"
	            + File.separatorChar + "report.properties");
		Properties props = new Properties();
		props.load(input);
		input.close();
		accountUser = props.getProperty(ACCOUNT_KEY);
		accountPwd = props.getProperty(PWD_KEY);
		email = props.getProperty(EMAIL);
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
