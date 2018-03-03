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
import java.net.HttpURLConnection;
import static java.net.HttpURLConnection.HTTP_OK;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.BotConfig;

/**
 *
 * @author Follpvosten
 */
public class GiphyCommand extends Command {

	@Override
	public String getName() {
		return "gyp";
	}

	@Override
	public String getUsage() {
		return "/gyp [filter]";
	}

	@Override
	public String getDescription() {
		return "Get a random GIF from Giphy (optionally filtered)";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		CommandResult result = new CommandResult("Powered by Giphy");
		try {
			String urlString =
				"http://api.giphy.com/v1/gifs/random?api_key="
					+ BotConfig.getInstance().getGiphyToken();
			if (params != null)
				urlString += "&tag=" + URLEncoder.encode(params, "UTF-8");
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() == HTTP_OK) {
				BufferedReader br =
					new BufferedReader(
						new InputStreamReader(con.getInputStream())
					);
				String httpResult = "";
				String line;
				while ((line = br.readLine()) != null)
					httpResult += line;
				JSONObject resultJson = new JSONObject(httpResult);
				if (resultJson.getJSONObject("meta").getInt("status") == HTTP_OK) {
					result.imageUrl =
						"http://i.giphy.com/" +
							resultJson.getJSONObject("data").getString("id") +
							".gif";
				} else {
					return null;
				}
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null;
		}

		result.isGIF = true;
		return result;
	}

}