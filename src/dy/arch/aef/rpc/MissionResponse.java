package dy.arch.aef.rpc;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.util.SendMail;
import dy.arch.aef.util.Verification;
import dy.arch.util.Conf;
import dy.arch.util.Log;
import dy.arch.util.Value;
import dy.arch.util.constant.Const;
import dy.arch.util.ip.IPUtils;

/**
 * 任务状态反馈
 * @author robert.xu
 *
 */
public class MissionResponse
{
    private String  address;
    
    private String  params;
    
    private Mission mission;
    
    public void missionFinish(Mission mission)
    {
        address = Conf.getConf("thread", "thread.response");
        
        params = mission.toString();
        
        this.mission = mission;
        
        this.sendResponse();
    }
    
    public void timeOut(Mission mission)
    {
        mission.setStderr(" the mission is over time !");
        
        this.missionFinish(mission);
    }
    
    public void missionStart(Mission mission)
    {
        address = Conf.getConf("thread", "mission.startURL");
        
        params = "id=" + mission.getId();
        
        this.mission = mission;
        
        this.sendResponse();
    }
    
    private void sendResponse()
    {
        try
        {
            if (Const.SUCCESS != mission.getExit_code())
            {
                this.sendError();
            }
            
            URL url = new URL(address);
            
            params = params + Verification.getAPIVer();
            
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            // timeout
            http.setConnectTimeout(Integer.parseInt(Conf.getConf("thread", "thread.httpConnectTimeOut")));
            http.setReadTimeout(Integer.parseInt(Conf.getConf("thread", "thread.httpReadTimeOut")));
            
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            
            OutputStream out = http.getOutputStream();
            
            http.connect();
            
            byte[] raw = params.getBytes();
            
            if (raw != null)
            {
                out.write(raw);
                out.flush();
                out.close();
            }
            List<String> headerString = http.getHeaderFields().get(null);
            // 关闭连接
            http.disconnect();
            
            if (headerString == null)
            {
                throw new Exception(" the result is not send ");
            }
            
            int httpStatus = Integer.parseInt(headerString.toString().split("[ ]")[1]);
            
            if (httpStatus == 200)
            {
                Log.notice(" the mission result is already send !" + params);
            }
        }
        catch (Exception e)
        {
            Log.fatal(" error to send the result of CMD " + e.getMessage());
        }
        
    }
    
    private void sendError()
    {
        if (Const.SWITCH_ON.equals(Conf.getConf("thread", "httpError.switch")))
        {
            sendHTTPMsg();
        }
        
        if (Const.SWITCH_ON.equals(Conf.getConf("thread", "email.switch")))
        {
            sendEmailMsg();
        }
    }
    
    private void sendHTTPMsg()
    {
        String addressError = Conf.getConf("thread", "mission.httpErrorAddress");
        try
        {
            URL url = new URL(addressError);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            // timeout
            http.setConnectTimeout(Integer.parseInt(Conf.getConf("thread", "mission.httpErrorConnectTimeOut")));
            http.setReadTimeout(Integer.parseInt(Conf.getConf("thread", "mission.httpErrorReadTimeOut")));
            
            http.setDoOutput(true);
            http.setRequestMethod("PUT");
            
            OutputStream out = http.getOutputStream();
            
            http.connect();
            
            Map<String, Value> map = new HashMap<String, Value>();
            map.put("ip", new Value(IPUtils.getIP()));
            map.put("path", new Value(" AEF Mission "));
            map.put("content", new Value(this.mission.toMessage()));
            
            Value data = new Value(map);
            byte[] raw = data.pack().getBytes();
            
            if (raw != null)
            {
                out.write(raw);
                out.close();
            }
            List<String> headerString = http.getHeaderFields().get(null);
            // 关闭连接
            http.disconnect();
            
            if (headerString == null)
            {
                Log.warning(" the target miss response ");
                
                return;
            }
            
            int httpStatus = Integer.parseInt(headerString.toString().split("[ ]")[1]);
            
            if (httpStatus == 200)
            {
                Log.notice(" the target got the message ");
                
            }
        }
        catch (Exception e)
        {
            Log.fatal(" the error code send failed ! " + e.getMessage());
        }
    }
    
    private void sendEmailMsg()
    {
        Map<String, String> mailProperties = new HashMap<String, String>();
        
        String targets = Conf.getConf("thread", "email.target");

        mailProperties.put("smtp_target", targets.contains(";") ? targets.replaceAll(";", ",") : targets);
        mailProperties.put("smtp_subject", Conf.getConf("thread", "email.title"));
        mailProperties.put("smtp_text", IPUtils.getLocalIP() + " <br /><br /> " + mission.toMessage());
        
        mailProperties.put("smtp_server", Conf.getConf("thread", "email.smtp_server"));
        mailProperties.put("smtp_port", Conf.getConf("thread", "email.smtp_port"));
        mailProperties.put("smtp_account", Conf.getConf("thread", "email.smtp_account"));
        mailProperties.put("smtp_password", Conf.getConf("thread", "email.smtp_password"));
        
        try
        {
            SendMail.sendMessage(mailProperties);
        }
        catch (MessagingException e)
        {
            Log.fatal(" try to send email failed ! msg : " + e);;
        }
    }
}
