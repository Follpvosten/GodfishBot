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

/**
 *
 * @author Follpvosten
 */
public class TestLoveCommand extends Command {

	@Override
	public String getName() {
		return "testlove";
	}

	@Override
	public String getUsage() {
		return "/testlove <name1> <name2>";
	}

	@Override
	public String getDescription() {
		return "Test your love by putting in your f***ing names (because that says so much).\n"
			+ "Don't use spaces in the names. You're gonna die otherwise.";
	}

	@Override
	public CommandResult getReply(String params, Message message, String myName) {
		if (params == null) return new CommandResult("Please submit two names.");
		String[] names = params.split(" ");
		if (names.length < 2) return new CommandResult("Please submit two names.");
		try {
			URL url = new URL("https://love-calculator.p.mashape.com/getPercentage"
				+ "?fname=" + names[0] + "&sname=" + names[1]);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestProperty("X-Mashape-Key", BotConfig.getInstance().getMashapeToken());
			con.setRequestProperty("Accept", "application/json");
			if (con.getResponseCode() == HTTP_OK) {
				BufferedReader br =
					new BufferedReader(
						new InputStreamReader(con.getInputStream())
					);
				String result = br.readLine();
				JSONObject resultJson = new JSONObject(result);
				String cmdResult = names[0] + " and " + names[1] + " fit "
					+ resultJson.getString("percentage") + "%.\n"
					+ resultJson.getString("result");
				return new CommandResult(cmdResult);
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}