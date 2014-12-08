package dy.arch.aef.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import dy.arch.util.Log;
import dy.arch.util.Value;
import dy.arch.util.constant.Const;

/**
 * <p>
 * 任务 Java bean 对象
 * </p>
 * 
 * @author robert.xu
 *
 */
public class Mission
{
    private long               start_time  = 0;
    
    private long               frequency   = 0;
    
    private long               id          = 0L;
    
    private long               end_time    = 0;
    
    private int                exit_code   = 0;
    
    private boolean            retryStatus = true;
    
    private long               retryTimes  = 3;
    
    private long               timeout     = 3600000;
    
    private String             cmd         = "";
    
    private String             stdout      = "";
    
    private String             stderr      = "";
    
    private Value              value;
    
    private Map<String, Value> params;
    
    private String             name;
    
    private Process            process;
    
    public Mission()
    {
        
    }
    
    public Mission(Value value)
    {
        this.value = value;
        this.params = value.toMap();
        init();
    }
    
    public Mission(JSONObject jsonObject)
    {
        params = new HashMap<String, Value>();
        Iterator<String> it = jsonObject.keys();
        while (it.hasNext())
        {
            String key = it.next();
            if (JSONObject.NULL == jsonObject.get(key))
            {
                params.put(key, null);
            }
            else
            {
                params.put(key, new Value(jsonObject.getString(key)));
            }
        }
        init();
    }
    
    private void init()
    {
        try
        {
            this.cmd = params.get("cmd").toString();
            this.start_time = Long.parseLong(null == params.get("next_time") ? "0" : params.get("next_time")
                    .toString());
            this.retryTimes = Long.parseLong(null == params.get("retry_time") ? "0" : (params.get(
                    "retry_time").isLong() ? params.get("retry_time").toLong() + "" : params
                    .get("retry_time").toString()));
            this.retryStatus = null == params.get("retry_time") || 0L == this.retryTimes ? false : true;
            this.timeout = Long.parseLong(null == params.get("timeout") ? "3600" : params.get("timeout")
                    .toString());
            this.frequency = Long.parseLong(null == params.get("frequency") ? "0" : params.get("frequency")
                    .toString());
            this.id = Long.parseLong(null == params.get("id") ? "0" : (params.get("id").isLong() ? params
                    .get("id").toLong() + "" : params.get("id").toString()));
            this.name = null == params.get("name") ? " linux cmd " + this.cmd : params.get("name").toString();
        }
        catch (Exception e)
        {
            Log.fatal(" the request miss important thing " + value.pack());
        }
    }
    
    public int getCMDType()
    {
        if (this.cmd.trim().startsWith("sh "))
        {
            return 0;
        }
        return 1;
    }
    
    public long getStart_time()
    {
        return start_time;
    }
    
    public void setStart_time(long start_time)
    {
        this.start_time = start_time / Const.TIME_CARRY;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getEnd_time()
    {
        return end_time;
    }
    
    public void setEnd_time(long end_time)
    {
        this.end_time = end_time / Const.TIME_CARRY;
    }
    
    public int getExit_code()
    {
        return exit_code;
    }
    
    public void setExit_code(int exit_code)
    {
        this.exit_code = exit_code;
    }
    
    public boolean isRetryStatus()
    {
        return retryStatus;
    }
    
    public void setRetryStatus(boolean retryStatus)
    {
        this.retryStatus = retryStatus;
    }
    
    public long getRetryTimes()
    {
        return retryTimes;
    }
    
    public void setRetryTimes(long retryTimes)
    {
        this.retryTimes = retryTimes;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    public String getCmd()
    {
        return cmd;
    }
    
    public void setCmd(String cmd)
    {
        this.cmd = cmd;
    }
    
    public Process getProcess()
    {
        return process;
    }
    
    public void setProcess(Process process)
    {
        this.process = process;
    }
    
    public String getStdout()
    {
        return stdout;
    }
    
    public void setStdout(String stdout)
    {
        this.stdout = stdout;
    }
    
    public String getStderr()
    {
        return stderr;
    }
    
    public void setStderr(String stderr)
    {
        this.stderr = stderr;
    }
    
    public long getFrequency()
    {
        return frequency;
    }
    
    public void setFrequency(long frequency)
    {
        this.frequency = frequency;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Map<String, Value> getParams()
    {
        return params;
    }
    
    public void setParams(Map<String, Value> params)
    {
        this.params = params;
    }
    
    @Override
    public String toString()
    {
        return "start_time=" + start_time + "&id=" + id + "&end_time=" + end_time + "&exit_code=" + exit_code
                + "&retryStatus=" + retryStatus + "&retryTimes=" + retryTimes + "&timeout=" + timeout
                + "&cmd=" + cmd + "&stdout=" + stdout + "&stderr=" + stderr;
    }
    
    public String toMessage()
    {
        return "[start_time:" + start_time + ", id:" + id + ", end_time:" + end_time + ", exit_code:"
                + exit_code + ", retryStatus:" + retryStatus + ", retryTimes:" + retryTimes + ", timeout:"
                + timeout + ", cmd:" + cmd + ", stdout:" + stdout + ", stderr:" + stderr + " ]";
    }
}
