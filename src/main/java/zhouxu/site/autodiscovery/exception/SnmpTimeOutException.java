package cn.pioneeer.dcim.saas.autodiscovery.exception;

/**
 * Created with IntelliJ IDEA.
 * Description:用于描述SNMP超时异常
 * User: zhouxu
 * Date: 2018-11-20 11:12
 */
public class SnmpTimeOutException extends RuntimeException {

    public static final String CODE="response is null, request time out";

    public SnmpTimeOutException(String ip,String oid){
        super("ip is ["+ip+"] oid is ["+oid+"] "+CODE);
    }
}
