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
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.BotConfig;
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class QuoteCommand extends Command {

	@Override
	public String getName() {
		return "quote";
	}

	@Override
	public String getDescription() {
		return "Get a random quote by famous people or from movies";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		try {
			String cat = Main.Random.nextInt(2) == 0 ? "famous" : "movies";
			URL url = new URL("https://andruxnet-random-famous-quotes.p.mashape.com/?cat=" + cat + "&count=1");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestProperty("X-Mashape-Key", BotConfig.getInstance().getMashapeToken());
			con.setRequestProperty("Accept", "application/json");
			con.setReadTimeout(5000);
			if (con.getResponseCode() == HTTP_OK) {
				BufferedReader br =
						new BufferedReader(
								new InputStreamReader(con.getInputStream())
						);
				String result = br.readLine();
				JSONObject resultJson = new JSONObject(result);
				String cmdResult = "„" + resultJson.getString("quote") + "“ - "
						+ resultJson.getString("author");
				return new CommandResult(cmdResult);
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}