package cn.pioneeer.dcim.saas.autodiscovery.utils;

import org.snmp4j.smi.VariableBinding;

/**
 * Created with IntelliJ IDEA.
 * Description:Snmp扩展工具
 * User: zhouxu
 * Date: 2018-11-22 14:58
 */
public class SnmpExtentionUtils {

    //具有路由功能
    static final String HAS_ROUTER_FUNC="1";
    //路由器检测所用OID
    //.1.3.6.1.2.1.4.1.0
    //forwarding(1) – for routing capable devices
    //not-forwarding(2) – for all other devices

    //.1.3.6.1.2.1.17.2.1.0 没有值
    static final String[] ROUTER_KEYS=new String[]{".1.3.6.1.2.1.4.1.0"};

    //交换机检测所用OID
    static final String[] SWTICH_KEYS=new String[]{".1.3.6.1.2.1.17.1.1.0",".1.3.6.1.2.1.17.2.1.0",".1.3.6.1.2.1.17.2.6.0",".1.3.6.1.2.1.17.2.7.0"};

    //打印机检测所用OID
    static final String[] PRINTER_KEYS=new String[]{".1.3.6.1.2.1.25.3.2.1.5.1",".1.3.6.1.2.1.25.3.5.1.1.1",".1.3.6.1.2.1.43.17.6.1.4",".1.3.6.1.2.1.43.17.6.1.5"};

    //主机检测所用OID
    static final String[] HOST_KEYS=new String[]{".1.3.6.1.2.1.25.1.1.0"};

    //防火墙检测所用OID
    /**
     * @Author zhouxu
     * @Description 判断是否是路由器
     * @Date 2018/11/22 15:00
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean isRouter(String ip, String community,int port,int version){
        VariableBinding routeVariableBinding = SnmpUtils.get(ip, community, ROUTER_KEYS[0], port, version);
        if(routeVariableBinding!=null&&routeVariableBinding.getVariable().toString().equals(HAS_ROUTER_FUNC)){
            //has router func exclude 3 layer switch
            if(!isSwitch(ip,community,port,version)&&!isHost(ip,community,port,version)){
                return true;
            }
        }
        return false;
    }

    /**
     * @Author zhouxu
     * @Description 判断是否是路由交换机（三层交换机）
     * @Date 2018/11/23 9:36
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean isRouterSwitch(String ip,String community,int port,int version){
        VariableBinding routeVariableBinding = SnmpUtils.get(ip, community, ROUTER_KEYS[0], port, version);
        if(routeVariableBinding!=null&&routeVariableBinding.getVariable().toString().equals(HAS_ROUTER_FUNC)){
            if(isSwitch(ip,community,port,version)&&!isHost(ip,community,port,version)){
                return true;
            }
        }
        return false;
    }

    /**
     * @Author zhouxu
     * @Description 判断是否是交换机
     * @Date 2018/11/22 15:00
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean isSwitch(String ip, String community,int port,int version){
        return oidKeysStatusCheck(SWTICH_KEYS,ip,community,port,version)&&!isHost(ip,community,port,version);
    }

    /**
     * @Author zhouxu
     * @Description 用于type检测的oid状态获取
     * @Date 2018/11/22 17:41
     * @Param [keys, ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean oidKeysStatusCheck(String[] keys,String ip, String community,int port,int version){
        for(int i=0;i<keys.length;i++){
            VariableBinding variableBinding = SnmpUtils.get(ip, community, keys[i], port, version);
            if(variableBinding==null){
                return false;
            }
        }
        return true;
    }

    /**
     * @Author zhouxu
     * @Description 判断是否打印机
     * @Date 2018/11/22 15:00
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean isPrinter(String ip, String community,int port,int version){
        return oidKeysStatusCheck(PRINTER_KEYS,ip,community,port,version);
    }

    /**
     * @Author zhouxu
     * @Description 判断是否是主机
     * @Date 2018/11/22 15:01
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean isHost(String ip, String community,int port,int version){
        return oidKeysStatusCheck(HOST_KEYS,ip,community,port,version);
    }

    /**
     * @Author zhouxu
     * @Description 判断是否是防火墙
     * @Date 2018/11/22 15:01
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean isFireWall(String ip, String community,int port,int version){
        return false;
    }
}



