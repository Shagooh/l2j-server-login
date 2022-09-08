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
package com.l2jserver.loginserver.network.gameserverpackets;

import com.l2jserver.commons.network.BaseRecievePacket;
import com.l2jserver.loginserver.mail.MailSystem;

/**
 * @author mrTJO
 * @version 2.6.1.0
 */
public class RequestSendMail extends BaseRecievePacket {

	public RequestSendMail(byte[] decrypt) {
		super(decrypt);
		String accountName = readS();
		String mailId = readS();
		int argNum = readC();
		String[] args = new String[argNum];
		for (int i = 0; i < argNum; i++) {
			args[i] = readS();
		}
		MailSystem.getInstance().sendMail(accountName, mailId, args);
	}
}
