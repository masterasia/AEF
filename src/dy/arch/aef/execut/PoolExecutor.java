package dy.arch.aef.execut;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.bean.Missions;
import dy.arch.aef.rpc.MissionResponse;
import dy.arch.aef.util.MissionUtil;
import dy.arch.util.Conf;
import dy.arch.util.Log;
import dy.arch.util.constant.Const;
import dy.arch.util.thread.ThreadUtil;

/**
 * <p>线程池 实际执行线程。每个线程监控一个执行命令</p>
 * @author robert.xu
 *
 */
public class PoolExecutor implements Runnable
{
    private Mission mission;
    
    private boolean flag  = true;
    
    private CMD     cmd;
    
    private long    waitForTime;
    
    private int     index = 0;
    
    MissionResponse mp    = new MissionResponse();
    
    public PoolExecutor()
    {
        waitForTime = Long.parseLong(Conf.getConf("thread", "threadpool.sleeptime"));
    }
    
    @Override
    public void run()
    {
        if (Missions.getMission().updateAliveMission(mission))
        {
            initCMD();
            
            startCMD();
            
            while (ifRetry())
            {
                flag = true;
                index++;
                cleanCMD();
                initCMD();
                startCMD();
            }
        }
        else
        {
            mission.setStderr(" the mission is already run ! id is " + mission.getId());
        }
        setResponse();
    }
    
    /**
     * <p>判断当前任务是否需要重试</p>
     * @return 当执行标识为异常 且 任务重试标识为true 且 任务执行次数小于任务尝试次数 返回true
     */
    private boolean ifRetry()
    {
        return Const.SUCCESS != mission.getExit_code() && mission.isRetryStatus()
                && index < mission.getRetryTimes();
    }
    
    /**
     * <p>初始化执行线程</p>
     */
    private void initCMD()
    {
        Log.debug(" run times " + index + " " + mission.toString());
        
        int cmdType = mission.getCMDType();
        
        cmd = CMDFactory.getExecutor(cmdType);
        
        MissionUtil.setNowTime(mission);
        
        mission.setExit_code(Const.SUCCESS);
        
        cmd.setMission(mission);
        
        mp.missionStart(mission);
    }
    
    /**
     * <p>启动执行线程，监控状态</p>
     */
    private void startCMD()
    {
        cmd.start();
        
        while (flag)
        {
            timeMonitor();
            
            if (cmd.isAlive())
            {
                ThreadUtil.threadSleep(waitForTime);
                
                mission.setEnd_time(System.currentTimeMillis());
            }
            else
            {
                mission.setEnd_time(System.currentTimeMillis());
                break;
            }
        }
    }
    
    /**
     * <p>清理线程</p>
     */
    private void cleanCMD()
    {
        Log.debug(" try to stop the thread of " + mission.toString());
        CMD temp = cmd;
        
        cmd = null;
        
        temp.stopCMD();
        temp.interrupt();
    }
    
    /**
     * <p>执行线程监控，当执行时间超出任务允许的超时时间时，当前执行视为失败</p>
     */
    private void timeMonitor()
    {
        Log.debug(" now the CMD is run " + (mission.getEnd_time() - mission.getStart_time()));
        if (mission.getTimeout() < (mission.getEnd_time() - mission.getStart_time()))
        {
            flag = false;
            mission.setExit_code(Const.FAILED);
            
            Log.notice(" the mission is overtime " + mission);
        }
        
        Log.debug(" the mission is running " + mission.toString());
    }
    
    /**
     * <p>返回执行的最终结果，执行最终非为2大类：
     * 1.有执行结果 （正常结果，异常结果）
     * 2.任务未执行完，超出执行时间</p>
     */
    private void setResponse()
    {
        if (flag)
        {
            mp.missionFinish(mission);
        }
        else
        {
            cleanCMD();
            mp.timeOut(mission);
        }
        
        Missions.getMission().removeAliveMission(mission);
        System.gc();
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
