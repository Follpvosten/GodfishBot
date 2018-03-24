package xyz.karpador.godfishbot.commands;

import org.telegram.telegrambots.api.objects.Message;

public class ImageCommand extends Command {

	private final String name, mediaUrl;
	private String mediaId = null;
	private final boolean answer;

	public ImageCommand(String name, String mediaUrl, boolean answer) {
		this.name = name;
		this.mediaUrl = mediaUrl;
		this.answer = answer;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return "Get a specific image.";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		CommandResult result = new CommandResult();
		result.imageUrl = mediaUrl;
		result.mediaId = mediaId;
		if(answer) {
			if (message.isReply())
				result.replyToId = message.getReplyToMessage().getMessageId();
			else
				result.replyToId = message.getMessageId();
		}
		return result;
	}

	@Override
	public void processSendResult(String mediaUrl, String mediaId) {
		this.mediaId = mediaId;
	}
}
