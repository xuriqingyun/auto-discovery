package cn.pioneeer.dcim.saas.autodiscovery.api;

import cn.pioneeer.dcim.saas.autodiscovery.dto.CommunityTargetDto;
import cn.pioneeer.dcim.saas.autodiscovery.pojo.Device;
import cn.pioneeer.dcim.saas.autodiscovery.service.DeviceService;
import cn.pioneeer.dcim.saas.autodiscovery.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:用于
 * User: zhouxu
 * Date: 2018-11-27 11:11
 */
@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * @Author zhouxu
     * @Description //获取ip段中的设备
     * @Date 2018/11/27 17:15
     * @Param [companyId, startIp, endIp, version1, community1, port1, version2, community2, port2]
     * @return java.lang.Object
     * @throws
     * http://localhost:8086/getobjects?companyId=16&startIp=10.200.0.1&endIp=10.200.0.255&community1=PGJT@Read&port1=161&version1=v2
     **/
    @GetMapping("/getobjects")
    public Object getobjects(String companyId,String startIp,String endIp,String version1,String community1,@RequestParam(name="port1",required=false,defaultValue="0") Integer port1,
                             String version2,String community2,@RequestParam(name="port2",required=false,defaultValue="0") Integer port2 ){
        try{
            List<CommunityTargetDto> communityTargetDtos = Arrays.asList(
                    new CommunityTargetDto(community1,version1,port1),
                    new CommunityTargetDto(community2,version2,port2)
            );
            List<Device> devices = deviceService.dicovery(companyId,startIp,endIp,communityTargetDtos);
            Map map = new HashMap();
            //
            map.put("resources", devices);

            map.put("relations", new ArrayList<>());
            //
            Map mapResult = new HashMap();
            mapResult.put("code", 200);
            mapResult.put("result", map);
            return mapResult;
            //return "{\"code\":\"200\",\"result\":\"result\":{\"resouces\":"+GsonUtils.toString(devices)+",\"relations\":[]}}";
        }catch (Exception e){
            e.printStackTrace();
            return "{\"code\":\"500\",\"result\":\"错误信息\"}";
        }
    }

    @GetMapping("/demo")
    public Object demo(){
        String startIp="10.200.132.1";
        String endIp="10.200.132.255";
        List<CommunityTargetDto> communityTargetDtos = Arrays.asList(new CommunityTargetDto[]{
                new CommunityTargetDto("PGJT@Read","v2",161),
                new CommunityTargetDto("public","v2",161),
        });
        List<Device> devices = deviceService.dicovery("16",startIp, endIp, communityTargetDtos);
        return "{\"code\":\"200\",\"result\":\"result\":{\"resouces\":"+GsonUtils.toString(devices)+",\"relations\":[]}}";
    }

    @GetMapping(value = "/connect")
    public String connect(){
        return "{\"code\":200,\"message\":\"连接成功\"}";

    }
}
