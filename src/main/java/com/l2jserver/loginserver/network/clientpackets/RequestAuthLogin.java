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

import static com.l2jserver.loginserver.config.Configuration.server;

import java.net.InetAddress;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.loginserver.GameServerTable.GameServerInfo;
import com.l2jserver.loginserver.LoginController;
import com.l2jserver.loginserver.LoginController.AuthLoginResult;
import com.l2jserver.loginserver.model.AccountInfo;
import com.l2jserver.loginserver.network.L2LoginClient;
import com.l2jserver.loginserver.network.L2LoginClient.LoginClientState;
import com.l2jserver.loginserver.network.serverpackets.AccountKicked;
import com.l2jserver.loginserver.network.serverpackets.AccountKicked.AccountKickedReason;
import com.l2jserver.loginserver.network.serverpackets.LoginFail.LoginFailReason;
import com.l2jserver.loginserver.network.serverpackets.LoginOk;
import com.l2jserver.loginserver.network.serverpackets.ServerList;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an password.
 * @version 2.6.1.0
 */
public class RequestAuthLogin extends L2LoginClientPacket {
	
	private static final Logger LOG = LoggerFactory.getLogger(RequestAuthLogin.class);
	
	private final byte[] _raw = new byte[128];
	
	private String _user;
	
	private String _password;
	
	private int _ncotp;
	
	public String getPassword() {
		return _password;
	}
	
	public String getUser() {
		return _user;
	}
	
	public int getOneTimePassword() {
		return _ncotp;
	}
	
	@Override
	public boolean readImpl() {
		if (super._buf.remaining() >= 128) {
			readB(_raw);
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		byte[] decrypted;
		final L2LoginClient client = getClient();
		try {
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
		} catch (Exception ex) {
			LOG.warn("There has been an error trying to login!", ex);
			return;
		}
		
		try {
			_user = new String(decrypted, 0x5E, 14).trim().toLowerCase();
			_password = new String(decrypted, 0x6C, 16).trim();
			_ncotp = decrypted[0x7c];
			_ncotp |= decrypted[0x7d] << 8;
			_ncotp |= decrypted[0x7e] << 16;
			_ncotp |= decrypted[0x7f] << 24;
		} catch (Exception ex) {
			LOG.warn("There has been an error parsing credentials!", ex);
			return;
		}
		
		InetAddress clientAddr = getClient().getConnection().getInetAddress();
		
		final LoginController lc = LoginController.getInstance();
		AccountInfo info = lc.retrieveAccountInfo(clientAddr, _user, _password);
		if (info == null) {
			// user or pass wrong
			client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
			return;
		}
		
		AuthLoginResult result = lc.tryCheckinAccount(client, clientAddr, info);
		switch (result) {
			case AUTH_SUCCESS -> {
				client.setAccount(info.getLogin());
				client.setState(LoginClientState.AUTHED_LOGIN);
				client.setSessionKey(lc.assignSessionKeyToClient(info.getLogin(), client));
				lc.getCharactersOnAccount(info.getLogin());
				if (server().showLicense()) {
					client.sendPacket(new LoginOk(getClient().getSessionKey()));
				} else {
					getClient().sendPacket(new ServerList(getClient()));
				}
			}
			case INVALID_PASSWORD -> client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
			case ACCOUNT_INACTIVE -> client.close(LoginFailReason.REASON_INACTIVE);
			case ACCOUNT_BANNED -> {
				client.close(new AccountKicked(AccountKickedReason.REASON_PERMANENTLY_BANNED));
			}
			case ALREADY_ON_LS -> {
				L2LoginClient oldClient = lc.getAuthedClient(info.getLogin());
				if (oldClient != null) {
					// kick the other client
					oldClient.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
					lc.removeAuthedLoginClient(info.getLogin());
				}
				// kick also current client
				client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
			}
			case ALREADY_ON_GS -> {
				GameServerInfo gsi = lc.getAccountOnGameServer(info.getLogin());
				if (gsi != null) {
					client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);

					// kick from there
					if (gsi.isAuthed()) {
						gsi.getGameServerThread().kickPlayer(info.getLogin());
					}
				}
			}
		}
	}
}
