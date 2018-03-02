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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.net.HttpURLConnection.HTTP_OK;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.BotConfig;

/**
 *
 * @author Follpvosten
 */
public class YodaCommand extends Command {

	@Override
	public String getName() {
		return "yoda";
	}

	@Override
	public String getUsage() {
		return "/yoda <sentence>";
	}

	@Override
	public String getDescription() {
		return "Get your sentence transformed into yoda speak.";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		if (params == null) return new CommandResult("Please provide a sentence.");
		try {
			URL url = new URL("https://yoda.p.mashape.com/yoda?sentence="
					+ URLEncoder.encode(params, "UTF-8"));
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestProperty("X-Mashape-Key", BotConfig.getInstance().getMashapeToken());
			con.setRequestProperty("Accept", "text/plain");
			if (con.getResponseCode() == HTTP_OK) {
				BufferedReader br =
						new BufferedReader(
								new InputStreamReader(con.getInputStream())
						);
				String result = br.readLine();
				return new CommandResult(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}