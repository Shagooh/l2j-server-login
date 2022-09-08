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

/**
 * Hacking Exception.
 * @version 2.6.1.0
 */
public class HackingException extends Exception {
	
	private static final long serialVersionUID = 4050762693478463029L;
	
	private final String _ip;
	
	private final int _connects;
	
	public HackingException(String ip, int connects) {
		_ip = ip;
		_connects = connects;
	}
	
	public String getIP() {
		return _ip;
	}
	
	public int getConnects() {
		return _connects;
	}
}
