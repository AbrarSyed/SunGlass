package me.abrarsyed.sunglass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

public class Main
{
    private static final Properties props = new Properties();
    private static File propertiesFile = new File("SunGlass.properties");
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws IOException
    {
        // parse arguments
        
        // parse
        logger.info("Reading property file: {}", propertiesFile.getCanonicalPath());
        try (Reader reader = new FileReader(propertiesFile))
        {
            props.load(reader);
        }
        
        File repoDir = new File(props.getProperty("repoDir", "repo"));
        File serverList = new File(props.getProperty("serverListFile", "servers.txt"));
        int port = Integer.parseInt(props.getProperty("port", "4567"));
        String ip = props.getProperty("ip", "0.0.0.0");
        
        Spark.ipAddress(ip);
        Spark.port(port);
        
        logger.info("Cache repository set: {}", repoDir.getCanonicalPath());
        
        // get -> info
        // get -> admin
        String[] servers = readServerList(serverList);
        logger.info("Checking {} external Maven repositories", servers.length);
        for (String server : servers)
            logger.debug(server);
        
        // get proxy thingy
        Spark.get("*", new ProxyRouter(repoDir, servers));
    }
    
    protected static String[] readServerList(File serverList) throws IOException
    {
        ArrayList<String> servers = new ArrayList<>();
        
        if (!serverList.exists())
            return new String[0];
        
        try (BufferedReader reader = new BufferedReader(new FileReader(serverList)))
        {
            while(reader.ready())
            {
                String line = reader.readLine();
                line = line.trim();
                
                if (line.startsWith("#") || line.isEmpty())
                    continue;
                
                int index = line.indexOf('#');
                if (index > 0)
                {
                    line = line.substring(0, index).trim();
                }
                
                servers.add(line);
            }
        }
        
        return servers.toArray(new String[servers.size()]);
    }
}
