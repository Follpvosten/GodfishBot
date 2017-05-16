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
public class PbCommand extends Command {

    @Override
    public String getName() {
	return "pb";
    }

    @Override
    public String getUsage() {
	return "/pb [search query]";
    }

    @Override
    public String getDescription() {
	return "Get an image from Pixabay (optionally filtered by a search query)";
    }

    @Override
    public CommandResult getReply(String params, Message message, String myName) {
	CommandResult result = new CommandResult();
	try {
	    String urlString = "https://pixabay.com/api/"
			     + "?key=" + BotConfig.getInstance().getPixabayToken()
			     + "&pretty=false";
	    if(params != null)
		urlString += "&q=" + URLEncoder.encode(params, "UTF-8");
	    URL url = new URL(urlString);
	    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
	    if(con.getResponseCode() == HTTP_OK) {
		BufferedReader br = 
		    new BufferedReader(
			new InputStreamReader(con.getInputStream())
		    );
		String httpResult = "";
		String line;
		while((line = br.readLine()) != null)
		    httpResult += line;
		JSONObject resultJson = new JSONObject(httpResult);
		int totalHits = resultJson.getInt("totalHits");
		int pageNumber = 1;
		if(totalHits > 20) // Generate a valid page number.
		    pageNumber = Main.Random.nextInt((int)Math.ceil(totalHits / 20)) + 1;
		if(pageNumber > 1) {
		    urlString += "&page=" + pageNumber;
		    url = new URL(urlString);
		    con = (HttpsURLConnection)url.openConnection();
		    if(con.getResponseCode() == HTTP_OK) {
			br = new BufferedReader(
			    new InputStreamReader(con.getInputStream())
			);
			httpResult = "";
			while((line = br.readLine()) != null)
			    httpResult += line;
			resultJson = new JSONObject(httpResult);
		    }
		}
		JSONArray hits = resultJson.getJSONArray("hits");
		int imageIndex = Main.Random.nextInt(hits.length());
		JSONObject img = hits.getJSONObject(imageIndex);
		result.imageUrl = img.getString("webformatURL");
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	    return null;
	}
	return result;
    }
    
}
