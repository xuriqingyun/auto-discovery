package cn.pioneeer.dcim.saas.autodiscovery.command;

import cn.pioneeer.dcim.saas.autodiscovery.utils.ManufactuerConfigUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:实现配置的预加载
 * User: zhouxu
 * Date: 2018-11-21 14:04
 */
@Component
public class ConfigCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        int count = ManufactuerConfigUtils.registerManufacturerCount();
    }
}
