package cn.pioneeer.dcim.saas.autodiscovery.dto;

/**
 * Created with IntelliJ IDEA.
 * Description:存活设备dto对象，保存设备检测时所用参数
 * User: zhouxu
 * Date: 2018-11-27 9:37
 */
public class LiveSnmpDeviceDto {

    //ip地址
    private String ip;

    //输入参数对象
    private CommunityTargetDto communityTargetDto;

    public LiveSnmpDeviceDto(String ip, String community, String version, int port) {
        this.ip = ip;
        this.communityTargetDto = new CommunityTargetDto(community,version,port);
    }

    public LiveSnmpDeviceDto(){}

    public LiveSnmpDeviceDto(String ip, CommunityTargetDto communityTargetDto) {
        this.ip = ip;
        this.communityTargetDto = communityTargetDto;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public CommunityTargetDto getCommunityTargetDto() {
        return communityTargetDto;
    }

    public void setCommunityTargetDto(CommunityTargetDto communityTargetDto) {
        this.communityTargetDto = communityTargetDto;
    }

    public String getCommunity() {
        if(this.communityTargetDto!=null){
            return this.communityTargetDto.getCommunity();
        }
        return "";
    }

    public String getVersion() {
        if(this.communityTargetDto!=null){
            return this.communityTargetDto.getVersion();
        }
        return "";
    }

    public int getPort() {
        if(this.communityTargetDto!=null){
            return this.communityTargetDto.getPort();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "LiveSnmpDeviceDto{" +
                "ip='" + ip + '\'' +
                ", communityTargetDto=" + communityTargetDto.toString() +
                '}';
    }
}
