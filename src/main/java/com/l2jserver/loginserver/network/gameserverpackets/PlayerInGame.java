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

import static com.l2jserver.loginserver.config.Configuration.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.dao.ServerNameDAO;
import com.l2jserver.commons.network.BaseRecievePacket;
import com.l2jserver.loginserver.GameServerThread;

/**
 * Player In Game packet.
 * @author -Wooden-
 * @author Zoey76
 * @version 2.6.1.0
 */
public class PlayerInGame extends BaseRecievePacket {
	private static final Logger LOG = LoggerFactory.getLogger(PlayerInGame.class);
	
	public PlayerInGame(byte[] decrypt, GameServerThread server) {
		super(decrypt);
		int size = readH();
		for (int i = 0; i < size; i++) {
			String account = readS();
			server.addAccountOnGameServer(account);
			if (server().isDebug()) {
				LOG.info("Account {} logged in Game Server {}[{}].", account, ServerNameDAO.getServer(server.getServerId()), server.getServerId());
			}
			server.broadcastToTelnet("Account " + account + " logged in GameServer " + server.getServerId());
		}
	}
}
