package cn.pioneeer.dcim.saas.autodiscovery.exception;

/**
 * Created with IntelliJ IDEA.
 * Description:用于描述SNMP状态异常
 * User: zhouxu
 * Date: 2018-11-20 11:50
 */
public class SnmpStatusException extends RuntimeException {
    public SnmpStatusException(String ip,String oid,int status,String text){
        super("ip is ["+ip+"] oid is ["+oid+"] status:"+status+" text:"+ text);
    }
}
