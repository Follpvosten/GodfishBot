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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.Main;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Follpvosten
 */
public class GofCommand extends Command {

	@Override
	public String getName() {
		return "gof";
	}

	@Override
	public String getUsage() {
		return "/gof <tag>";
	}

	@Override
	public String getDescription() {
		return "Get a GIF from gifbase (by search query)";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		if (params == null)
			return new CommandResult(getUsage() + "\n" + getDescription());
		CommandResult result = new CommandResult();
		try {
			String urlString = "https://gifbase.com/tag/" + params + "?format=json";
			URL url = new URL(urlString);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setConnectTimeout(4000);
			if (con.getResponseCode() == HTTP_OK) {
				BufferedReader br =
					new BufferedReader(
						new InputStreamReader(con.getInputStream())
					);
				StringBuilder httpResult = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null)
					httpResult.append(line);
				JSONObject resultJson = new JSONObject(httpResult.toString());
				int pageCount = resultJson.optInt("page_count", 1);
				if (pageCount > 1) {
					int page = Main.Random.nextInt(pageCount - 1) + 1;
					if (page != resultJson.getInt("page_current")) {
						urlString += "&p=" + page;
						url = new URL(urlString);
						con = (HttpsURLConnection) url.openConnection();
						con.setConnectTimeout(4000);
						if (con.getResponseCode() == HTTP_OK) {
							br = new BufferedReader(
								new InputStreamReader(con.getInputStream())
							);
							httpResult.setLength(0);
							while ((line = br.readLine()) != null)
								httpResult.append(line);
							resultJson = new JSONObject(httpResult.toString());
						}
					}
				}
				JSONArray gifs = resultJson.optJSONArray("gifs");
				if (gifs != null) {
					JSONObject gif = gifs.getJSONObject(Main.Random.nextInt(gifs.length()));
					result.imageUrl = gif.getString("url");
				} else
					return null;
			} else {
				return new CommandResult(
					"gifbase.com responded with error code: "
					+ con.getResponseCode() + ": " + con.getResponseMessage()
				);
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null;
		}
		if (result.imageUrl == null) return null;
		result.isGIF = true;
		return result;
	}

}