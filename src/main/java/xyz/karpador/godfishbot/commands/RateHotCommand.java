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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author Follpvosten
 */
public class RateHotCommand extends Command {
    private final String name;
    private final int topCount;
    
    private final String HOTSTRING = "SEXÄY";
    
    /**
     * 
     * @param cmdName The name of the command.
     * @param topCount The maximum length of the top list returned.
     */
    public RateHotCommand(String cmdName, int topCount) {
	name = cmdName;
	if(topCount != 0)
	    this.topCount = topCount;
	else
	    this.topCount = Integer.MAX_VALUE;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getUsage() {
	return "/" + name + " <name(s)>";
    }

    @Override
    public String getDescription() {
	return "Rates the hotness of characters by their name.";
    }
    
    private class Rating implements Comparable<Rating> {
	private final String name;
	private final int rating;
	
	public Rating(String name, int rating) {
	    this.name = name;
	    this.rating = rating;
	}
	
	@Override
	public String toString() {
	    return name + ": " + rating;
	}

	@Override
	public int compareTo(Rating other) {
	    return Integer.compare(other.rating, rating);
	}
    }
    
    private int getNameRating(String name) {
	int result = 1;
	name = name.toLowerCase();
	// Calculate how hot that guy is, lol
	// First, we times 1.5 the person...
	name = name + name.substring(name.length() / 2);
	// Then we double that, but in reverse
	name = name + new StringBuilder(name).reverse().toString();
	// Log the current working value to console, just for fun
	System.out.println(name);
	// Initialize a randomizer with the first character as seed
	Random random = new Random(name.charAt(0));
	// The randomizer helps deciding the numbers added to the result
	for(int i = 0; i < name.length(); i++) {
	    // Get a nice number...
	    long value = name.charAt(i) + HOTSTRING.charAt(random.nextInt(HOTSTRING.length()));
	    while(value > 0) {
		// And shift it into the result
		result++;
		value--;
		// Which may not be above 10, so we circle through there
		if(result > 10) result = 1;
	    }
	}
	return result;
    }

    @Override
    public CommandResult getReply(String params, Message message, String myName) {
	if(params == null) return new CommandResult("Please submit at least one name.");
	String[] names = params.split(" ");
	if(names.length == 1 || topCount == 1) {
	    // Rate that one guy
	    return new CommandResult(names[0] + ": " + getNameRating(names[0]));
	} else {
	    // Make a list
	    List<Rating> ratings = new ArrayList<>();
	    for (String currentName : names) {
		ratings.add(new Rating(currentName, getNameRating(currentName)));
	    }
	    Collections.sort(ratings);
	    String result = "";
	    for(int i = 0; i < ratings.size() && i < topCount; i++) {
		result += (i+1) + ". " + ratings.get(i).toString();
		if(i != ratings.size() - 1 && i != topCount - 1)
		    result += "\n";
	    }
	    return new CommandResult(result);
	}
    }
    
}
