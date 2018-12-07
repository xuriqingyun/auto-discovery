package cn.pioneeer.dcim.saas.autodiscovery.constants;

/**
 * Created with IntelliJ IDEA.
 * Description:设备定义
 * User: zhouxu
 * Date: 2018-11-15 17:35
 */
public class DeviceConst {

    public enum DeviceType{
        ROUTER("router","路由器"),
        SWITCH("switch","交换机"),
        ROUTERSWITCH("routerswitch","路由器交换机"),
        FIREWALL("firewall","防火墙"),
        PRINTER("printer","打印机"),
        HOST("host","主机"),
        OS("os","操作系统")
        ;

        private String name;

        private String znName;

        DeviceType(String name, String znName) {
            this.name = name;
            this.znName = znName;
        }

        public String getName() {
            return name;
        }

        public String getZnName() {
            return znName;
        }
    }
}
