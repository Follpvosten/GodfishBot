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
public class ExplodeCommand extends Command {
    
    private static final String[] EXPLODE_AT_MESSAGES =
    { "%1$s exploded all over %2$s's face", "%1$s blew up at %2$s", "%1$s kamikaze'd %2$s",
      "%1$s blew up %2$s's house", "%1$s used Explosion! %2$s fainted!",
      "%1$s ate a grenade from %2$s" };
    private static final String[] EXPLODE_MESSAGES =
    { "%1$s exploded", "%1$s blew up", "%1$s detonated", "%1$s used Explosion!",
      "%1$s used Self-Destruct!", "%1$s played minesweeper in real-life",
      "%1$s ate a grenade" };

    @Override
    public String getName() {
	return "explode";
    }

    @Override
    public String getUsage() {
	return "/explode [target]";
    }

    @Override
    public String getDescription() {
	return "Explode (at someone, optionally)";
    }

    @Override
    public CommandResult getReply(String params, Message message, String myName) {
	CommandResult result = new CommandResult();
	if(params == null) {
	    result.text =
		    String.format(
			EXPLODE_MESSAGES[Main.Random.nextInt(EXPLODE_MESSAGES.length)],
			message.getFrom().getFirstName()
		    );
	} else {
	    result.text =
		    String.format(
			EXPLODE_AT_MESSAGES[Main.Random.nextInt(EXPLODE_AT_MESSAGES.length)],
			message.getFrom().getFirstName(), params
		    );
	}
	return result;
    }
    
}
