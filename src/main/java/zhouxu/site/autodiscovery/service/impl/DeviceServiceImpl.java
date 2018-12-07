package cn.pioneeer.dcim.saas.autodiscovery.service.impl;

import cn.pioneeer.dcim.saas.autodiscovery.constants.DeviceConst;
import cn.pioneeer.dcim.saas.autodiscovery.constants.RestConst;
import cn.pioneeer.dcim.saas.autodiscovery.dto.CommunityTargetDto;
import cn.pioneeer.dcim.saas.autodiscovery.dto.LiveSnmpDeviceDto;
import cn.pioneeer.dcim.saas.autodiscovery.exception.BizException;
import cn.pioneeer.dcim.saas.autodiscovery.pojo.Device;
import cn.pioneeer.dcim.saas.autodiscovery.service.DeviceService;
import cn.pioneeer.dcim.saas.autodiscovery.utils.ManufactuerConfigUtils;

import cn.pioneeer.dcim.saas.autodiscovery.utils.NetUtils;
import cn.pioneeer.dcim.saas.autodiscovery.utils.SnmpExtentionUtils;
import cn.pioneeer.dcim.saas.autodiscovery.utils.SnmpUtils;
import org.snmp4j.smi.VariableBinding;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: zhouxu
 * Date: 2018-11-20 17:41
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    //获取厂商所用OID
    public static final String MANUFACTURER_OID = ".1.3.6.1.2.1.1.2.0";

    //获取系统名称所有OID
    public static final String SYSNAME_OID = ".1.3.6.1.2.1.1.5.0";

    @Override
    public Device dicovery(String companyId, String ip, String community, String version, int port) {
        //ping检测
        boolean ping = NetUtils.ping(ip);
        if (!ping) {
            return null;
        }
        //判断snmp服务是否正常
        if (!SnmpUtils.snmpStatus(ip, community, port, SnmpUtils.parseVersion(version))) {
            return null;
        }
        return createDevice(companyId, ip, community, version, port);
    }

    @Override
    public Device dicoveryWithoutPing(String companyId, String ip, String community, String version, int port) {
        //判断snmp服务是否正常
        if (!SnmpUtils.snmpStatus(ip, community, port, SnmpUtils.parseVersion(version))) {
            return null;
        }
        return createDevice(companyId, ip, community, version, port);
    }


    /**
     * @return cn.pioneeerservice.autodiscovery.pojo.Device
     * @throws
     * @Author zhouxu
     * @Description 通过snmp获取device属性
     * @Date 2018/11/21 16:25
     * @Param [ip, community, version, port]
     **/
    Device createDevice(String companyId, String ip, String community, String version, int port) {
        Device device = new Device();
        device.setIp(ip);
        device.setCommunity(community);
        device.setVersion(version);
        device.setPort(port);
        //snmp获取系统名称
        String name = SnmpUtils.get(ip, community, SYSNAME_OID, port, SnmpUtils.parseVersion(version)).getVariable().toString();
        device.setHostName(name);
        device.setResName(ip+"-"+name);

        String resCode_postfix = name + "-" + ip + "-" + community + "-" + version + "-" + port;
        resCode_postfix = DigestUtils.md5DigestAsHex(resCode_postfix.getBytes());
        device.setResKey(companyId + "-" + resCode_postfix);
        //snmp获取厂商
        VariableBinding variableBinding = SnmpUtils.get(ip, community, MANUFACTURER_OID, port, SnmpUtils.parseVersion(version));
        int enterpriseNumber = getEnterpriseNumberByVariableBinding(variableBinding);
        device.setManufacturer(ManufactuerConfigUtils.getManufacturer(enterpriseNumber));
        //snmp获取类型

        if (SnmpExtentionUtils.isRouter(ip, community, port, SnmpUtils.parseVersion(version))) {
            device.setRealType(DeviceConst.DeviceType.ROUTER);
            device.setResCategoryCode(DeviceConst.DeviceType.ROUTER);
        } else if (SnmpExtentionUtils.isRouterSwitch(ip, community, port, SnmpUtils.parseVersion(version))) {
            device.setRealType(DeviceConst.DeviceType.ROUTERSWITCH);
            device.setResCategoryCode(DeviceConst.DeviceType.SWITCH);
        } else if (SnmpExtentionUtils.isSwitch(ip, community, port, SnmpUtils.parseVersion(version))) {
            device.setRealType(DeviceConst.DeviceType.SWITCH);
            device.setResCategoryCode(DeviceConst.DeviceType.SWITCH);
        } else if (SnmpExtentionUtils.isHost(ip, community, port, SnmpUtils.parseVersion(version))) {
            device.setRealType(DeviceConst.DeviceType.HOST);
            device.setResCategoryCode(DeviceConst.DeviceType.OS);
        } else if (SnmpExtentionUtils.isPrinter(ip, community, port, SnmpUtils.parseVersion(version))) {
            device.setRealType(DeviceConst.DeviceType.PRINTER);
            device.setResCategoryCode(DeviceConst.DeviceType.PRINTER);
        } else if (SnmpExtentionUtils.isFireWall(ip, community, port, SnmpUtils.parseVersion(version))) {
            device.setRealType(DeviceConst.DeviceType.FIREWALL);
            device.setResCategoryCode(DeviceConst.DeviceType.FIREWALL);
        }
        return device;
    }


    @Override
    public List<Device> dicovery(String companyId, String startIp, String endIp, String community, String version, int port) {
        List<Device> devices = new ArrayList<Device>();
        List<String> liveIps = NetUtils.liveIps(startIp, endIp);
        List<String> liveSnmpIps = NetUtils.liveSnmpIps(liveIps, community, port, SnmpUtils.parseVersion(version));
        for (String liveSnmpIp : liveSnmpIps) {
            Device device = createDevice(companyId, liveSnmpIp, community, version, port);
            if (device != null) {
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * @return java.util.List<cn.pioneeerservice.autodiscovery.dto.CommunityTargetDto>
     * @throws
     * @Author zhouxu
     * @Description //检测各组配置
     * @Date 2018/11/27 17:18
     * @Param [communityTargetDtos]
     **/
    List<CommunityTargetDto> checkCommunityTarget(List<CommunityTargetDto> communityTargetDtos) {
        if (communityTargetDtos == null) {
            throw new BizException(RestConst.CommonEnum.NOTNULL);
        }
        List<CommunityTargetDto> tmp = new ArrayList<CommunityTargetDto>();
        for (CommunityTargetDto communityTargetDto : communityTargetDtos) {
            if (communityTargetDto.getCommunity() == null || communityTargetDto.getVersion() == null ||
                    communityTargetDto.getCommunity().equals("") || communityTargetDto.getVersion().equals("") || communityTargetDto.getPort() == 0) {
                continue;
            }
            tmp.add(communityTargetDto);
        }
        if (tmp.size() == 0) {
            throw new BizException(RestConst.CommonEnum.ERROR_PARAMS);
        }
        return tmp;
    }

    @Override
    public List<Device> dicovery(String companyId, String startIp, String endIp, List<CommunityTargetDto> communityTargetDtos) {
        List<Device> devices = new ArrayList<Device>();
        //check
        communityTargetDtos = checkCommunityTarget(communityTargetDtos);
        List<String> liveIps = NetUtils.liveIps(startIp, endIp);
        List<LiveSnmpDeviceDto> liveSnmpDeviceDtos = NetUtils.liveSnmpDevices(liveIps, communityTargetDtos);
        for (LiveSnmpDeviceDto liveSnmpDeviceDto : liveSnmpDeviceDtos) {
            Device device = createDevice(companyId, liveSnmpDeviceDto.getIp(), liveSnmpDeviceDto.getCommunity(), liveSnmpDeviceDto.getVersion(), liveSnmpDeviceDto.getPort());
            if (device != null) {
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * @return int
     * @throws
     * @Author zhouxu
     * @Description //通过VariableBinding获取参商号
     * @Date 2018/11/21 11:11
     * @Param []
     **/
    int getEnterpriseNumberByVariableBinding(VariableBinding variableBinding) {
        if (variableBinding == null) {
            throw new BizException(RestConst.SnmpEnum.CANTGETMANUFACTUREROID);
        }
        if (SnmpUtils.isNoSuchOid(variableBinding)) {
            throw new BizException(RestConst.SnmpEnum.CANTGETMANUFACTUREROID);
        }
        String value = variableBinding.getVariable().toString();
        String[] split = value.split("\\.");
        return Integer.parseInt(split[6]);
    }

}
