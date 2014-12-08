package dy.arch.aef.execut;

import dy.arch.aef.bean.Mission;

/**
 * <p>执行 抽象接口，多种执行命令可自由拓展</p>
 * @author robert.xu
 *
 */
public abstract class CMD extends Thread
{
    private Mission mission;
    
    public abstract void execut(Mission mission);
    
    public abstract void copy();
    
    public abstract void stopCMD();
    
    @Override
    public void run()
    {
        this.execut(mission);
        
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
