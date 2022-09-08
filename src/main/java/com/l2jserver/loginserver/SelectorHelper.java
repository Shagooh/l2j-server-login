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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.IPv4Filter;
import com.l2jserver.loginserver.network.L2LoginClient;
import com.l2jserver.loginserver.network.serverpackets.Init;
import com.l2jserver.mmocore.AcceptFilter;
import com.l2jserver.mmocore.ClientFactory;
import com.l2jserver.mmocore.MMOConnection;
import com.l2jserver.mmocore.MMOExecutor;
import com.l2jserver.mmocore.ReceivablePacket;

/**
 * Selector Helper.
 * @author KenM
 * @author Zoey76
 * @version 2.6.1.0
 */
public class SelectorHelper implements MMOExecutor<L2LoginClient>, ClientFactory<L2LoginClient>, AcceptFilter {
	
	private static final Logger LOG = LoggerFactory.getLogger(SelectorHelper.class);
	
	private final ThreadPoolExecutor _generalPacketsThreadPool;
	
	private final IPv4Filter _ipv4filter;
	
	public SelectorHelper() {
		_generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, SECONDS, new LinkedBlockingQueue<>());
		_ipv4filter = new IPv4Filter();
	}
	
	@Override
	public void execute(ReceivablePacket<L2LoginClient> packet) {
		_generalPacketsThreadPool.execute(packet);
	}
	
	@Override
	public L2LoginClient create(MMOConnection<L2LoginClient> con) {
		L2LoginClient client = new L2LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}
	
	@Override
	public boolean accept(SocketChannel sc) {
		try {
			return _ipv4filter.accept(sc) && !LoginController.getInstance().isBannedAddress(sc.socket().getInetAddress());
		} catch (Exception ex) {
			LOG.error("Invalid address {}!", sc.socket().getInetAddress(), ex);
		}
		return false;
	}
}
