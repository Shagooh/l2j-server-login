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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.network.BaseRecievePacket;
import com.l2jserver.loginserver.GameServerTable;
import com.l2jserver.loginserver.GameServerTable.GameServerInfo;
import com.l2jserver.loginserver.GameServerThread;

/**
 * Change Password packet.
 * @author Nik
 * @version 2.6.1.0
 */
public class ChangePassword extends BaseRecievePacket {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChangePassword.class);
	
	private static GameServerThread gst = null;
	
	public ChangePassword(byte[] decrypt) {
		super(decrypt);
		
		String accountName = readS();
		String characterName = readS();
		String curpass = readS();
		String newpass = readS();
		
		for (GameServerInfo gsi : GameServerTable.getInstance().getRegisteredGameServers().values()) {
			if ((gsi.getGameServerThread() != null) && gsi.getGameServerThread().hasAccountOnGameServer(accountName)) {
				gst = gsi.getGameServerThread();
			}
		}
		
		if (gst == null) {
			return;
		}
		
		if ((curpass == null) || (newpass == null)) {
			gst.ChangePasswordResponse((byte) 0, characterName, "Invalid password data! Try again.");
		} else {
			try {
				final var md = MessageDigest.getInstance("SHA");
				final var raw = md.digest(curpass.getBytes(UTF_8));
				String curpassEnc = Base64.getEncoder().encodeToString(raw);
				String pass = null;
				int passUpdated;
				
				try (var con = ConnectionFactory.getInstance().getConnection();
					var ps = con.prepareStatement("SELECT password FROM accounts WHERE login=?")) {
					ps.setString(1, accountName);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							pass = rs.getString("password");
						}
					}
				}
				
				if (curpassEnc.equals(pass)) {
					final var password = md.digest(newpass.getBytes(UTF_8));
					final var newPasswordEnc = Base64.getEncoder().encodeToString(password);
					try (var con = ConnectionFactory.getInstance().getConnection();
						var ps = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?")) {
						ps.setString(1, newPasswordEnc);
						ps.setString(2, accountName);
						passUpdated = ps.executeUpdate();
					}
					
					LOG.info("The password for account {} has been changed from {} to {}.", accountName, curpassEnc, newPasswordEnc);
					if (passUpdated > 0) {
						gst.ChangePasswordResponse((byte) 1, characterName, "You have successfully changed your password!");
					} else {
						gst.ChangePasswordResponse((byte) 0, characterName, "The password change was unsuccessful!");
					}
				} else {
					gst.ChangePasswordResponse((byte) 0, characterName, "The typed current password doesn't match with your current one.");
				}
			} catch (Exception ex) {
				LOG.warn("Error while changing password for account {} requested by player {}!", accountName, characterName, ex);
			}
		}
	}
}