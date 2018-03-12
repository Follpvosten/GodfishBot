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
package xyz.karpador.godfishbot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVoice;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import xyz.karpador.godfishbot.commands.*;

/**
 *
 * @author Follpvosten
 */
public class GodfishPollingBot extends TelegramLongPollingBot {

	public static final Command[] COMMANDS = {
		new HelpCommand(), new AboutCommand(), new HelloCommand(), new KissCommand(),
		new ExplodeCommand(), new HugCommand(), new FlauschCommand(),
		new TrumpCommand(), new RikkaCommand(), new GifCommand(),
		new GofCommand(), new GiphyCommand(), new BitchslapCommand(),
		new VoiceCommand("truthahn", "truthahn.ogg"), new MLPCommand(),
		new VoiceCommand("heuldoch", "heuldoch.ogg"), new StarCommand(),
		new VoiceCommand("boahey", "boahey.ogg"), new PbCommand(),
		new VoiceCommand("eghugh", "eghughehhhh.mp3"),
		new WaCommand(), new YodaCommand(), new QuoteCommand(),
		new TestLoveCommand(), new RankLoveCommand(),
		new RateHotCommand("ratehot", 1),
		new RateHotCommand("rankhot", 5),
		new RateHotCommand("rankhotfull", 0)
	};

	private final HashMap<String, Command> commands;
	private String myName = null;

	public GodfishPollingBot() {
		commands = new HashMap<>();
		for (Command cmd : COMMANDS)
			commands.put(cmd.getName(), cmd);
		if (BotConfig.getInstance().getAlphacodersToken().startsWith("<")
			|| BotConfig.getInstance().getAlphacodersToken().isEmpty()) {
			System.err.println("Warning: No AlphaCoders Token specified in config file!");
			System.err.println("The /rikka and /wa commands will be disabled.");
			commands.get("rikka").disable();
			commands.get("wa").disable();
		}
		if (BotConfig.getInstance().getPixabayToken().startsWith("<")
			|| BotConfig.getInstance().getPixabayToken().isEmpty()) {
			System.err.println("Warning: No Pixabay Token specified in config file!");
			System.err.println("The /flausch and /pb commands will be disabled.");
			commands.get("flausch").disable();
			commands.get("pb").disable();
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (myName == null) {
			try {
				myName = getMe().getFirstName();
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		if (update.hasMessage() && update.getMessage().hasText()) {
			String msgTxt = update.getMessage().getText();
			if (msgTxt.startsWith("/")) {
				int spaceIndex = msgTxt.indexOf(" ");
				String cmdName;
				if (spaceIndex > 0)
					cmdName = msgTxt.substring(1, spaceIndex);
				else
					cmdName = msgTxt.substring(1);
				if (cmdName.contains("@")) {
					String[] cmdParts = cmdName.split("@");
					if (!cmdParts[1].equalsIgnoreCase(getBotUsername()))
						return;
					cmdName = cmdParts[0];
				}
				String params = null;
				if (spaceIndex > 0)
					params = msgTxt.substring(spaceIndex + 1);

				Command cmd = commands.get(cmdName.toLowerCase());
				if (cmd != null) {
					runCommand(cmd, params, update.getMessage());
				}
			}
		}
	}

	private InputStream getImageInputStream(String imageUrl) throws IOException {
		InputStream stream;
		if (!imageUrl.startsWith("http"))
			stream = getClass().getResource(imageUrl).openStream();
		else
			stream = new URL(imageUrl).openStream();
		return stream;
	}

	private void runCommand(Command cmd, String params, Message msg) {
		CommandResult result = cmd.getReply(params, msg, myName);
		if (result != null) {
			if (result.imageUrl != null) {
				// It's an image
				try {
					if (result.isGIF) {
						SendDocument document = new SendDocument()
							.setChatId(msg.getChatId());
							//.setNewDocument("blub.gif", stream);
						if(result.mediaId == null) {
							document.setNewDocument(
								"blub.gif",
								getImageInputStream(result.imageUrl)
							);
						} else {
							document.setDocument(result.mediaId);
						}
						if (result.text != null)
							document.setCaption(result.text);
						if (result.replyToId != -1)
							document.setReplyToMessageId(result.replyToId);
						cmd.processSendResult(result.imageUrl, sendDocument(document).getDocument().getFileId());
					} else {
						SendPhoto photo = new SendPhoto()
							.setChatId(msg.getChatId());
						if(result.mediaId == null) {
							photo.setNewPhoto("photo", getImageInputStream(result.imageUrl));
						} else {
							photo.setPhoto(result.mediaId);
						}
						if (result.text != null)
							photo.setCaption(result.text);
						if (result.replyToId != -1)
							photo.setReplyToMessageId(result.replyToId);
						cmd.processSendResult(result.imageUrl, sendPhoto(photo).getPhoto().get(0).getFileId());
					}
				} catch (IOException | TelegramApiException e) {
					e.printStackTrace();
				}
			} else if (result.audioUrl != null) {
				try {
					SendVoice voice = new SendVoice()
						.setChatId(msg.getChatId());
					if (result.mediaId == null) {
						InputStream stream =
							getClass().getResource(result.audioUrl).openStream();
						voice.setNewVoice("audio.ogg", stream);
					} else {
						voice.setVoice(result.mediaId);
					}
					if (result.text != null)
						voice.setCaption(result.text);
					if (result.replyToId != -1)
						voice.setReplyToMessageId(result.replyToId);
					cmd.processSendResult(result.audioUrl, sendVoice(voice).getVoice().getFileId());
				} catch (IOException | TelegramApiException e) {
					e.printStackTrace();
				}
			} else {
				// It's a text message
				SendMessage message = new SendMessage()
					.setChatId(msg.getChatId())
					.setText(result.text);
				if (result.replyToId != -1)
					message.setReplyToMessageId(result.replyToId);
				try {
					sendMessage(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getBotToken() {
		return BotConfig.getInstance().getTelegramBotToken();
	}

	@Override
	public String getBotUsername() {
		return "godfishbot";
	}

}