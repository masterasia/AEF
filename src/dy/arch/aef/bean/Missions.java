package dy.arch.aef.bean;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dy.arch.util.Conf;
import dy.arch.util.Log;
import dy.arch.util.thread.ThreadUtil;

/**
 * 任务管理集合 包含待执行任务集合 missions 任务状态集合 aliveMissions
 * @author robert.xu
 *
 */
public class Missions
{
    private static ArrayBlockingQueue<Mission>  missions;
    
    private static Missions                     mission;
    
    private static ConcurrentMap<Long, Integer> aliveMissions;
    
    static
    {
        if (null == missions)
            missions = new ArrayBlockingQueue<Mission>(Integer.parseInt(Conf.getConf("thread",
                    "mission.maxSize")));
        
        if (null == aliveMissions)
            aliveMissions = new ConcurrentHashMap<Long, Integer>();
    }
    
    private Missions()
    {
        
    }
    
    public void add(Mission value)
    {
        synchronized (missions)
        {
            if (this.addAliveMission(value))
            {
                while (missions.offer(value) == false)
                {
                    Log.fatal("Save data to Mission fail, store queue is " + missions.size());
                    
                    ThreadUtil.threadSleep(10L);
                }
            }
        }
        
        Log.debug(" the missions now have " + missions.size());
    }
    
    public Map<Long, Integer> getAliveMission()
    {
        return aliveMissions;
    }
    
    public boolean addAliveMission(Mission mission)
    {
        boolean flag = false;
        try
        {
            if (null != aliveMissions.get(mission.getId()))
            {
                Log.fatal(" this mission is already exist ! mission id is " + mission.getId());
            }
            else
            {
                aliveMissions.put(mission.getId(), 0);
                flag = true;
            }
        }
        catch (Exception e)
        {
            Log.fatal(" try to find the alive mission failed !" + mission.toString());
        }
        
        Log.debug(" the alivemission have " + aliveMissions.size());
        return flag;
    }
    
    public boolean updateAliveMission(Mission mission)
    {
        boolean flag = false;
        try
        {
            if (null == aliveMissions.get(mission.getId()))
            {
                Log.fatal(" this mission is not exist ! mission id is " + mission.getId());
            }
            else
            {
                aliveMissions.put(mission.getId(), 1);
                flag = true;
            }
        }
        catch (Exception e)
        {
            Log.fatal(" try to find the alive mission failed !" + mission.toString());
        }
        
        Log.debug(" the alivemission have " + aliveMissions.size());
        return flag;
    }
    
    public boolean removeAliveMission(Mission mission)
    {
        boolean flag = false;
        
        try
        {
            if (null != aliveMissions.get(mission.getId()))
            {
                aliveMissions.remove(mission.getId());
            }
            
            flag = true;
        }
        catch (Exception e)
        {
            Log.fatal(" try to remove the alive mission failed !" + mission.toString());
        }
        
        Log.debug(" the alivemission have " + aliveMissions.size());
        return flag;
    }
    
    public ArrayBlockingQueue<Mission> getQueue()
    {
        return missions;
    }
    
    public void setQueue(ArrayBlockingQueue<Mission> mission)
    {
        missions = mission;
    }
    
    public static Missions getMission()
    {
        if (null == mission)
        {
            mission = new Missions();
        }
        
        return mission;
    }
}
