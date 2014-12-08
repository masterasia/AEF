package dy.arch.aef.timer;

import java.util.TimerTask;

import dy.arch.aef.bean.Mission;

/**
 * 定时任务的基类
 * @author robert.xu
 *
 */
public class BaseTimer extends TimerTask
{
    
    protected Mission mission;
    
    public BaseTimer()
    {
        
    }
    
    public BaseTimer(Mission mission)
    {
        this.mission = mission;
    }
    
    @Override
    public void run()
    {
        
    }
    
    public Mission getMission()
    {
        return mission;
    }
    
    public void setMission(Mission mission)
    {
        this.mission = mission;
    }
}
