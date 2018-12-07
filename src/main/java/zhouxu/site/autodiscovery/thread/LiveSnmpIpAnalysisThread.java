package cn.pioneeer.dcim.saas.autodiscovery.thread;

import cn.pioneeer.dcim.saas.autodiscovery.utils.SnmpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * Description:snmp服务状态检测线程
 * User: zhouxu
 * Date: 2018-11-20 15:37
 */
public class LiveSnmpIpAnalysisThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(LiveSnmpIpAnalysisThread.class);

    //存活ip列表
    private Vector<String> liveIps;

    //备选库
    private List<String> tmpIps;

    //开启线程数
    private static Hashtable<Long,Integer> countMap=new Hashtable<Long,Integer>();

    //是否完成
    private static Hashtable<Long,Boolean> statusMap=new Hashtable<Long, Boolean>();
    //snmp服务团体名
    private String community;
    //snmp端口
    private int port;
    //snmp版本
    private int version;

    private Long time;

    /**
     * @Author zhouxu
     * @Description //初始化线程参数
     * @Date 2018/11/20 16:27
     * @Param [time, count]
     * @return void
     * @throws
     **/
    public static void init(long time,int count){
        countMap.put(time,count);
        statusMap.put(time,false);
    }

    /**
     * @Author zhouxu
     * @Description 获取线程状态
     * @Date 2018/11/20 16:30
     * @Param [time]
     * @return boolean
     * @throws
     **/
    public static boolean getStatus(long time){
        boolean status = statusMap.get(time);
        if(status){
            countMap.remove(time);
            statusMap.remove(time);
        }
        return status;
    }

    public LiveSnmpIpAnalysisThread(Vector<String> liveIps, List<String> tmpIps, long time, String community,int port,int version){
        this.liveIps = liveIps;
        this.tmpIps = tmpIps;
        this.time = time;
        this.community = community;
        this.port = port;
        this.version=version;
    }

    @Override
    public void run() {
        if(this.tmpIps!=null&&this.tmpIps.size()>0){
            for(int i=0;i<this.tmpIps.size();i++){
                boolean snmpStatus = SnmpUtils.snmpStatus(tmpIps.get(i),this.community,this.port,this.version);
                if(snmpStatus){
                    logger.info("ip:"+tmpIps.get(i)+" snmp service status is on");
                    this.liveIps.add(this.tmpIps.get(i));
                }else{
                    logger.info("ip:"+tmpIps.get(i)+" snmp service status is off");
                }
            }
        }
        synchronized (countMap){
            countMap.put(this.time,countMap.get(this.time)-1);
            if(countMap.get(this.time)<=0){
                statusMap.put(this.time,true);
            }
        }
    }
}
