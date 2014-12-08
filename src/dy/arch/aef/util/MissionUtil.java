package dy.arch.aef.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dy.arch.aef.bean.Mission;
import dy.arch.util.Log;

public class MissionUtil
{
    /**
     * <p>解析请求，转化为任务列表</p>
     * @param value
     * @return
     */
    public static List<Mission> getMission(String value)
    {
        List<Mission> missions = new ArrayList<Mission>();
        
        try
        {
            JSONObject json = new JSONObject(value);
            if (json.has("data") && null != json.getJSONArray("data"))
            {
                
                JSONArray jsons = json.getJSONArray("data");
                for (int i = 0; i < jsons.length(); i++)
                {
                    JSONObject temp = new JSONObject(jsons.get(i) + "");
                    if (temp.has("cmd") && JSONObject.NULL != temp.get("cmd"))
                    {
                        Mission mission = new Mission(temp);
                        
                        missions.add(mission);
                    }
                }
            }
        }
        catch (JSONException e)
        {
            Log.fatal(e.getMessage());
        }
        
        return missions;
    }
    
    /**
     * <p>重置任务时间</p>
     * @param mission
     */
    public static void setNowTime(Mission mission)
    {
        mission.setStart_time(System.currentTimeMillis());
        mission.setEnd_time(System.currentTimeMillis());
    }
}
