package dy.arch.aef.execut;

/**
 * <p>实际执行流程 工厂类，根据类别返回合适的执行流程</p>
 * @author robert.xu
 *
 */
public class CMDFactory
{
    public static CMD getExecutor(int type)
    {
        switch (type)
        {
        case 0:
            return new LinuxCMD();
            
        default:
            return new LinuxCMD();
        }
    }
}
