package cn.pioneeer.dcim.saas.autodiscovery.utils;

import cn.pioneeer.dcim.saas.autodiscovery.exception.SnmpNoOidException;
import cn.pioneeer.dcim.saas.autodiscovery.exception.SnmpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:Snmp工具提供最基本的get getlist walk
 * User: zhouxu
 * Date: 2018-11-15 18:03
 */
public class SnmpUtils {

    private static  final Logger logger = LoggerFactory.getLogger(SnmpUtils.class);

    //用于检测snmp服务状态端口
    public static final String DEFAULT_SNMP_STATUS_OID=".1.3.6.1.2.1.1.1.0";

    //默认版本v2
    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    //默认协议
    public static final String DEFAULT_PROTOCOL = "udp";
    //默认snmp端口
    public static final int DEFAULT_PORT = 161;
    //默认溢出时间
    public static final long DEFAULT_TIMEOUT = 2 * 1000L;
    //默认重发次数
    public static final int DEFAULT_RETRY = 2;
    //默认未找到Oid
    public static final String NOSUCHOIDPREFIX="noSuch";
    /**
     * @Author zhouxu
     * @Description 创建对象communityTarget，用于返回target
     * @Date 2018/11/15 18:04
     * @Param [ip, community]
     * @return org.snmp4j.CommunityTarget
     * @throws
     **/
    public static CommunityTarget createDefault(String ip, String community) {
        return createCommunityTarget(ip,community,DEFAULT_PORT,DEFAULT_VERSION);
    }

    /**
     * @Author zhouxu
     * @Description 创建对象communityTarget，用于返回target
     * @Date 2018/11/15 18:04
     * @Param [ip, community]
     * @return org.snmp4j.CommunityTarget
     * @throws
     **/
    public static CommunityTarget createCommunityTarget(String ip, String community,int port,int version) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip
                + "/" + port);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(version);
        target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
        target.setRetries(DEFAULT_RETRY);
        return target;
    }

    /**
     * @Author zhouxu
     * @Description //version标准化
     * @Date 2018/11/20 17:45
     * @Param [version]
     * @return int
     * @throws
     **/
    public static int parseVersion(String version){
        if(version.equals("v1")||version.equals("V1")){
            return SnmpConstants.version1;
        }else if(version.equals("v2")||version.equals("V2")){
            return SnmpConstants.version2c;
        }else if(version.equals("v3")||version.equals("V3")){
            return SnmpConstants.version3;
        }
        return SnmpConstants.version2c;
    }

    /**
     * @Author zhouxu
     * @Description //获取snmp服务状态
     * @Date 2018/11/21 13:48
     * @Param [ip, community, port, version]
     * @return boolean
     * @throws
     **/
    public static boolean snmpStatus(String ip, String community, int port,int version){
        VariableBinding variableBinding = get(ip, community, DEFAULT_SNMP_STATUS_OID, port, version);
        if(variableBinding==null){
//            logger.info("ip:"+ip+" snmp service status is off");
            return false;
        }
//        logger.info("ip:"+ip+" snmp service status is on");
        return true;
    }

    /**
     * @Author zhouxu
     * @Description //根据OID，获取单条消息
     * @Date 2018/11/15 18:04
     * @Param [ip, community, oid,port,version]
     * @return void
     * @throws
     **/
    public static VariableBinding get(String ip, String community, String oid,int port,int version) {
        CommunityTarget target = createCommunityTarget(ip, community,port,version);
        Snmp snmp = null;
        try {
//            logger.info("ip is "+ip+" snmp get oid:"+oid+" start..........");
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            PDU response = respEvent.getResponse();

            if (response == null) {
//                throw new SnmpTimeOutException(ip,oid);
            } else {
                if(response.size()>0){
                    VariableBinding vb=response.get(0);
//                    logger.info(vb.getOid() + " = " + vb.getVariable());
                    if(vb.getVariable().toString().startsWith(NOSUCHOIDPREFIX)){
                        throw new SnmpNoOidException(ip,oid);
                    }
//                    logger.info("ip is "+ip+" snmp get oid:"+oid+" finished..........");
                    return vb;
                }
            }
        }catch (SnmpNoOidException snmpNoOidException){
            logger.error(snmpNoOidException.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return  null;
    }

    /**
     * @Author zhouxu
     * @Description 检测是否没有改Oid
     * @Date 2018/11/21 11:19
     * @Param [variableBinding]
     * @return boolean
     * @throws
     **/
    public static boolean isNoSuchOid(VariableBinding variableBinding){
       if(variableBinding.getVariable().toString().startsWith(NOSUCHOIDPREFIX)){
           return true;
       }
       return false;
    }

    /**
     * @Author zhouxu
     * @Description 根据OID列表，一次获取多条OID数据，并且以List形式返回
     * @Date 2018/11/15 18:07
     * @Param [ip, community, oids,port,version]
     * @return void
     * @throws
     **/
    public static List<VariableBinding> getList(String ip, String community, List<String> oids,int port,int version)
    {
        List<VariableBinding> variableBindings =new ArrayList<VariableBinding>();
        CommunityTarget target = createCommunityTarget(ip, community,port,version);
        Snmp snmp = null;
        try {
            logger.info("ip is "+ip+" snmp list oid:"+Arrays.toString(oids.toArray())+" start..........");
            PDU pdu = new PDU();

            for(String oid:oids)
            {
                pdu.add(new VariableBinding(new OID(oid)));
            }

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            PDU response = respEvent.getResponse();

            if (response == null) {
//                throw new SnmpTimeOutException(ip,Arrays.toString(oids.toArray()));
            } else {
                assemblyResults(ip,response, variableBindings);
            }
            logger.info("ip is "+ip+" snmp list oid:"+Arrays.toString(oids.toArray())+" finished!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
        return variableBindings;
    }

    /**
     * @Author zhouxu
     * @Description 根据OID列表，采用异步方式一次获取多条OID数据，并且以List形式返回
     * @Date 2018/11/15 18:08
     * @Param [ip, community, oids,port,version]
     * @return void
     * @throws
     **/
    public static List<VariableBinding> getAsynList(String ip, String community,List<String> oids,int port,int version)
    {
        List<VariableBinding> variableBindings =new ArrayList<VariableBinding>();
        CommunityTarget target = createCommunityTarget(ip, community,port,version);
        Snmp snmp = null;
        try {
            logger.info("ip is "+ip+" snmp asyn list oid:"+Arrays.toString(oids.toArray())+" start..........");
            PDU pdu = new PDU();

            for(String oid:oids)
            {
                pdu.add(new VariableBinding(new OID(oid)));
            }

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            PDU response = respEvent.getResponse();

            /*异步获取*/
            final CountDownLatch latch = new CountDownLatch(1);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    if (response == null) {
//                        throw new SnmpTimeOutException(ip,Arrays.toString(oids.toArray()));
                    } else if (response.getErrorStatus() != 0) {
                        throw new SnmpStatusException(ip,Arrays.toString(oids.toArray()),response.getErrorStatus(),response.getErrorStatusText());
                    } else {
                        assemblyResults(ip,response, variableBindings);
                        latch.countDown();
                    }
                }
            };

            pdu.setType(PDU.GET);
            snmp.send(pdu, target, null, listener);
            boolean wait = latch.await(30, TimeUnit.SECONDS);
            snmp.close();
            logger.info("ip is "+ip+" snmp asyn list oid:"+Arrays.toString(oids.toArray())+" finished!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
            }

        }
        return variableBindings;
    }

    /**
     * @Author zhouxu
     * @Description 对于多个variableBindings进行过滤注入
     * @Date 2018/11/20 11:59
     * @Param [response, variableBindings]
     * @return void
     * @throws
     **/
    private static void assemblyResults(String ip,PDU response, List<VariableBinding> variableBindings) {
        for (int i = 0; i < response.size(); i++) {
            VariableBinding vb = response.get(i);
            logger.info(vb.getOid() + " = " + vb.getVariable());
            if(vb.getVariable().toString().startsWith(NOSUCHOIDPREFIX)){
                try{
                    throw new SnmpNoOidException(ip,vb.getOid().toString());
                }catch (SnmpNoOidException e){
//                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
                continue;
            }
            variableBindings.add(vb);
        }
    }

    /**
     * @Author zhouxu
     * @Description 根据targetOID，获取树形数据
     * @Date 2018/11/15 18:09
     * @Param [ip, community, targetOid,port,version]
     * @return void
     * @throws
     **/
    public static List<VariableBinding> walk(String ip, String community, String targetOid,int port,int version)
    {
        List<VariableBinding> variableBindings =new ArrayList<VariableBinding>();
        CommunityTarget target = createCommunityTarget(ip, community,port,version);
        TransportMapping transport = null;
        Snmp snmp = null;
        try {
            logger.info("ip is "+ip+" snmp walk oid:"+targetOid+" start..........");
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();
            PDU pdu = new PDU();
            OID targetOID = new OID(targetOid);
            pdu.add(new VariableBinding(targetOID));
            boolean finished = false;
            while (!finished) {
                VariableBinding vb = null;
                ResponseEvent respEvent = snmp.getNext(pdu, target);

                PDU response = respEvent.getResponse();

                if (null == response) {
                    String errorOid=targetOid;
                    if(vb!=null){
                        errorOid = vb.getOid().toString();
                    }
//                    throw new SnmpTimeOutException(ip,errorOid);
                } else {
                    vb = response.get(0);
                }
                // check finish
                finished = checkWalkFinished(targetOID, pdu, vb);
                if (!finished) {
                    logger.info(vb.getOid() + " = " + vb.getVariable());
                    if(vb.getVariable().toString().startsWith(NOSUCHOIDPREFIX)){
                        throw new SnmpNoOidException(ip,vb.getOid().toString());
                    }
                    variableBindings.add(vb);

                    // Set up the variable binding for the next entry.
                    pdu.setRequestID(new Integer32(0));
                    pdu.set(0, vb);
                } else {
                    logger.info("ip is "+ip+" snmp walk oid:"+targetOid+" finished");
                    snmp.close();
                }
            }
        }catch (SnmpNoOidException snmpNoOidException){
            logger.error(snmpNoOidException.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    snmp = null;
                }
            }
        }
        return variableBindings;
    }

    /**
     * @Author zhouxu
     * @Description //TODO
     * @Date 2018/11/15 18:09
     * @Param [targetOID, pdu, vb]
     * @return boolean
     * @throws
     **/
    private static boolean checkWalkFinished(OID targetOID, PDU pdu,
                                             VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            logger.info("[true] responsePDU.getErrorStatus() != 0 ");
            logger.info(pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            logger.info("[true] vb.getOid() == null");
            finished = true;
        } else if (vb.getOid().size() < targetOID.size()) {
            logger.info("[true] vb.getOid().size() < targetOID.size()");
            finished = true;
        } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
            logger.info("[true] targetOID.leftMostCompare() != 0");
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            System.out
                    .println("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            finished = true;
        } else if (vb.getOid().compareTo(targetOID) <= 0) {
            logger.info("[true] Variable received is not "
                    + "lexicographic successor of requested " + "one:");
            logger.info(vb.toString() + " <= " + targetOID);
            finished = true;
        }
        return finished;

    }

    /**
     * @Author zhouxu
     * @Description 根据targetOID，异步获取树形数据
     * @Date 2018/11/15 18:09
     * @Param [ip, community, oid]
     * @return void
     * @throws
     **/
//    public static List<VariableBinding> walkAsyn(String ip, String community, String oid)
//    {
//        List<VariableBinding> variableBindings =new ArrayList<VariableBinding>();
//        final CommunityTarget target = createDefault(ip, community);
//        Snmp snmp = null;
//        try {
//            logger.info("----> demo start <----");
//
//            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
//            snmp = new Snmp(transport);
//            snmp.listen();
//
//            final PDU pdu = new PDU();
//            final OID targetOID = new OID(oid);
//            final CountDownLatch latch = new CountDownLatch(1);
//            pdu.add(new VariableBinding(targetOID));
//
//            ResponseListener listener = new ResponseListener() {
//                public void onResponse(ResponseEvent event) {
//                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
//
//                    try {
//                        PDU response = event.getResponse();
//                        // PDU request = event.getRequest();
//                        // logger.info("[request]:" + request);
//                        if (response == null) {
//                            logger.info("[ERROR]: response is null");
//                        } else if (response.getErrorStatus() != 0) {
//                            logger.info("[ERROR]: response status"
//                                    + response.getErrorStatus() + " Text:"
//                                    + response.getErrorStatusText());
//                        } else {
//                            System.out
//                                    .println("Received Walk response value :");
//                            VariableBinding vb = response.get(0);
//
//                            boolean finished = checkWalkFinished(targetOID,
//                                    pdu, vb);
//                            if (!finished) {
//                                logger.info(vb.getOid() + " = "
//                                        + vb.getVariable());
//                                pdu.setRequestID(new Integer32(0));
//                                pdu.set(0, vb);
//                                ((Snmp) event.getSource()).getNext(pdu, target,
//                                        null, this);
//                            } else {
//                                System.out
//                                        .println("SNMP Asyn walk OID value success !");
//                                latch.countDown();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        latch.countDown();
//                    }
//
//                }
//            };
//
//            snmp.getNext(pdu, target, null, listener);
//            logger.info("pdu 已发送,等到异步处理结果...");
//
//            boolean wait = latch.await(30, TimeUnit.SECONDS);
//            logger.info("latch.await =:" + wait);
//            snmp.close();
//
//            logger.info("----> demo end <----");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return variableBindings;
//    }

    /**
     * @Author zhouxu
     * @Description 根据OID和指定string来设置设备的数据
     * @Date 2018/11/15 18:10
     * @Param [ip, community, oid, val]
     * @return void
     * @throws
     **/
    public static void setPDU(String ip,String community,String oid,String val) throws IOException
    {
        CommunityTarget target = createDefault(ip, community);
        Snmp snmp = null;
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid),new OctetString(val)));
        pdu.setType(PDU.SET);

        DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        snmp.listen();
        snmp.send(pdu, target);
        snmp.close();
    }
}
