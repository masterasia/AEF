package dy.arch.aef.rpc;

import dy.arch.util.rpc.ServerExecutor;
import dy.arch.util.rpc.ServerThread;

public class MissionThread extends ServerThread
{
    public MissionThread(ServerExecutor executor)
    {
        super(executor);
    }

    public MissionThread(short maxWaitingCount, ServerExecutor executor)
    {
        super(maxWaitingCount, executor);
    }

    
}
