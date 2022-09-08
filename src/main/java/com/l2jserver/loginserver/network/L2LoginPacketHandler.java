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
package com.l2jserver.loginserver.network;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.loginserver.network.L2LoginClient.LoginClientState;
import com.l2jserver.loginserver.network.clientpackets.AuthGameGuard;
import com.l2jserver.loginserver.network.clientpackets.RequestAuthLogin;
import com.l2jserver.loginserver.network.clientpackets.RequestServerList;
import com.l2jserver.loginserver.network.clientpackets.RequestServerLogin;
import com.l2jserver.mmocore.PacketHandler;
import com.l2jserver.mmocore.ReceivablePacket;

/**
 * Handler for packets received by Login Server.
 * @author KenM
 * @version 2.6.1.0
 */
public final class L2LoginPacketHandler implements PacketHandler<L2LoginClient> {
	
	private static final Logger LOG = LoggerFactory.getLogger(L2LoginPacketHandler.class);
	
	@Override
	public ReceivablePacket<L2LoginClient> handlePacket(ByteBuffer buf, L2LoginClient client) {
		int opcode = buf.get() & 0xFF;
		
		ReceivablePacket<L2LoginClient> packet = null;
		LoginClientState state = client.getState();
		
		switch (state) {
			case CONNECTED:
				if (opcode == 0x07) {
					packet = new AuthGameGuard();
				} else {
					debugOpcode(opcode, state);
				}
				break;
			case AUTHED_GG:
				if (opcode == 0x00) {
					packet = new RequestAuthLogin();
				} else {
					debugOpcode(opcode, state);
				}
				break;
			case AUTHED_LOGIN:
				switch (opcode) {
					case 0x02 -> packet = new RequestServerLogin();
					case 0x05 -> packet = new RequestServerList();
					default -> debugOpcode(opcode, state);
				}
				break;
		}
		return packet;
	}
	
	private void debugOpcode(int opcode, LoginClientState state) {
		LOG.warn("Unknown Opcode {} for state {}!", opcode, state.name());
	}
}
