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
import java.util.ArrayList;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.BotConfig;
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class FlauschCommand extends Command {
    
    private ArrayList<String[]> imgUrls = null;
    private Date lastRefreshDate = new Date();
    
    private static final String[] BLOCKTAGS =
    { "baby", "child", "chicken", "figure", "keinohrhase", "man", "cat",
      "gold foil", "girl", "paddle board" };

    @Override
    public String getName() {
	return "flausch";
    }

    @Override
    public String getUsage() {
	return "/flausch";
    }

    @Override
    public String getDescription() {
	return "Get a fluffy bunny picture";
    }
    
    private boolean stringContainsAny(String str, String[] values) {
	for (String value : values)
	    if (str.contains(value)) return true;
	return false;
    }

    @Override
    public CommandResult getReply(String params, Message message, String myName) {
	if(imgUrls == null
	|| new Date().getTime() >= lastRefreshDate.getTime() + (24 * 3600)) {
	    imgUrls = new ArrayList<>();
	    // Populate the list with pixabay URLs
	    try {
		for(int j = 1; j < 10; j++) {
		    URL url = new URL("https://pixabay.com/api/"
			    + "?key=" + BotConfig.getInstance().getPixabayToken() + "&q=bunny"
			    + "&image_type=photo&category=animals&pretty=false"
			    + "&page=" + j);
		    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		    if(con.getResponseCode() == HTTP_OK) {
			BufferedReader br = 
			    new BufferedReader(
				new InputStreamReader(con.getInputStream())
			    );
			String result = "";
			String line;
			while((line = br.readLine()) != null)
			    result += line;
			JSONObject resultJson = new JSONObject(result);
			JSONArray hits = resultJson.getJSONArray("hits");
			
			for(int i = 0; i < hits.length(); i++) {
			    JSONObject currentImg = hits.getJSONObject(i);
			    String tags = currentImg.getString("tags");
			    if(stringContainsAny(tags, BLOCKTAGS)) continue;
			    imgUrls.add(new String[] {
				currentImg.getString("webformatURL"),
				null
			    });
			}
			lastRefreshDate = new Date();
		    }
		}
	    } catch(IOException | JSONException e) {
		e.printStackTrace();
		return null;
	    }
	}
	CommandResult result = new CommandResult();
	String[] data = imgUrls.get(Main.Random.nextInt(imgUrls.size()));
	result.imageUrl = data[0];
	result.mediaId = data[1];
	return result;
    }

    @Override
    public void processSendResult(String mediaUrl, String mediaId) {
	for(String[] image : imgUrls) {
	    if(image[0].equals(mediaUrl)) {
		image[1] = mediaId;
		return;
	    }
	}
    }
    
}
