package dy.arch.aef.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import dy.arch.aef.bean.Mission;
import dy.arch.aef.bean.Missions;
import dy.arch.util.Log;
import dy.arch.util.Value;
import dy.arch.util.rpc.ServerException;
import dy.arch.util.rpc.ServerExecutor;

/**
 * 即时任务 执行器 将即时任务插入待执行集合
 * @author robert.xu
 *
 */
public class MissionExecutor implements ServerExecutor
{
    private Map<String, Value> mapValue;
    
    private Mission            mission;
    
    private Value              succ;
    
    private String             cmd;
    
    public MissionExecutor(BlockingQueue<Mission> missions)
    {
        mapValue = new HashMap<String, Value>();
        mapValue.put("code", new Value(0));
        mapValue.put("message", new Value("succ"));
        
        succ = new Value(mapValue);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Value execute(Value request) throws ServerException
    {
        try
        {
            mapValue = (HashMap<String, Value>) request.getValue();
            
            cmd = (String) mapValue.get("cmd").getValue();
        }
        catch (Exception e)
        {
            throw new ServerException("Bad request", ServerException.BAD_REQUEST);
        }
        Log.notice("Accept new request, req:" + request.pack());
        check();
        recordAndSave(request);
        
        return succ;
    }
    
    private void check() throws ServerException
    {
        if (cmd == null)
        {
            throw new ServerException(" the cmd is lost ", ServerException.BAD_REQUEST);
        }
    }
    
    private void recordAndSave(Value request)
    {
        this.mission = new Mission(request);
        Missions.getMission().add(mission);
    }
}
