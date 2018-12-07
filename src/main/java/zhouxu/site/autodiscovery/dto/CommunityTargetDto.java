package cn.pioneeer.dcim.saas.autodiscovery.dto;

/**
 * Created with IntelliJ IDEA.
 * Description:用于保存snmp检测参数
 * User: zhouxu
 * Date: 2018-11-27 9:41
 */
public class CommunityTargetDto {

    //snmp团体名
    private String community;
    //snmp版本号
    private String version;
    //snmp端口
    private int port;

    public CommunityTargetDto(){}

    public CommunityTargetDto(String community, String version, int port) {
        this.community = community;
        this.version = version;
        this.port = port;
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

    @Override
    public String toString() {
        return "CommunityTargetDto{" +
                "community='" + community + '\'' +
                ", version='" + version + '\'' +
                ", port=" + port +
                '}';
    }
}
