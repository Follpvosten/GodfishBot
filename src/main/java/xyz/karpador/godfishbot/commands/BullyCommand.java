package xyz.karpador.godfishbot.commands;

import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.Main;

import java.util.HashMap;

public class BullyCommand extends Command {

	private static final String[] PICS = { "/images/bully.jpg", "/images/bully2.jpg" };
	private final HashMap<String, String> mediaIds;

	public BullyCommand() {
		mediaIds = new HashMap<>();
		for(String pic : PICS) {
			mediaIds.put(pic, null);
		}
	}

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
		result.imageUrl = PICS[Main.Random.nextInt(PICS.length)];
		result.mediaId = mediaIds.get(result.imageUrl);
		if (message.isReply())
			result.replyToId = message.getReplyToMessage().getMessageId();
		else
			result.replyToId = message.getMessageId();
		return result;
	}

	@Override
	public void processSendResult(String mediaUrl, String mediaId) {
		mediaIds.put(mediaUrl, mediaId);
	}
}