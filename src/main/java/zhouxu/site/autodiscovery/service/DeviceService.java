package cn.pioneeer.dcim.saas.autodiscovery.service;



import cn.pioneeer.dcim.saas.autodiscovery.dto.CommunityTargetDto;
import cn.pioneeer.dcim.saas.autodiscovery.pojo.Device;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:设备服务接口
 * User: zhouxu
 * Date: 2018-11-20 17:22
 */
public interface DeviceService {

    /**
     * @Author zhouxu
     * @Description 发现单个设备默认采用v2模式
     * @Date 2018/11/20 17:23
     * @Param [ip, community,version,port]
     * @return cn.pioneeerservice.autodiscovery.pojo.Device
     * @throws
     **/
    Device dicovery(String companyId, String ip, String community, String version, int port);

    /**
     * @Author zhouxu
     * @Description 不通过ping发现设备
     * @Date 2018/11/21 11:36
     * @Param [ip, community, version, port]
     * @return cn.pioneeerservice.autodiscovery.pojo.Device
     * @throws
     **/
    Device dicoveryWithoutPing(String companyId,String ip,String community,String version,int port);

    /**
     * @Author zhouxu
     * @Description 发现多个设备默认采用v2模式
     * @Date 2018/11/20 17:24
     * @Param [startIp, endIp, community,version,port]
     * @return java.util.List<cn.pioneeerservice.autodiscovery.pojo.Device>
     * @throws
     **/
    List<Device> dicovery(String companyId,String startIp,String endIp,String community,String version,int port);
    /**
     * @Author zhouxu
     * @Description 发现多个设备默认采用v2模式
     * @Date 2018/11/20 17:24
     * @Param [startIp, endIp, community,version,port]
     * @return java.util.List<cn.pioneeerservice.autodiscovery.pojo.Device>
     * @throws
     **/
    List<Device> dicovery(String companyId,String startIp, String endIp, List<CommunityTargetDto> communityTargetDtos);
}
