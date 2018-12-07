package cn.pioneeer.dcim.saas.autodiscovery.api;


import cn.pioneeer.dcim.saas.autodiscovery.utils.RestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:用于检测服务状态
 * User: zhouxu
 * Date: 2018-11-14 17:43
 */
@RestController
@RequestMapping("/api/v1/")
public class HealthController {
    @GetMapping("/health")
    public RestResult health(){
        return RestResult.Success("true");
    }
}
