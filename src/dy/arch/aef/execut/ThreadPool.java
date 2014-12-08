package dy.arch.aef.execut;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.bean.Missions;
import dy.arch.util.Conf;
import dy.arch.util.Log;

/**
 * 任务线程池
 * @author robert.xu
 *
 */
public class ThreadPool extends Thread
{
    // the core thread nums
    private int                     corePoolSize    = 8;
    
    // max thread
    private int                     maximumPoolSize = 30;
    
    private long                    keepAliveTime   = 3600;
    
    private BlockingQueue<Runnable> queue;
    
    private BlockingQueue<Mission>  missions;
    
    private ThreadPoolExecutor      pools;
    
    public ThreadPool()
    {
        init();
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            Mission v = new Mission();
            try
            {
                v = missions.take();
                
                PoolExecutor m = new PoolExecutor();
                m.setMission(v);
                
                pools.execute(m);
            }
            catch (InterruptedException e)
            {
                Log.fatal(" try to new a thread to run the mission failed !" + v.toString());
            }
        }
        
    }
    
    private void init()
    {
        Log.init("threadpool", Conf.getConf("thread", "common.logPath"),
                Short.parseShort(Conf.getConf("thread", "threadpool.logLevel")));
        
        corePoolSize = Integer.parseInt(Conf.getConf("thread", "threadpool.corePoolSize"));
        
        maximumPoolSize = Integer.parseInt(Conf.getConf("thread", "threadpool.maximumPoolSize"));
        
        keepAliveTime = Long.parseLong(Conf.getConf("thread", "threadpool.keepAliveTime"));
        
        missions = Missions.getMission().getQueue();
        
        queue = new LinkedBlockingQueue<Runnable>();
        
        //new a cache thread pool
        pools = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
        
        //set the type what will java do when zhe pool is full.
        pools.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        Log.notice("the thread pool is already : " + pools.toString());
    }
}
