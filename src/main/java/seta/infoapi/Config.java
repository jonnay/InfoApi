package seta.infoapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    public enum Preferences {
	SECRET("secret"), PORT("25577"), SERVER_NAME("&a[&bServer&a]&f"), ENABLE_CHAT_LOG("true"), LOG_LENGTH("25"), OP_PLAYER(""), NPC_SAVE_MODE("false");

	private final String defaultValue;

	Preferences(String defaultString) {
	    this.defaultValue = defaultString;
	}

	public String getDefaultValue() {
	    return this.defaultValue;
	}
    }

    static String pluginName = "InfoApi";
    static String mainDirectory = "plugins/" + pluginName;
    static File configFile = new File(mainDirectory + File.separator + "config.cfg");
    static Properties config = new Properties();

    Config() {
	File mainDir = new File(mainDirectory);
	if (!mainDir.exists()) {
	    mainDir.mkdir();
	}

	if (!configFile.exists()) {
	    try {
		configFile.createNewFile();
		FileOutputStream out = new FileOutputStream(configFile);
		config.put(Preferences.SECRET.name(), Preferences.SECRET.getDefaultValue());
		config.put(Preferences.PORT.name(), Preferences.PORT.getDefaultValue());
		config.put(Preferences.SERVER_NAME.name(), Preferences.SERVER_NAME.getDefaultValue());
		config.put(Preferences.ENABLE_CHAT_LOG.name(), Preferences.ENABLE_CHAT_LOG.getDefaultValue());
		config.put(Preferences.LOG_LENGTH.name(), Preferences.LOG_LENGTH.getDefaultValue());
		config.put(Preferences.OP_PLAYER.name(), Preferences.OP_PLAYER.getDefaultValue());
		config.put(Preferences.NPC_SAVE_MODE.name(), Preferences.NPC_SAVE_MODE.getDefaultValue());
		config.store(out, pluginName + " Config file");
		out.flush();
		out.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}

	loadConfig();
    }

    public void setConfig(String string, String string2) {
	config.setProperty(string, string2);
	this.saveConfig();
    }

    public String getConfig(String key) {
	String storedValue = config.getProperty(key.toUpperCase());
	if(storedValue != null && storedValue.isEmpty()) {
	    storedValue = Preferences.valueOf(key).defaultValue;
	}
	
	return storedValue;
    }

    private void loadConfig() {
	try {
	    FileInputStream inStream = new FileInputStream(configFile);
	    config.load(inStream);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    private void saveConfig() {
	try {
	    FileOutputStream out = new FileOutputStream(configFile);
	    config.store(out, pluginName + " Config file");
	    out.flush();
	    out.close();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
