package cn.pioneeer.dcim.saas.autodiscovery.pojo;


import cn.pioneeer.dcim.saas.autodiscovery.constants.DeviceConst;

/**
 * Created with IntelliJ IDEA.
 * Description:设备实体类
 * User: zhouxu
 * Date: 2018-11-15 17:27
 */
public class Device {

    //编码
    private String resKey;

    //展现名称
    private String resName;
    //主机snmp采集到名称
    private String hostName;
    //ip
    private String ip;

    //展现类型
    private DeviceConst.DeviceType resCategoryCode;
    //检测类型
    private DeviceConst.DeviceType realType;

    //厂商
    private String manufacturer;
    //snmp 采集使用的团体名
    private String community;
    //snmp 所使用的版本
    private String version;
    //snmp 所使用的端口
    private int port;

    public Device(String resKey, String resName, String ip, DeviceConst.DeviceType realType, String manufacturer) {
        this.resKey = resKey;
        this.resName = resName;
        this.ip = ip;
        this.realType = realType;
        this.manufacturer = manufacturer;
    }

    public Device(String resKey, String resName, String ip, DeviceConst.DeviceType realType, String manufacturer, String community, String version, int port) {
        this.resKey = resKey;
        this.resName = resName;
        this.ip = ip;
        this.realType = realType;
        this.manufacturer = manufacturer;
        this.community = community;
        this.version = version;
        this.port = port;
    }

    public Device(String resKey, String resName, String hostName, String ip, DeviceConst.DeviceType realType, String manufacturer, String community, String version, int port) {
        this.resKey = resKey;
        this.resName = resName;
        this.hostName = hostName;
        this.ip = ip;
        this.realType = realType;
        this.manufacturer = manufacturer;
        this.community = community;
        this.version = version;
        this.port = port;
    }

    public Device(){}

    public String getResKey() {
        return resKey;
    }

    public void setResKey(String resKey) {
        this.resKey = resKey;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public DeviceConst.DeviceType getRealType() {
        return realType;
    }

    public void setRealType(DeviceConst.DeviceType realType) {
        this.realType = realType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DeviceConst.DeviceType getResCategoryCode() {
        return resCategoryCode;
    }

    public void setResCategoryCode(DeviceConst.DeviceType resCategoryCode) {
        this.resCategoryCode = resCategoryCode;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return "Device{" +
                "resKey='" + resKey + '\'' +
                ", resName='" + resName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", ip='" + ip + '\'' +
                ", resCategoryCode=" + resCategoryCode +
                ", realType=" + realType +
                ", manufacturer='" + manufacturer + '\'' +
                ", community='" + community + '\'' +
                ", version='" + version + '\'' +
                ", port=" + port +
                '}';
    }
}
