# GodfishBot
This is a multi-purpose Telegram bot written in Java.  
It uses multiple web APIs to pull in various kinds of data.

## How to set up
### Using maven on the command line
1. Install maven and cd into the project dir
2. `mvn compile && mvn package`
3. Add your API token(s) to the file called `config.json` in the `target` folder.
The Telegram Bot Token is required, the others are all optional.
4. `java -jar target/GodfishBot-<version>-jar-with-dependencies.jar`

### Using IntelliJ IDEA
The process using IDEA is not working perfectly at the moment.  
Open the folder (it's an existing project), let it index, run a `Build`,
enter your bot token (and optionally other tokens) in `target/config.json`,
and `Run 'Main'`. It should work in theory. Manual rebuilds may be required
on code changes, haven't got that working yet.
