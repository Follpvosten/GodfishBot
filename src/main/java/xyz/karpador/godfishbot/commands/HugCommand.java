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
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class HugCommand extends Command {

	private static final String[] HUG_MESSAGES =
			{"%1$s hugged %2$s", "%1$s cuddled with %2$s", "%1$s hugged %2$s tightly",
					"%2$s got crushed by %1$s's Bewear-like hug"};

	@Override
	public String getName() {
		return "hug";
	}

	@Override
	public String getUsage() {
		return "/hug [target]";
	}

	@Override
	public String getDescription() {
		return "Hug someone (or get a hug)";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		CommandResult result =
				new CommandResult(
						HUG_MESSAGES[Main.Random.nextInt(HUG_MESSAGES.length)]
				);
		if (params != null) {
			result.text = String.format(result.text, message.getFrom().getFirstName(), params);
		} else {
			result.text = String.format(result.text, myName, message.getFrom().getFirstName());
		}
		return result;
	}

}