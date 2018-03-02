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

import java.util.HashMap;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class StarCommand extends Command {

	private static final String PREFIX = "star";
	private static final int STAR_COUNT = 58;

	private final HashMap<String, String> mediaIds;

	public StarCommand() {
		mediaIds = new HashMap<>();
	}

	@Override
	public String getName() {
		return "star";
	}

	@Override
	public String getDescription() {
		return "Get a beautiful star";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		CommandResult result = new CommandResult();
		int starNumber = Main.Random.nextInt(STAR_COUNT);
		result.imageUrl = "/images/" + PREFIX + starNumber + ".jpg";
		if (mediaIds.containsKey(result.imageUrl))
			result.mediaId = mediaIds.get(result.imageUrl);
		return result;
	}

	@Override
	public void processSendResult(String mediaUrl, String mediaId) {
		mediaIds.put(mediaUrl, mediaId);
	}

}