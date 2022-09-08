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
import static com.l2jserver.loginserver.network.L2JGameServerPacketHandler.GameServerState.BF_CONNECTED;
import static javax.crypto.Cipher.DECRYPT_MODE;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.network.BaseRecievePacket;
import com.l2jserver.commons.security.crypt.NewCrypt;
import com.l2jserver.loginserver.GameServerThread;

/**
 * Blowfish Key.
 * @author -Wooden-
 * @version 2.6.1.0
 */
public class BlowFishKey extends BaseRecievePacket {
	
	private static final Logger LOG = LoggerFactory.getLogger(BlowFishKey.class);
	
	public BlowFishKey(byte[] decrypt, GameServerThread server) {
		super(decrypt);
		int size = readD();
		byte[] tempKey = readB(size);
		try {
			byte[] tempDecryptKey;
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(DECRYPT_MODE, server.getPrivateKey());
			tempDecryptKey = rsaCipher.doFinal(tempKey);
			// there are nulls before the key we must remove them
			int i = 0;
			int len = tempDecryptKey.length;
			for (; i < len; i++) {
				if (tempDecryptKey[i] != 0) {
					break;
				}
			}
			byte[] key = new byte[len - i];
			System.arraycopy(tempDecryptKey, i, key, 0, len - i);
			
			server.SetBlowFish(new NewCrypt(key));
			if (server().isDebug()) {
				LOG.info("New BlowFish key received, Blowfish Engine initialized:");
			}
			server.setLoginConnectionState(BF_CONNECTED);
		} catch (Exception ex) {
			LOG.error("There has been an error while decrypting blowfish key (RSA)!", ex);
		}
	}
}
