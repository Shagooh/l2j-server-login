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
package com.l2jserver.loginserver;

import static com.l2jserver.loginserver.config.Configuration.server;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Game Server listener.
 * @author KenM
 * @version 2.6.1.0
 */
public class GameServerListener extends FloodProtectedListener {
	private static final List<GameServerThread> _gameServers = new CopyOnWriteArrayList<>();
	
	public GameServerListener() throws Exception {
		super(server().getGameServerHost(), server().getGameServerPort());
		setName(getClass().getSimpleName());
	}
	
	@Override
	public void addClient(Socket s) {
		_gameServers.add(new GameServerThread(s));
	}
	
	public void removeGameServer(GameServerThread gst) {
		_gameServers.remove(gst);
	}
}
