package dy.arch.aef;

import dy.arch.aef.execut.ThreadPool;
import dy.arch.util.UtilException;

/**
 * AEF启动
 * @author robert.xu
 *
 */
public class AEFController
{
    public static void main(String[] args) throws UtilException
    {
        aefStart();
    }
    
    public static void aefStart() throws UtilException
    {
        ThreadPool tp = new ThreadPool();
        tp.start();
        
        MissionLoader.start();
        
        MissionReceiver.start();
    }
}
