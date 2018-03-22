package xyz.karpador.godfishbot.commands;

import org.telegram.telegrambots.api.objects.Message;

public class BullyCommand extends Command {

	private String mediaId = null;

	@Override
	public String getName() {
		return "bully";
	}

	@Override
	public String getDescription() {
		return "Bully someone";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		CommandResult result = new CommandResult();
		result.imageUrl = "/images/bully.jpg";
		result.mediaId = mediaId;
		if (message.isReply())
			result.replyToId = message.getReplyToMessage().getMessageId();
		else
			result.replyToId = message.getMessageId();
		return result;
	}

	@Override
	public void processSendResult(String mediaUrl, String mediaId) {
		this.mediaId = mediaId;
	}
}