package dy.arch.aef.rpc;

import java.util.concurrent.BlockingQueue;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.bean.MissionConf;
import dy.arch.aef.bean.Missions;
import dy.arch.util.Log;
import dy.arch.util.rpc.Server;
import dy.arch.util.rpc.ServerThread;

/**
 * mission rpc 实现util的rpc 
 * @author robert.xu
 *
 */
public class MissionServer extends Server
{
    private MissionConf            conf;
    
    private BlockingQueue<Mission> missions;
    
    public MissionServer(MissionConf conf)
    {
        super(conf);
        this.conf = conf;
    }
    
    @Override
    protected ServerThread newThread()
    {
        return new MissionThread(conf.getMaxWaitingCount(), new MissionExecutor(missions));
    }
    
    @Override
    protected void prepare()
    {
        missions = Missions.getMission().getQueue();
        Log.notice("MissionServer start succ");
    }
    
    @Override
    protected void finish()
    {
        Log.notice("Mission receiver start successfully, ready to accept new request");
    }
    
}
