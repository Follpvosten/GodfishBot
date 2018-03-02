/*
 * Copyright (C) 2017 Follpvosten
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package xyz.karpador.godfishbot.commands;

import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.GodfishPollingBot;

/**
 *
 * @author Follpvosten
 */
public class HelpCommand extends Command {

	private static final String USAGE = "Available commands:\n%sArguments: <required> [optional]";

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "/help [command]";
	}

	@Override
	public String getDescription() {
		return "Get a list of commands (or more info about one command)";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		if (params == null) {
			String commands = "";
			for (Command cmd : GodfishPollingBot.COMMANDS) {
				commands += cmd.getUsage();
				if (cmd.isDisabled()) commands += " (DISABLED)";
				commands += "\n";
			}
			return new CommandResult(String.format(USAGE, commands));
		} else {
			for (Command cmd : GodfishPollingBot.COMMANDS) {
				if (cmd.getName().equals(params))
					return new CommandResult(cmd.getUsage() + "\n" + cmd.getDescription());
			}
			return null;
		}
	}

}