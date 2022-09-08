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
package com.l2jserver.loginserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.loginserver.network.L2LoginClient;
import com.l2jserver.mmocore.ReceivablePacket;

/**
 * Login Client abstract packet.
 * @author KenM
 * @version 2.6.1.0
 */
public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient> {
	
	private static final Logger LOG = LoggerFactory.getLogger(L2LoginClientPacket.class);
	
	@Override
	protected final boolean read() {
		try {
			return readImpl();
		} catch (Exception ex) {
			LOG.error("Error reading {}!", getClass().getSimpleName(), ex);
			return false;
		}
	}
	
	protected abstract boolean readImpl();
}
