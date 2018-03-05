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
import java.util.HashMap;

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
public class MLPCommand extends Command {

	private final HashMap<String, String> knownImages = new HashMap<>();

	@Override
	public String getName() {
		return "mlp";
	}

	@Override
	public String getUsage() {
		return "/mlp <tags>";
	}

	@Override
	public String getDescription() {
		return "Get an image from Derpibooru.\n<tags>: Search tags separated by commas";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		if (params == null)
			return new CommandResult(getUsage() + "\n" + getDescription());
		CommandResult result = new CommandResult();
		try {
			String urlString =
				"https://derpibooru.org/search.json?q="
					+ URLEncoder.encode(params, "UTF-8")
					.replace("%20", "+");
			URL url = new URL(urlString);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
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
				int totalHits = resultJson.getInt("total");
				if (totalHits < 1) {
					result.replyToId = message.getMessageId();
					result.text = "No results found.";
					return result;
				}
				int pageNumber = 1;
				if (totalHits > 20) // Generate a valid page number.
					pageNumber = Main.Random.nextInt((int) Math.ceil(totalHits / 20)) + 1;
				if (pageNumber > 1) {
					urlString += "&page=" + pageNumber;
					url = new URL(urlString);
					con = (HttpsURLConnection) url.openConnection();
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
				JSONArray hits = resultJson.getJSONArray("search");
				int imageIndex = Main.Random.nextInt(hits.length());
				JSONObject img = hits.getJSONObject(imageIndex);
				result.imageUrl = "https:" + img.getString("image");
				if (result.imageUrl.toLowerCase().endsWith(".gif"))
					result.isGIF = true;
				if(knownImages.containsKey(result.imageUrl))
					result.mediaId = knownImages.get(result.imageUrl);
				result.text = "From derpibooru.org";
					//+ "(Source: " + img.getString("source_url") + ")";
				if(!img.isNull("source_url"))
					result.text += " (Source: " + img.getString("source_url") + ")";
			} else {
				result.text =
					"derpibooru.org returned error code " +
						con.getResponseCode() + ": " +
						con.getResponseMessage();
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	@Override
	public void processSendResult(String mediaUrl, String mediaId) {
		knownImages.put(mediaUrl, mediaId);
	}
}