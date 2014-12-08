package dy.arch.aef.execut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dy.arch.aef.bean.Mission;
import dy.arch.util.Log;
import dy.arch.util.constant.Const;

/**
 * <p>
 * 用于适配LINUX系统的执行命令流程
 * </p>
 * 
 * @author Robert.xu
 *
 */
public class LinuxCMD extends CMD
{
    
    BufferedReader br        = null;
    BufferedReader err       = null;
    Process        process;
    boolean        bashFlag  = true;
    String         cmdString = "";
    
    @Override
    public void execut(Mission mission)
    {
        processTheCMD(mission);
    }
    
    @Override
    public void copy()
    {
        
    }
    
    @Override
    public void stopCMD()
    {
        stopStream();
        try
        {
            process = Runtime.getRuntime().exec(
                    "sh /dianyi/app/aef/cmd/taskkill.sh " + cmdString.trim().split(" ")[1]);
        }
        catch (IOException e)
        {
            Log.fatal(" try to stop the CMD failed !" + cmdString);
        }
    }
    
    private void processTheCMD(Mission mission)
    {
        Log.notice(" the mission is ready to start " + mission.toString());
        
        bashFlag = true;
        
        cmdString = mission.getCmd();
        
        try
        {
            //尝试使用默认方式运行
            process = Runtime.getRuntime().exec(cmdString);   
            
            if (null != process)
            {
                br = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                err = new BufferedReader(new InputStreamReader(process.getErrorStream()), 1024);
                mission.setProcess(process);
                
                mission.setExit_code(process.waitFor());
                
                mission.setStdout(getStringFromReader(br));
                mission.setStderr(getStringFromReader(err));
            }
            else
            {
                mission.setExit_code(Const.FAILED);
                Log.fatal(" thread " + getName() + " try to execut [" + mission.getCmd() + "] is failed !");
            }
        }
        catch (Exception e)
        {
            if (e.getMessage().contains("Cannot run program"))
            {
                bashFlag = false;
            }
            else
            {
                mission.setExit_code(Const.FAILED);
                Log.fatal(" try to get the stream of " + mission.getCmd() + " is failed !");
            }
        }
        finally
        {
            if (!bashFlag)
            {
                //尝试失败，使用/bin/sh方式运行
                String[] command = { "/bin/sh", "-c", cmdString };
                try
                {
                    process = Runtime.getRuntime().exec(command);
                    shProcess(mission);
                }
                catch (IOException e1)
                {
                    mission.setExit_code(Const.FAILED);
                    Log.fatal(" try to get the stream of " + mission.getCmd() + " is failed !");
                }
            }
        }        
    }
    
    /**
     * 出现无法执行程序时，切换为/bin/sh方式执行
     * @param mission
     */
    private void shProcess(Mission mission)
    {
        try
        {
            if (null != process)
            {
                br = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                err = new BufferedReader(new InputStreamReader(process.getErrorStream()), 1024);
                mission.setProcess(process);
                
                mission.setExit_code(process.waitFor());
                
                mission.setStdout(getStringFromReader(br));
                mission.setStderr(getStringFromReader(err));
            }
            else
            {
                mission.setExit_code(Const.FAILED);
                Log.fatal(" thread " + getName() + " try to execut [" + mission.getCmd() + "] is failed !");
            }
        }
        catch (Exception e)
        {
            mission.setExit_code(Const.FAILED);
            Log.fatal(" the thread " + this.getName() + " is Interrupted !");
        }
        finally
        {
            stopStream();
        }
    }
    
    private String getStringFromReader(BufferedReader br)
    {
        StringBuffer sb = new StringBuffer();
        String line = "";
        try
        {
            while (null != br && null != (line = br.readLine()))
            {
                sb.append(line).append("\r\n");
            }
        }
        catch (IOException e)
        {
            Log.fatal(" try to get the message about the CMD failed ! " + cmdString);
        }
        
        return sb.toString();
    }
    
    private void stopStream()
    {
        br = null;
        err = null;
    }
    
}
