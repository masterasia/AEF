package dy.arch.aef.timer;

import java.util.List;
import java.util.Timer;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.rpc.MissionRequest;
import dy.arch.aef.util.MissionUtil;
import dy.arch.util.Conf;
import dy.arch.util.ContainerUtil;
import dy.arch.util.Log;
import dy.arch.util.constant.Const;
import dy.arch.util.thread.ThreadUtil;

/**
 * 定时加载器
 * @author robert.xu
 *
 */
public class Loader extends Thread
{
    private long          waitTime;
    
    private List<Mission> missions;
    
    public Loader()
    {
        init();
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            Log.notice(" now we try to get the next " + waitTime + " ms have how many missions !");
                
            getMissions();
            
            if (!ContainerUtil.isEmpty(missions))
            {
                for (Mission mission : missions)
                {
                    buildTimer(mission);
                }
            }
            
            ThreadUtil.threadSleep(waitTime);
        }
    }
    
    private void init()
    {
        waitTime = Long.parseLong(Conf.getConf("thread", "loader.waitTime")) * Const.TIME_CARRY;
    }
    
    private void getMissions()
    {
        MissionRequest mr = new MissionRequest();
        String value = mr.sendRequest();
        
        missions = MissionUtil.getMission(value);
    }
    
    private void buildTimer(Mission mission)
    {
        Timer timer = new Timer();
        
        long nowTime = System.currentTimeMillis() / Const.TIME_CARRY;
        
        long startTime = nowTime > mission.getStart_time() ? 0 : mission.getStart_time() - nowTime;
        
        long runTimes = mission.getFrequency() * Const.TIME_CARRY;
        
        if (runTimes > 0)
        {
            timer.scheduleAtFixedRate(new MissionTimer(mission), startTime * Const.TIME_CARRY, runTimes);
        }
        else
        {
            timer.schedule(new MissionTimer(mission), startTime * Const.TIME_CARRY);
        }
    }
}
