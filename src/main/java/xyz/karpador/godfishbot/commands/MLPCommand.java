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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class MLPCommand extends Command {

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
		return "Get a face from MyLittleFaceWhen.\n<tags>: Search tags separated by commas";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		if (params == null)
			return new CommandResult(getUsage() + "\n" + getDescription());
		CommandResult result = new CommandResult();
		try {
			if (!params.endsWith(","))
				params += ",";
			String urlString =
					"http://mylittlefacewhen.com/api/v3/face/"
							+ "?tags__all=" + URLEncoder.encode(params, "UTF-8")
							+ "&limit=100&accepted=true&format=json";
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() == HTTP_OK) {
				BufferedReader br =
						new BufferedReader(
								new InputStreamReader(con.getInputStream())
						);
				String httpResult = br.readLine();
				JSONArray jsonResult = new JSONObject(httpResult).getJSONArray("objects");
				int randIndex = Main.Random.nextInt(jsonResult.length());
				JSONObject jsonObj = jsonResult.getJSONObject(randIndex);
				result.imageUrl = "http://mylittlefacewhen.com" + jsonObj.getString("image");
				if (result.imageUrl.toLowerCase().endsWith(".gif"))
					result.isGIF = true;
			} else {
				result.text =
						"mylittlefacewhen.com returned error code " +
								con.getResponseCode() + ": " +
								con.getResponseMessage();
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

}