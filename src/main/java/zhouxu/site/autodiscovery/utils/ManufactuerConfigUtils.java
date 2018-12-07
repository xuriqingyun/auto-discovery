package cn.pioneeer.dcim.saas.autodiscovery.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:用于解析厂商号与厂商对应关系
 * User: zhouxu
 * Date: 2018-11-21 10:31
 */
public class ManufactuerConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(ManufactuerConfigUtils.class);

    //配置文件位置
    public final static String CONFIG_PATH="config/manufacturer.data";

    //映射关系对应表
    private static Map<Integer,String> manufacturerMap = new HashMap<Integer,String>();

    //静态初始化映射表
    static {
        load();
    }

    /**
     * @Author zhouxu
     * @Description 加载企业映射数据
     * @Date 2018/11/21 10:38
     * @Param []
     * @return void
     * @throws
     **/
    static void load(){
        logger.info("====================== start load manufacturer.data ==========================");
        long start = System.currentTimeMillis();
        BufferedReader bufferedReader=null;
        try {
            bufferedReader= new BufferedReader(new FileReader(new File(CONFIG_PATH)));
            String line = bufferedReader.readLine().trim();
            while (line!=null){
                if(isEnterpriseNumbers(line)){
                    Integer enterpriseNumber= Integer.parseInt(line);
                    String manufacturer = bufferedReader.readLine().trim();
//                    logger.info(enterpriseNumber+"="+manufacturer);
                    manufacturerMap.put(enterpriseNumber,manufacturer);
                }
                line = bufferedReader.readLine();
            }
            long used = (System.currentTimeMillis()-start)/1000;
            logger.info("======================finished load manufacturer.data  total cost: "+used+"s ==========================");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @Author zhouxu
     * @Description //通过厂商号获取厂商名称
     * @Date 2018/11/21 10:37
     * @Param [enterpriseNumbers]
     * @return java.lang.String
     * @throws
     **/
    public static String  getManufacturer(Integer enterpriseNumbers){
        if(manufacturerMap.containsKey(enterpriseNumbers)){
            return manufacturerMap.get(enterpriseNumbers).trim();
        }
        return "unkown";
    }

    /**
     * @Author zhouxu
     * @Description //检测是否为企业号
     * @Date 2018/11/21 10:47
     * @Param [line]
     * @return boolean
     * @throws
     **/
    static boolean isEnterpriseNumbers(String line){
        try {
            Integer.parseInt(line);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @Author zhouxu
     * @Description //获取已经注册厂商的数量
     * @Date 2018/11/21 10:56
     * @Param []
     * @return int
     * @throws
     **/
    public static int registerManufacturerCount(){
        return manufacturerMap.size();
    }


}
