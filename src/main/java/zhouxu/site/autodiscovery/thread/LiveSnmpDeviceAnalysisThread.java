package cn.pioneeer.dcim.saas.autodiscovery.thread;

import cn.pioneeer.dcim.saas.autodiscovery.dto.CommunityTargetDto;
import cn.pioneeer.dcim.saas.autodiscovery.dto.LiveSnmpDeviceDto;
import cn.pioneeer.dcim.saas.autodiscovery.utils.SnmpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * Description:snmp存活设备检测线程
 * User: zhouxu
 * Date: 2018-11-20 15:37
 */
public class LiveSnmpDeviceAnalysisThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(LiveSnmpDeviceAnalysisThread.class);

    //存活ip列表
    private Vector<LiveSnmpDeviceDto> liveSnmpDeviceDtos;

    //备选库
    private List<String> tmpIps;

    //开启线程数
    private static Hashtable<Long,Integer> countMap=new Hashtable<Long,Integer>();

    //是否完成
    private static Hashtable<Long,Boolean> statusMap=new Hashtable<Long, Boolean>();
//    //snmp服务团体名
//    private String community;
//    //snmp端口
//    private int port;
//    //snmp版本
//    private int version;

    //备选community库
    private List<CommunityTargetDto> communityTargetDtos;

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

    public LiveSnmpDeviceAnalysisThread(Vector<LiveSnmpDeviceDto> liveSnmpDeviceDtos, List<String> tmpIps, long time, List<CommunityTargetDto> communityTargetDtos){
        this.liveSnmpDeviceDtos = liveSnmpDeviceDtos;
        this.tmpIps = tmpIps;
        this.time = time;
        this.communityTargetDtos = communityTargetDtos;
    }

    @Override
    public void run() {
        if(this.tmpIps!=null&&this.tmpIps.size()>0&&this.communityTargetDtos!=null&&this.communityTargetDtos.size()>0){
            for(int i=0;i<this.tmpIps.size();i++){
                boolean snmpServiceStatus=false;
                for(CommunityTargetDto communityTargetDto : this.communityTargetDtos){
                    boolean snmpStatus = SnmpUtils.snmpStatus(tmpIps.get(i),communityTargetDto.getCommunity()
                            ,communityTargetDto.getPort(),
                            SnmpUtils.parseVersion(communityTargetDto.getVersion()));
                    if(snmpStatus){
                        LiveSnmpDeviceDto liveSnmpDeviceDto = new LiveSnmpDeviceDto(this.tmpIps.get(i),communityTargetDto);
                        this.liveSnmpDeviceDtos.add(liveSnmpDeviceDto);
                        logger.info("ip:"+tmpIps.get(i)+" snmp service status is on");
                        snmpServiceStatus = true;
                        continue;
                    }
                }
                if(!snmpServiceStatus){
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
