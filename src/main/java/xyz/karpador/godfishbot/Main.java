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

import java.util.Random;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 * @author Follpvosten
 */
public class Main {
	public static final Random Random = new Random();

	public static void main(String[] args) {
		System.out.println("Initializing ApiContext...");
		ApiContextInitializer.init();

		System.out.println("Creating BotsApi...");
		TelegramBotsApi botsApi = new TelegramBotsApi();

		System.out.println("Loading config file...");
		BotConfig.getInstance().init();

		if (BotConfig.getInstance().getTelegramBotToken().startsWith("<")) {
			System.err.println("Fatal: No Telegram Bot API Token defined in config file!");
			System.err.println("Please refer to the documentation.");
			System.exit(0);
		}

		try {
			System.out.println("Starting bot...");
			botsApi.registerBot(new GodfishPollingBot());
			System.out.println("Started!");
		} catch (TelegramApiException e) {
			System.out.println("Start failed:");
			e.printStackTrace();
		}
	}
}