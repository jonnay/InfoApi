/*
 * honestly thanks to fullwall/aPunch for keeping their NPCs plugin open.
 * Same with the heroes guys.  
 * Standing on shoulders of giants.
 */
package seta.infoapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.util.logging.Logger;

// some code snarfed from
// https://github.com/Herocraft/Heroes/blob/master/src/com/herocraftonline/dev/heroes/skill/SkillManager.java

public class InfoApiEndpointManager {
	private static final String DELINEATION = "main-class: ";
	
    private final File dir;
    private final ClassLoader classLoader;
	private final InfoApi plugin;
	
	private Map<String, InfoApiEndpoint> endpoints;
	private Map<String, File> endpointFiles;

	Logger log = Logger.getLogger("Minecraft");
	
	public InfoApiEndpointManager(InfoApi plugin) {
		endpointFiles = new HashMap<String, File>();
		endpoints = new HashMap<String, InfoApiEndpoint>();
		this.plugin = plugin;
		dir = new File(plugin.getDataFolder(), "endpoints");
		dir.mkdir();

		List<URL> urls = new ArrayList<URL>();

		for (String endpointFile : dir.list()) {

			File file = new File(dir, endpointFile);
			String name = endpointFile.toLowerCase().replace(".jar", "");

			endpointFiles.put(name, file);

			try {
				urls.add(file.toURI().toURL());
			} catch (java.net.MalformedURLException e) {
				// Probably shouldn't happen, so dump the stacktrace
				e.printStackTrace();
			}
		}
		
        ClassLoader cl = plugin.getClass().getClassLoader();
        classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), cl);			
	}		

	InfoApiEndpoint getEndpoint(String id) throws InfoApiEndpointLoaderException {
		if (endpointFiles.containsKey(id)) {
			if (isLoaded(id)) {
				return endpoints.get(id);
			} else {
				InfoApiEndpoint endpoint = loadEndpoint(endpointFiles.get(id));
				endpoints.put(id, endpoint);
				return endpoint;
			}
		} else {
			throw new InfoApiEndpointLoaderException("Endpoint "+id+" does not exist.");
		}
	}


	boolean isLoaded(String id) {
		return endpoints.containsKey(id);
	}
	
	/**
	 * Return an instantiated Endpoint.
	 */
	InfoApiEndpoint loadEndpoint(File file)  {
		try {
			JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();

            String mainClass = null;
            while (entries.hasMoreElements()) {
                JarEntry element = entries.nextElement();
                if (element.getName().equalsIgnoreCase("endpoint.info")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
					// TODO This should validate.. it is very brittle.
					// String line = reader.readLine();
					// if (line.substring(0, DELINEATION.length)) {
                    mainClass = reader.readLine().substring(DELINEATION.length());
                    break;
                }
            }

            if (mainClass != null) {
                Class<?> clazz = Class.forName(mainClass, true, classLoader);
                Class<? extends InfoApiEndpoint> apiClass = clazz.asSubclass(InfoApiEndpoint.class);
                Constructor<? extends InfoApiEndpoint> ctor = apiClass.getConstructor(plugin.getClass());
                InfoApiEndpoint iae = ctor.newInstance(plugin);
                return iae;
            } else {
                throw new InfoApiEndpointLoaderException("No endpoint.info file found in the jar to delineate main class!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warning("[InfoApi] Endpoint " + file.getName() + " failed to load: "+e.getMessage());
            return null;
        }
	}
}
