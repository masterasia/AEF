package dy.arch.aef;

import dy.arch.aef.timer.Loader;
import dy.arch.util.Conf;
import dy.arch.util.Log;

/**
 * 定时任务启动接口
 * @author robert.xu
 *
 */
public class MissionLoader
{
    public static void init()
    {
        Log.init("missionloader", Conf.getConf("thread", "common.logPath"),
            Short.parseShort(Conf.getConf("thread", "missionloader.logLevel")));
    }
    
    public static void start()
    {
        init();
        
        Loader loader = new Loader();
        loader.start();
    }
}
