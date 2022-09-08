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

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

import java.util.Set;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;

/**
 * Server Configuration.
 * @author Zoey76
 * @version 2.6.1.1
 */
@Sources({
	"file:${L2J_HOME}/custom/login/config/server.properties",
	"file:./config/server.properties",
	"classpath:config/server.properties"
})
@LoadPolicy(MERGE)
@HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface ServerConfiguration extends Config {
	
	@Key("EnableUPnP")
	boolean isUPnPEnabled();
	
	@Key("Host")
	String getHost();
	
	@Key("Port")
	int getPort();
	
	@Key("GameServerHost")
	String getGameServerHost();
	
	@Key("GameServerPort")
	int getGameServerPort();
	
	@Key("ServerVersions")
	Set<Integer> getServerVersions();
	
	@Key("LoginTryBeforeBan")
	int getLoginTryBeforeBan();
	
	@Key("LoginBlockAfterBan")
	int getLoginBlockAfterBan();
	
	@Key("AcceptNewGameServer")
	boolean isAcceptNetGameServer();
	
	@Key("EnableFloodProtection")
	boolean isFloodProtectionEnabled();
	
	@Key("FastConnectionLimit")
	int getFastConnectionLimit();
	
	@Key("NormalConnectionTime")
	int getNormalConnectionTime();
	
	@Key("FastConnectionTime")
	int getFastConnectionTime();
	
	@Key("MaxConnectionPerIP")
	int getMaxConnectionPerIP();
	
	@Key("AccountInactiveAccessLevel")
	int getAccountInactiveAccessLevel();

	@Key("ShowLicence")
	boolean showLicense();
	
	@Key("AutoCreateAccounts")
	boolean autoCreateAccounts();
	
	@Key("AutoCreateAccountsAccessLevel")
	int autoCreateAccountsAccessLevel();
	
	@Key("DatapackRoot")
	String getDatapackRoot();
	
	@Key("Debug")
	boolean isDebug();
	
	@Key("LoginRestartSchedule")
	boolean isLoginRestartEnabled();
	
	@Key("LoginRestartTime")
	int getLoginRestartTime();
}