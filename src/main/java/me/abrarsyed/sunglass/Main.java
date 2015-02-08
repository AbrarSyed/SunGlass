package me.abrarsyed.sunglass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

import spark.Spark;

public class Main
{
    private static final Properties props = new Properties();
    private static File propertiesFile = new File("SunGlass.properties");
    
    public static void main(String[] args) throws IOException
    {
        // parse arguments
        
        // parse
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
        
        
        // get -> info
        // get -> admin
        
        // get proxy thingy
        Spark.get("*", new ProxyRouter(repoDir, readServerList(serverList)));
    }
    
    protected static String[] readServerList(File serverList) throws IOException
    {
        ArrayList<String> servers = new ArrayList<>();
        
        if (!serverList.exists())
            return new String[0];
        
        try (BufferedReader reader = new BufferedReader(new FileReader(propertiesFile)))
        {
            while(reader.ready())
            {
                String line = reader.readLine();
                line = line.trim();
                
                if (line.startsWith("#"))
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
