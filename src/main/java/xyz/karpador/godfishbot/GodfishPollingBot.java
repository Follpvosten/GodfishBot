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
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import xyz.karpador.godfishbot.commands.*;

/**
 *
 * @author Follpvosten
 */
public class GodfishPollingBot extends TelegramLongPollingBot {
    
    public static final Command[] COMMANDS =
    { new HelloCommand(), new HelpCommand(), new KissCommand(),
      new ExplodeCommand(), new HugCommand(), new FlauschCommand(),
      new TrumpCommand(), new RikkaCommand(), new GifCommand(),
      new GofCommand(), new GiphyCommand(), new BitchslapCommand(),
      new TruthahnCommand(), new HeuldochCommand(), new BoaheyCommand() };
    
    private final HashMap<String, Command> commands;
    private String myName = null;
    
    public GodfishPollingBot() {
	commands = new HashMap<>();
	for(Command cmd : COMMANDS)
	    commands.put(cmd.getName(), cmd);
        if(BotConfig.getInstance().getAlphacodersToken().startsWith("<")) {
            System.err.println("Warning: No AlphaCoders Token specified in config file!");
            System.err.println("The /rikka command will be disabled.");
            commands.get("rikka").disable();
        }
        if(BotConfig.getInstance().getPixabayToken().startsWith("<")) {
            System.err.println("Warning: No Pixabay Token specified in config file!");
            System.err.println("The /flausch command will be disabled.");
            commands.get("flausch").disable();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
	if(myName == null) {
	    try {
		myName = getMe().getFirstName();
	    } catch(TelegramApiException e) {
		e.printStackTrace();
	    }
	}
	if(update.hasMessage() && update.getMessage().hasText()) {
	    String msgTxt = update.getMessage().getText();
	    if(msgTxt.startsWith("/")) {
		int spaceIndex = msgTxt.indexOf(" ");
		String cmdName;
		if(spaceIndex > 0)
		    cmdName = msgTxt.substring(1, spaceIndex);
		else
		    cmdName = msgTxt.substring(1);
		if(cmdName.contains("@")) {
		    String[] cmdParts = cmdName.split("@");
		    if(!cmdParts[1].equalsIgnoreCase(getBotUsername()))
			return;
		    cmdName = cmdParts[0];
		}
		String params = null;
		if(spaceIndex > 0)
		    params = msgTxt.substring(spaceIndex + 1);
		
		Command cmd = commands.get(cmdName.toLowerCase());
		if(cmd != null) {
		    CommandResult result = cmd.getReply(params, update.getMessage(), myName);
		    if(result != null) {
			if(result.imageUrl != null) {
			    // It's an image
			    try {
				InputStream stream;
				if(!result.imageUrl.startsWith("http"))
				    stream = getClass().getResource(result.imageUrl).openStream();
				else
				    stream = new URL(result.imageUrl).openStream();
				if(result.isGIF) {
				    SendDocument document = new SendDocument()
					.setChatId(update.getMessage().getChatId())
					.setNewDocument("blub.gif", stream);
				    if(result.text != null)
					document.setCaption(result.text);
				    if(result.replyToId != -1)
					document.setReplyToMessageId(result.replyToId);
				    sendDocument(document);
				} else {
				    SendPhoto photo = new SendPhoto()
					.setChatId(update.getMessage().getChatId())
					.setNewPhoto("photo", stream);
				    if(result.text != null)
					photo.setCaption(result.text);
				    if(result.replyToId != -1)
					photo.setReplyToMessageId(result.replyToId);
				    sendPhoto(photo);
				}
			    } catch(IOException | TelegramApiException e) {
				e.printStackTrace();
			    }
			} else if(result.audioUrl != null) {
			    try {
				InputStream stream =
					getClass().getResource(result.audioUrl).openStream();
				SendAudio audio = new SendAudio()
					.setChatId(update.getMessage().getChatId())
					.setNewAudio("audio.ogg", stream);
				if(result.text != null)
				    audio.setCaption(result.text);
				if(result.replyToId != -1)
				    audio.setReplyToMessageId(result.replyToId);
				cmd.processSendResult(sendAudio(audio).getAudio().getFileId());
			    } catch(IOException | TelegramApiException e) {
				    e.printStackTrace();
			    }
			} else {
			    // It's a text message
			    SendMessage message = new SendMessage()
				.setChatId(update.getMessage().getChatId())
				.setText(result.text);
			    if(result.replyToId != -1)
				message.setReplyToMessageId(result.replyToId);
			    try {
				sendMessage(message);
			    } catch(TelegramApiException e) {
				e.printStackTrace();
			    }
			}
		    }
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
