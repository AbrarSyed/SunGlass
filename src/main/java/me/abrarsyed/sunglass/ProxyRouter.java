package me.abrarsyed.sunglass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Route;

public class ProxyRouter implements Route
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private File repoDir;
    private String[] servers;
    
    public ProxyRouter(File repoDir, String[] servers)
    {
        if (repoDir == null || servers == null)
            throw new IllegalArgumentException("Argumetns are null!");
        
        this.repoDir = repoDir;
        this.servers = servers;
    }

    public File getRepoDir()
    {
        return repoDir;
    }

    public void setRepoDir(File repoDir)
    {
        repoDir.mkdirs();
        
        this.repoDir = repoDir;
    }

    public String[] getServers()
    {
        return servers;
    }

    public void setServers(String[] servers)
    {
        if (servers == null)
            throw new IllegalArgumentException("NO NULL SERVER LIST! ONLY EMPTY ARRAY");
        
        this.servers = servers;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception
    {
        String path = request.pathInfo();
        logger.debug("Checking for path: {}", path);
        
        // local file location
        File outFile = new File(repoDir, path);
        
        for (int i = 0; i < servers.length && !outFile.exists(); i++)
        {
            String url = servers[i] + path;
            
            // DOWNLOAD
            try
            {
                HttpURLConnection connect = (HttpURLConnection) (new URL(url)).openConnection();
                connect.setInstanceFollowRedirects(true);

                if (connect.getResponseCode() == 200)
                {
                    logger.info("resource '{}' found in server '{}'", path, servers[i]);
                }
                else
                // 40X or 50X errors, or the unlikely 30X thingy
                {
                    logger.debug("{}: {} from {}", connect.getResponseCode(), connect.getResponseMessage(), url);
                    continue;
                }

                // just to close the streams...
                try (InputStream inStream = connect.getInputStream(); OutputStream outStream = new FileOutputStream(outFile))
                {
                    int data = inStream.read();
                    while (data != -1)
                    {
                        outStream.write(data);

                        // read next
                        data = inStream.read();
                    }

                    outStream.flush();
                }
                
            }
            catch (IOException e)
            {
                logger.debug("Error on server {}", e, url);
            }
        }
        
        
        if (!outFile.exists())
        {
            logger.info("resource '{}' found not found on any of the {} servers", path, servers.length);
            response.status(404);
            return null;
        }
        else
        {
            response.type("application/octet-stream");
            return outFile;
        }
    }

}
