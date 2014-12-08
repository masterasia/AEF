package dy.arch.aef.rpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dy.arch.aef.util.Verification;
import dy.arch.util.Conf;
import dy.arch.util.Log;
import dy.arch.util.ip.IPUtils;

/**
 * 定时任务拉取
 * @author robert.xu
 *
 */
public class MissionRequest
{
    private BufferedReader    dis;
    
    private HttpURLConnection http;
    
    private String            confName = "thread";
    
    private long              waitTime = 0;
    
    public MissionRequest()
    {
        init();
    }
    
    public MissionRequest(String confName)
    {
        this.confName = confName;
        init();
    }
    
    public String sendRequest()
    {
        String line = null;
        
        StringBuilder sb = new StringBuilder();
        try
        {
            http.connect();
            
            while (null != (line = dis.readLine()))
            {
                sb.append(line);
            }
            
            Log.debug(" the mission load is finished ! the missions is " + sb.toString());
        }
        catch (Exception e)
        {
            Log.fatal(" the mission loader is failed !" + e.getMessage());
        }
        finally
        {
            http.disconnect();
        }
        return sb.toString();
    }
    
    private void init()
    {
        String address = Conf.getConf(confName, "loader.request");
        try
        {
            waitTime = Long.parseLong(Conf.getConf(confName, "loader.waitTime"));
            
            URL url = new URL(String.format(address, IPUtils.getIP(), waitTime + "")
                    + Verification.getAPIVer());
            
            Log.debug(" the load url is " + url.toString());
            
            http = (HttpURLConnection) url.openConnection();
            // timeout
            http.setConnectTimeout(Integer.parseInt(Conf.getConf(confName, "loader.httpConnectTimeOut")));
            http.setReadTimeout(Integer.parseInt(Conf.getConf(confName, "loader.httpReadTimeOut")));
            
            http.setDoOutput(true);
            
            dis = new BufferedReader(new InputStreamReader(http.getInputStream()));
            
        }
        catch (Exception e)
        {
            Log.fatal(" mission loader try to init request failed! " + e.toString());
        }
    }
}
