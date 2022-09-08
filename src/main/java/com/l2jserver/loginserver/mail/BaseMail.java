/*
 * Copyright © 2004-2020 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.loginserver.mail;

import static com.l2jserver.loginserver.config.Configuration.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;

/**
 * Base Mail.
 * @author mrTJO
 * @version 2.6.1.0
 */
public class BaseMail implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseMail.class);
	
	private MimeMessage _messageMime = null;
	
	private static class SmtpAuthenticator extends Authenticator {
		private final PasswordAuthentication _auth;
		
		public SmtpAuthenticator() {
			_auth = new PasswordAuthentication(email().getSmtpUsername(), email().getSmtpPassword());
		}
		
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return _auth;
		}
	}
	
	public BaseMail(String account, String mailId, String... args) {
		final var mailAddr = getUserMail(account);
		if (mailAddr == null) {
			return;
		}
		
		final var content = MailSystem.getInstance().getMailContent(mailId);
		if (content == null) {
			return;
		}
		
		final var message = compileHtml(account, content.getText(), args);
		final var mailProp = new Properties();
		mailProp.put("mail.smtp.host", email().getHost());
		mailProp.put("mail.smtp.auth", email().isSmtpAuthRequired());
		mailProp.put("mail.smtp.port", email().getPort());
		mailProp.put("mail.smtp.socketFactory.port", email().getPort());
		mailProp.put("mail.smtp.socketFactory.class", email().getSmtpFactory());
		mailProp.put("mail.smtp.socketFactory.fallback", email().smtpFactoryCallback());
		final var authenticator = (email().isSmtpAuthRequired() ? new SmtpAuthenticator() : null);
		final var mailSession = Session.getDefaultInstance(mailProp, authenticator);
		
		try {
			_messageMime = new MimeMessage(mailSession);
			_messageMime.setSubject(content.getSubject());
			try {
				_messageMime.setFrom(new InternetAddress(email().getServerEmail(), email().getServerName()));
			} catch (UnsupportedEncodingException ex) {
				LOG.warn("Sender address {} is not Valid!", email().getServerEmail());
			}
			_messageMime.setContent(message, "text/html");
			_messageMime.setRecipient(Message.RecipientType.TO, new InternetAddress(mailAddr));
		} catch (MessagingException ex) {
			LOG.warn("There has been an error sending the email!", ex);
		}
	}
	
	private String compileHtml(String account, String html, String[] args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				html = html.replace("%var" + i + "%", args[i]);
			}
		}
		return html.replace("%accountname%", account);
	}
	
	private String getUserMail(String username) {
		try (var con = ConnectionFactory.getInstance().getConnection();
			var statement = con.prepareStatement(email().getSelectQuery())) {
			statement.setString(1, username);
			try (var rs = statement.executeQuery()) {
				if (rs.next()) {
					return rs.getString(email().getDatabaseField());
				}
			}
		} catch (Exception ex) {
			LOG.warn("Cannot select user mail!", ex);
		}
		return null;
	}
	
	@Override
	public void run() {
		try {
			if (_messageMime != null) {
				Transport.send(_messageMime);
			}
		} catch (MessagingException ex) {
			LOG.warn("There has been an error while sending email!", ex);
		}
	}
}
