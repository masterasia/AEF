package dy.arch.aef.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dy.arch.util.Conf;

/**
 * 校验工具类
 * @author robert.xu
 *
 */
public class Verification
{
    private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
    /**
     * 获取API接口校验
     * @return
     */
    public static String getAPIVer()
    {
        Map<String, String> parameters = new HashMap<String, String>();
        
        String app = Conf.getConf("verification", "api.app");
        String appKey = Conf.getConf("verification", "api.appkey");
        
        parameters.put("_app", app);
        
        String times = System.currentTimeMillis()/1000 + "";
        parameters.put("_time", times);
        
        try
        {
            String str = getMD5(app + appKey + times);
            
            parameters.put("_sign", str.substring(0, 8));
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        
        return changeMapToString(parameters);
    }
    
    /**
     * 获取字符串对应MD5内容
     * @param bases
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String getMD5(String bases) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        byte[] before = (bases).getBytes();
        byte[] after = md.digest(before);
        
        int len = after.length;
        
        char[] str = new char[len * 2];
        int index = 0;
        
        for (int i = 0; i < len; i++)
        {
            byte byteB = after[i];
            str[index++] = hexDigits[byteB >>> 4 & 0xf];
            str[index++] = hexDigits[byteB & 0xf];
        }
        
        return new String(str);
    }
    
    /**
     * MAP转化为STRING
     * @param map
     * @return
     */
    private static String changeMapToString(Map<String, String> map)
    {
        StringBuffer sb = new StringBuffer("");
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> entry : set)
        {
            sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        
        return sb.toString();
    }
}
