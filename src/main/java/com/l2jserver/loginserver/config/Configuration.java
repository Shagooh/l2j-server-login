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
package com.l2jserver.loginserver.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Configuration.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class Configuration {
	
	private static final EmailConfiguration email = ConfigFactory.create(EmailConfiguration.class);
	
	private static final ServerConfiguration server = ConfigFactory.create(ServerConfiguration.class);
	
	private static final DatabaseConfiguration database = ConfigFactory.create(DatabaseConfiguration.class);
	
	private static final MMOConfiguration mmo = ConfigFactory.create(MMOConfiguration.class);
	
	private static final TelnetConfiguration telnet = ConfigFactory.create(TelnetConfiguration.class);
	
	private Configuration() {
		// Do nothing.
	}
	
	public static EmailConfiguration email() {
		return email;
	}
	
	public static ServerConfiguration server() {
		return server;
	}
	
	public static DatabaseConfiguration database() {
		return database;
	}
	
	public static MMOConfiguration mmo() {
		return mmo;
	}
	
	public static TelnetConfiguration telnet() {
		return telnet;
	}
}
