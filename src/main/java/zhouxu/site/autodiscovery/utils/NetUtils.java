package cn.pioneeer.dcim.saas.autodiscovery.utils;

import cn.pioneeer.dcim.saas.autodiscovery.dto.CommunityTargetDto;
import cn.pioneeer.dcim.saas.autodiscovery.dto.LiveSnmpDeviceDto;
import cn.pioneeer.dcim.saas.autodiscovery.thread.LiveIpAnalysisThread;
import cn.pioneeer.dcim.saas.autodiscovery.thread.LiveSnmpDeviceAnalysisThread;
import cn.pioneeer.dcim.saas.autodiscovery.thread.LiveSnmpIpAnalysisThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * Description:网络判断工具
 * User: zhouxu
 * Date: 2018-11-20 14:33
 */
public class NetUtils {

    private  static  final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    //默认检测存活IP开启的线程数
    public static int DEFALUT_IPRANGE_THREAD_COUNT=50;

    //默认检测snmp服务是否开启线程数
    public static int DEFAULT_SNMP_STATUS_CHECK_THREAD_COUNT=50;

    //默认ping超时为2s
    public static int DEFAULT_PING_TIME_OUT =2000;

    /**
     * @Author zhouxu
     * @Description //ping 判断网络是否通
     * @Date 2018/11/20 14:36
     * @Param [ipAddress]
     * @return boolean
     * @throws
     **/
    public static boolean ping(String ipAddress){
        boolean status = false;     // 当返回值是true时，说明host是可用的，false则不可。
        try {
            status = InetAddress.getByName(ipAddress).isReachable(DEFAULT_PING_TIME_OUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String statusLabel=status?"on":"off";
        logger.info("ip:"+ipAddress+" ping service is "+ statusLabel);
        return status;
    }

    /**
     * @Author zhouxu
     * @Description //存活ip判断
     * @Date 2018/11/20 15:26
     * @Param [startId, endIp]
     * @return java.util.List<java.lang.String>
     * @throws
     **/
    public static List<String> liveIps(String startId,String endIp) {
        logger.info("======================= start find live ip bettewn ["+startId+"-"+endIp+"] =======================");
        long start = System.currentTimeMillis();
        Vector<String> liveIps = new Vector<String>();
        List<String> ips = IPRangeUtils.rangeIps(startId, endIp);
        if(ips.size()==0){
            return liveIps;
        }
        //需要开启线程数矫正
        int threadCount=DEFALUT_IPRANGE_THREAD_COUNT;
        if(ips.size()<threadCount){
            threadCount = ips.size();
        }
        List<List<String>> spliteIps = ListUtils.splite(ips, threadCount);

        //从初始化thread
        long time = System.currentTimeMillis();
        LiveIpAnalysisThread.init(time,threadCount);
        for(int i=0;i<spliteIps.size();i++){
            LiveIpAnalysisThread liveIpAnalysisThread = new LiveIpAnalysisThread(liveIps,spliteIps.get(i),time);
            new Thread(liveIpAnalysisThread).start();
        }
        while (!LiveIpAnalysisThread.getStatus(time)){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long used = (System.currentTimeMillis()-start)/1000;
        logger.info("======================= finished find live ip bettewn ["+startId+"-"+endIp+"] total cost: "+used+"s =======================");
        logger.info("ping liveIps:"+Arrays.toString(liveIps.toArray()));
        return liveIps;
    }

    /**
     * @Author zhouxu
     * @Description //检测snmp服务正常的设备
     * @Date 2018/11/21 14:59
     * @Param [startId, endIp]
     * @return java.util.List<java.lang.String>
     * @throws
     **/
    public static List<String> liveSnmpIps(List<String> tmpIps, String community,int port,int version){
        logger.info("======================= start find live snmp ips =======================");
        long start = System.currentTimeMillis();
        Vector<String> liveIps = new Vector<String>();
        if(tmpIps==null||tmpIps.size()==0){
            return liveIps;
        }
        //需要开启线程数矫正
        int threadCount=DEFAULT_SNMP_STATUS_CHECK_THREAD_COUNT;
        if(tmpIps.size()<threadCount){
            threadCount = tmpIps.size();
        }
        List<List<String>> spliteIps = ListUtils.splite(tmpIps, threadCount);

        //从初始化thread
        long time = System.currentTimeMillis();
        LiveSnmpIpAnalysisThread.init(time,threadCount);
        for(int i=0;i<spliteIps.size();i++){
            LiveSnmpIpAnalysisThread liveSnmpIpAnalysisThread = new LiveSnmpIpAnalysisThread(liveIps,spliteIps.get(i),time,community,port,version);
            new Thread(liveSnmpIpAnalysisThread).start();
        }
        while (!LiveSnmpIpAnalysisThread.getStatus(time)){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long used = (System.currentTimeMillis()-start)/1000;
        logger.info("======================= finished find live snmp ips  total cost: "+used+"s =======================");
        logger.info("snmp liveIps:"+Arrays.toString(liveIps.toArray()));
        return liveIps;
    }

    /**
     * @Author zhouxu
     * @Description //检测snmp服务正常的ip
     * @Date 2018/11/21 14:59
     * @Param [startId, endIp]
     * @return java.util.List<java.lang.String>
     * @throws
     **/
    public static List<LiveSnmpDeviceDto> liveSnmpDevices(List<String> tmpIps, List<CommunityTargetDto> communityTargetDtos){
        logger.info("======================= start find live snmp ips =======================");
        long start = System.currentTimeMillis();
        Vector<LiveSnmpDeviceDto> liveSnmpDeviceDtos = new Vector<LiveSnmpDeviceDto>();
        if(tmpIps==null||tmpIps.size()==0){
            return liveSnmpDeviceDtos;
        }
        //需要开启线程数矫正
        int threadCount=DEFAULT_SNMP_STATUS_CHECK_THREAD_COUNT;
        if(tmpIps.size()<threadCount){
            threadCount = tmpIps.size();
        }
        List<List<String>> spliteIps = ListUtils.splite(tmpIps, threadCount);

        //从初始化thread
        long time = System.currentTimeMillis();
        LiveSnmpDeviceAnalysisThread.init(time,threadCount);
        for(int i=0;i<spliteIps.size();i++){
            LiveSnmpDeviceAnalysisThread liveSnmpDeviceAnalysisThread = new LiveSnmpDeviceAnalysisThread(liveSnmpDeviceDtos,spliteIps.get(i),time,communityTargetDtos);
            new Thread(liveSnmpDeviceAnalysisThread).start();
        }
        while (!LiveSnmpDeviceAnalysisThread.getStatus(time)){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long used = (System.currentTimeMillis()-start)/1000;
        logger.info("======================= finished find live snmp ips  total cost: "+used+"s =======================");
        logger.info("snmp liveIps:"+Arrays.toString(liveSnmpDeviceDtos.toArray()));
        return liveSnmpDeviceDtos;
    }
}
