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

/**
 *
 * @author Follpvosten
 */
public class BitchslapCommand extends Command {

	private String mediaId = null;

	@Override
	public String getName() {
		return "bitchslap";
	}

	@Override
	public String getUsage() {
		return "/bitchslap [target]";
	}

	@Override
	public String getDescription() {
		return "Bitchslap someone (or just get a bitchslap GIF)";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		CommandResult result = new CommandResult();

		if (message.getReplyToMessage() != null) {
			result.replyToId = message.getReplyToMessage().getMessageId();
			result.imageUrl = "/images/bitchslap.gif";
			if(mediaId != null)
				result.mediaId = mediaId;
			result.isGIF = true;
		} else {
			if (params != null) {
				result.text =
					message.getFrom().getFirstName()
						+ " bitch slapped " + params;
			} else {
				result.imageUrl = "/images/bitchslap.gif";
				if(mediaId != null)
					result.mediaId = mediaId;
				result.isGIF = true;
			}
		}
		return result;
	}

	@Override
	public void processSendResult(String mediaUrl, String mediaId) {
		if (this.mediaId == null)
			this.mediaId = mediaId;
	}
}