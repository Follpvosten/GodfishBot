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
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import xyz.karpador.godfishbot.AsyncFileHelper;
import xyz.karpador.godfishbot.BotConfig;
import xyz.karpador.godfishbot.Main;

/**
 *
 * @author Follpvosten
 */
public class RikkaCommand extends Command {
    
    private ArrayList<String[]> imgUrls = null;
    
    private final AsyncFileHelper fileHelper;
    
    public RikkaCommand() {
	fileHelper = new AsyncFileHelper("rikka.json");
	if(fileHelper.fileExists()) {
	    fileHelper.startRead();
	}
    }

    @Override
    public String getName() {
	return "rikka";
    }

    @Override
    public String getUsage() {
	return "/rikka";
    }

    @Override
    public String getDescription() {
	return "Get a Takanashi Rikka pic from Wallpaper Abyss";
    }

    @Override
    public CommandResult getReply(String params, Message message, String myName) {
	if(imgUrls == null) {
	    if(fileHelper.fileExists()) {
		String fileContent = fileHelper.getReadData();
		if(fileContent != null) {
		    try {
			JSONObject fileJson = new JSONObject(fileContent);
			JSONArray urlsData = fileJson.getJSONArray("urls");
			imgUrls = new ArrayList<>();
			for(int i = 0; i < urlsData.length(); i++) {
			    JSONArray values = urlsData.getJSONArray(i);
			    imgUrls.add(new String[] {
				values.getString(0),
				values.getString(1),
				values.optString(2, null)
			    });
			}
		    } catch(JSONException e) {
			e.printStackTrace();
			imgUrls = null;
		    }
		}
	    }
	    if(imgUrls == null) {
		imgUrls = new ArrayList<>();
		try {
		    for(int j = 1; j < 4; j++) {
			URL url = new URL(
				"https://wall.alphacoders.com/api2.0/get.php"
				+ "?auth=" + BotConfig.getInstance().getAlphacodersToken()
				+ "&method=tag&id=35982&page=" + j
			);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			if(con.getResponseCode() == HTTP_OK) {
			    BufferedReader br = 
				new BufferedReader(
				    new InputStreamReader(con.getInputStream())
				);
			    String result = br.readLine();
			    JSONObject resultJson = new JSONObject(result);
			    if(resultJson.getBoolean("success")) {
				JSONArray imgs = resultJson.getJSONArray("wallpapers");
				for(int i = 0; i < imgs.length(); i++) {
				    JSONObject currentImg = imgs.getJSONObject(i);
				    imgUrls.add(new String[] {
					currentImg.getString("url_thumb"),
					currentImg.getString("url_image"),
					null
				    });
				}
				writeCurrentState();
			    }
			}
		    }
		} catch(IOException | JSONException e) {
		    e.printStackTrace();
		    return null;
		}
	    }
	}
	CommandResult result = new CommandResult();
	String[] image = imgUrls.get(Main.Random.nextInt(imgUrls.size()));
	result.imageUrl = image[0];
	result.text = image[1];
	result.mediaId = image[2];
	return result;
    }
    
    private void writeCurrentState() {
	try {
	    JSONObject jsonObj = new JSONObject();
	    JSONArray urls = new JSONArray();
	    for(String[] values : imgUrls) {
		JSONArray url = new JSONArray(values);
		urls.put(url);
	    }
	    jsonObj.put("urls", urls);
	    fileHelper.startWrite(jsonObj.toString());
	} catch(JSONException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void processSendResult(String mediaUrl, String mediaId) {
	for(String[] image : imgUrls) {
	    if(image[0].equals(mediaUrl)) {
		if(image[2] == null) {
		    image[2] = mediaId;
		    writeCurrentState();
		}
		return;
	    }
	}
    }
    
}
