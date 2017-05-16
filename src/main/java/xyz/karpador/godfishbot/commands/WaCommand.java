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
import java.io.InputStreamReader;
import static java.net.HttpURLConnection.HTTP_OK;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.BotConfig;
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class WaCommand extends Command {

    @Override
    public String getName() {
	return "wa";
    }

    @Override
    public String getUsage() {
	return "/wa [search query]";
    }

    @Override
    public String getDescription() {
	return "Get a wallpaper from Wallpaper Abyss (optionally filtered).";
    }

    @Override
    public CommandResult getReply(String params, Message message, String myName) {
	CommandResult result = new CommandResult();
	if(params != null) {
	    // Get a command by search term
	    try {
		URL url = new URL(
			"https://wall.alphacoders.com/api2.0/get.php"
			+ "?auth=" + BotConfig.getInstance().getAlphacodersToken()
			+ "&method=search&term=" + URLEncoder.encode(params, "UTF-8")
		);
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		if(con.getResponseCode() == HTTP_OK) {
		    BufferedReader br = 
			new BufferedReader(
			    new InputStreamReader(con.getInputStream())
			);
		    String httpResult = br.readLine();
		    JSONObject resultJson = new JSONObject(httpResult);
		    int totalHits = resultJson.getInt("total_match");
		    if(totalHits < 1) {
			result.replyToId = message.getMessageId();
			result.text = "No results found.";
			return result;
		    }
		    int pageNumber = 1;
		    if(totalHits > 30)
			pageNumber = Main.Random.nextInt((int)Math.ceil(totalHits / 30)) + 1;
		    if(pageNumber > 1) {
			url = new URL(url.toString() + "&page=" + pageNumber);
			con = (HttpsURLConnection)url.openConnection();
			if(con.getResponseCode() == HTTP_OK) {
			    br = new BufferedReader(
				new InputStreamReader(con.getInputStream())
			    );
			    httpResult = br.readLine();
			    resultJson = new JSONObject(httpResult);
			}
		    }
		    if(resultJson.getBoolean("success")) {
			JSONArray imgs = resultJson.getJSONArray("wallpapers");
			JSONObject img = imgs.getJSONObject(Main.Random.nextInt(imgs.length()));
			result.imageUrl = img.getString("url_thumb");
			result.text = img.getString("url_page");
		    } else {
			result.text = resultJson.getString("error");
		    }
		}
	    } catch(Exception e) {
		e.printStackTrace();
		return null;
	    }
	} else {
	    // Get a random wallpaper
	    try {
		URL url = new URL(
			"https://wall.alphacoders.com/api2.0/get.php"
			+ "?auth=" + BotConfig.getInstance().getAlphacodersToken()
			+ "&method=random&count=1"
		);
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		if(con.getResponseCode() == HTTP_OK) {
		    BufferedReader br = 
			new BufferedReader(
			    new InputStreamReader(con.getInputStream())
			);
		    String httpResult = br.readLine();
		    JSONObject resultJson = new JSONObject(httpResult);
		    if(resultJson.getBoolean("success")) {
			JSONObject img = resultJson.getJSONArray("wallpapers").getJSONObject(0);
			result.imageUrl = img.getString("url_thumb");
			result.text = img.getString("url_page");
		    } else {
			result.text = resultJson.getString("error");
		    }
		}
	    } catch(Exception e) {
		e.printStackTrace();
		return null;
	    }
	}
	return result;
    }
    
}
