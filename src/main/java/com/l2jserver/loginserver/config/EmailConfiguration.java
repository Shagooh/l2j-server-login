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

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;

/**
 * Email Configuration.
 * @author Zoey76
 * @version 2.6.1.1
 */
@Sources({
	"file:${L2J_HOME}/custom/login/config/email.properties",
	"file:./config/email.properties",
	"classpath:config/email.properties"
})
@LoadPolicy(MERGE)
@HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface EmailConfiguration extends Config {
	
	@Key("EnableEmail")
	boolean isEnabled();
	
	@Key("ServerName")
	String getServerName();
	
	@Key("ServerEmail")
	String getServerEmail();
	
	@Key("SelectQuery")
	String getSelectQuery();
	
	@Key("DatabaseField")
	String getDatabaseField();
	
	@Key("Host")
	String getHost();
	
	@Key("Port")
	int getPort();
	
	@Key("SmtpAuthRequired")
	boolean isSmtpAuthRequired();
	
	@Key("SmtpFactory")
	String getSmtpFactory();
	
	@Key("SmtpFactoryCallback")
	boolean smtpFactoryCallback();
	
	@Key("SmtpUsername")
	String getSmtpUsername();
	
	@Key("SmtpPassword")
	String getSmtpPassword();
	
	@Key("SystemAddress")
	String getSystemAddress();
}