package dy.arch.aef.timer;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.bean.Missions;

public class MissionTimer extends BaseTimer
{    
    public MissionTimer()
    {
        super();
    }
    
    public MissionTimer(Mission mission)
    {
        super(mission);
    }
    
    @Override
    public void run()
    {
        Missions.getMission().add(mission);
    }
}
