package cn.pioneeer.dcim.saas.autodiscovery.exception;

/**
 * Created with IntelliJ IDEA.
 * Description:用于描述未找到对应Oid
 * User: zhouxu
 * Date: 2018-11-20 11:10
 */
public class SnmpNoOidException extends RuntimeException {

    public static final String CODE="no such oid";

    public SnmpNoOidException(String ip,String oid){
        super("ip is ["+ip+"] oid is ["+oid+"] "+CODE);
    }
}
