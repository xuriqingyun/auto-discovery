package cn.pioneeer.dcim.saas.autodiscovery.thread;


import cn.pioneeer.dcim.saas.autodiscovery.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * Description:存活ip检测线程
 * User: zhouxu
 * Date: 2018-11-20 15:37
 */
public class LiveIpAnalysisThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(LiveIpAnalysisThread.class);

    //存活ip列表
    private Vector<String> liveIps;

    //备选库
    private List<String> tmpIps;

    //开启线程数
    private static Hashtable<Long,Integer> countMap=new Hashtable<Long,Integer>();

    //是否完成
    private static Hashtable<Long,Boolean> statusMap=new Hashtable<Long, Boolean>();

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

    public LiveIpAnalysisThread(Vector<String> liveIps,List<String> tmpIps,long time){
        this.liveIps = liveIps;
        this.tmpIps = tmpIps;
        this.time = time;
    }

    @Override
    public void run() {
        if(this.tmpIps!=null&&this.tmpIps.size()>0){
            for(int i=0;i<this.tmpIps.size();i++){
                boolean ping = NetUtils.ping(tmpIps.get(i));
                if(ping){
                    this.liveIps.add(this.tmpIps.get(i));
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
