package cn.pioneeer.dcim.saas.autodiscovery.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:ip段提取工具
 * User: zhouxu
 * Date: 2018-11-20 15:03
 */
public class IPRangeUtils {
    /**
     * @Author zhouxu
     * @Description //TODO
     * @Date 2018/11/20 15:22
     * @Param [startIp, endIp]
     * @return java.util.List<java.lang.String>
     * @throws
     **/
    public static List<String> rangeIps(String startIp,String endIp){
        List<String> ips = new ArrayList<String>();
        long ipStart = convertIpToLong(startIp);
        long ipEnd = convertIpToLong(endIp);
        if (ipStart > ipEnd)
        {
            long l = ipStart;
            ipStart = ipEnd;
            ipEnd = l;
        }
        for (long i = ipStart; i <= ipEnd; i++)
        {
            ips.add(convertLongToIp(i));
        }
        return ips;
    }

    /**
     * @Author zhouxu
     * @Description String ip->long
     * @Date 2018/11/20 15:21
     * @Param [ip]
     * @return long
     * @throws
     **/
    static long convertIpToLong(String ip)
    {
        Pattern p = Pattern.compile("((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)");
        Matcher m = p.matcher(ip);
        if ((ip == null) || !m.matches()) throw new RuntimeException("wrong ip");
        long result = 0;
        String[] ipStringArray = ip.split("\\.");
        int[] ipArray = new int[ipStringArray.length];
        for (int i = 0; i < 4; i++)
        {
            ipArray[i] = Integer.parseInt(ipStringArray[i]);
        }
        result = (long)( ipArray[0]*Math.pow(256,3)
                + ipArray[1]*Math.pow(256,2)
                + ipArray[2]*Math.pow(256,1)
                + ipArray[3]*Math.pow(256,0));
        return  result;
    }

    /**
     * @Author zhouxu
     * @Description //long->String
     * @Date 2018/11/20 15:21
     * @Param [ip]
     * @return java.lang.String
     * @throws
     **/
    static String convertLongToIp(long ip)
    {
        if (ip < 0) throw new IllegalArgumentException();
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }
}
