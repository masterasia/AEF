package dy.arch.aef;

import dy.arch.aef.bean.MissionConf;
import dy.arch.aef.rpc.MissionServer;
import dy.arch.util.Conf;
import dy.arch.util.Log;
import dy.arch.util.UtilException;

/**
 * 即时任务 启动接口
 * @author robert.xu
 *
 */
public class MissionReceiver
{
    public static void init()
    {
        Log.init("missionreceiver", Conf.getConf("thread", "common.logPath"),
                Short.parseShort(Conf.getConf("thread", "missionreceiver.logLevel")));
    }
    
    public static void start() throws UtilException
    {
        init();
        
        MissionConf conf = new MissionConf("thread");
        MissionServer server = new MissionServer(conf);
        
        server.start();
    }
}
