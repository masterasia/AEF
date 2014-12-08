package dy.arch.aef.bean;

import dy.arch.util.ConfReader;
import dy.arch.util.UtilException;
import dy.arch.util.rpc.ServerConf;

/**
 * 任务配置文件对象
 * @author robert.xu
 *
 */
public class MissionConf extends ServerConf
{
    
    private int maxStoreQueueCount = 20;
    
    public MissionConf(String file) throws UtilException
    {
        setConfReaderByFile(file);
        loadFromConfFile();
    }
    
    @Override
    public void loadFromConfFile() throws UtilException
    {
        super.loadFromConfFile();
        
        try
        {
            ConfReader conf = getConfReader();
            maxStoreQueueCount = conf.getInteger("missionreceiver.maxStoreQueueCount");
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            throw new UtilException("Conf parse error");
        }
    }
    
    public int getMaxStoreQueueCount()
    {
        return maxStoreQueueCount;
    }
    
    public void setMaxStoreQueueCount(int maxStoreQueueCount)
    {
        this.maxStoreQueueCount = maxStoreQueueCount;
    }
    
}
