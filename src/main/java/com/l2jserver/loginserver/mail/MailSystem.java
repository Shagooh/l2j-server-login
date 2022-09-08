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
import static com.l2jserver.loginserver.config.Configuration.server;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Mail System.
 * @author mrTJO
 * @version 2.6.1.0
 */
public class MailSystem {
	
	private static final Logger LOG = LoggerFactory.getLogger(MailSystem.class);
	
	private final Map<String, MailContent> _mailData = new HashMap<>();
	
	public MailSystem() {
		loadMails();
	}
	
	public void sendMail(String account, String messageId, String... args) {
		BaseMail mail = new BaseMail(account, messageId, args);
		mail.run();
	}
	
	private void loadMails() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File file = new File(server().getDatapackRoot() + "data/mail/MailList.xml");
		if (!file.exists()) {
			LOG.warn("Cannot load email system - Missing file MailList.xml");
			return;
		}

		Document doc;
		try {
			doc = factory.newDocumentBuilder().parse(file);
		} catch (Exception ex) {
			LOG.warn("Could not parse MailList.xml file!", ex);
			return;
		}
		
		Node n = doc.getFirstChild();
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
			if (d.getNodeName().equals("mail")) {
				String mailId = d.getAttributes().getNamedItem("id").getNodeValue();
				String subject = d.getAttributes().getNamedItem("subject").getNodeValue();
				String maFile = d.getAttributes().getNamedItem("file").getNodeValue();
				
				File mailFile = new File(server().getDatapackRoot() + "data/mail/" + maFile);
				try (FileInputStream fis = new FileInputStream(mailFile);
					BufferedInputStream bis = new BufferedInputStream(fis)) {
					int bytes = bis.available();
					byte[] raw = new byte[bytes];
					
					bis.read(raw);
					String html = new String(raw, UTF_8);
					html = html.replaceAll(System.lineSeparator(), "\n");
					html = html.replace("%servermail%", email().getServerEmail());
					html = html.replace("%servername%", email().getServerName());
					
					_mailData.put(mailId, new MailContent(subject, html));
				} catch (IOException ex) {
					LOG.warn("There has been an error while reading {}!", maFile, ex);
				}
			}
		}
		LOG.info("Email system loaded.");
	}
	
	public static class MailContent {
		private final String _subject;
		private final String _text;
		
		public MailContent(String subject, String text) {
			_subject = subject;
			_text = text;
		}
		
		public String getSubject() {
			return _subject;
		}
		
		public String getText() {
			return _text;
		}
	}
	
	public MailContent getMailContent(String mailId) {
		return _mailData.get(mailId);
	}
	
	public static MailSystem getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final MailSystem INSTANCE = new MailSystem();
	}
}
