package com.dsj.aicode.utils;

import cn.hutool.core.lang.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.net.InetAddress;
import java.util.UUID;

/**
 * 雪花算法工具类
 */
@Slf4j
public class SnowFlakeUtil {
    private static final long START_STMP = 1420041600000L;
    private static final long SEQUENCE_BIT = 9L;
    private static final long MACHINE_BIT = 2L;
    private static final long DATACENTER_BIT = 2L;
    private static final long MAX_SEQUENCE = 511L;
    private static final long MAX_MACHINE_NUM = 3L;
    private static final long MAX_DATACENTER_NUM = 3L;
    private static final long MACHINE_LEFT = 9L;
    private static final long DATACENTER_LEFT = 11L;
    private static final long TIMESTMP_LEFT = 13L;
    private long datacenterId;
    private long machineId;
    private long sequence = 0L;
    private long lastStmp = -1L;

    public SnowFlakeUtil(long datacenterId, long machineId) {
        if (datacenterId <= 3L && datacenterId >= 0L) {
            if (machineId <= 3L && machineId >= 0L) {
                this.datacenterId = datacenterId;
                this.machineId = machineId;
            } else {
                throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
            }
        } else {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
    }

    public synchronized long nextId() {
        long currStmp = this.getNewstmp();
        if (currStmp < this.lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        } else {
            if (currStmp == this.lastStmp) {
                this.sequence = this.sequence + 1L & 511L;
                if (this.sequence == 0L) {
                    currStmp = this.getNextMill();
                }
            } else {
                this.sequence = 0L;
            }

            this.lastStmp = currStmp;
            return currStmp - 1420041600000L << 13 | this.datacenterId << 11 | this.machineId << 9 | this.sequence;
        }
    }

    private long getNextMill() {
        long mill;
        for (mill = this.getNewstmp(); mill <= this.lastStmp; mill = this.getNewstmp()) {
        }

        return mill;
    }

    private long getNewstmp() {
        return System.nanoTime();
    }

    /**
     * 正常生成唯一的雪花算法ID(16位的long类型，使用此静态方法即可)
     *
     * @return
     */
    public static Long getDefaultSnowFlakeId() {
        long id = Singleton.get(SnowFlakeUtil.class, 1L, getLocalIpAsLong()).nextId();
        return ensure18Digits(id);
    }

    public static String getLongerId(String prefix) {
        long id = Singleton.get(SnowFlakeUtil.class, 1L, getLocalIpAsLong()).nextId();
        return getUUIDByPrefixName(prefix, 6) + "_" + id;
    }

    public static String clientToken() {
        long id = Singleton.get(SnowFlakeUtil.class, 1L, getLocalIpAsLong()).nextId();
        return getUUIDByPrefixName("clientToken_", 6) + "_" + id;
    }

    /**
     * 获取本机IP地址并转换为数字
     */
    public static long getLocalIpAsLong() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            long ipNum = ipToLong(ip);
            return ipNum % 3 + 1;
        } catch (Exception e) {
            log.error("无法获取本机IP地址", e);
        }
        return 1L;
    }

    /**
     * 将IPv4地址字符串转换为32位无符号整数
     *
     * @param ipAddress IPv4地址字符串（如"192.168.1.1"）
     * @return 对应的32位无符号整数（使用long存储）
     */
    public static long ipToLong(String ipAddress) {
        // 验证IP地址格式
        if (!ipAddress.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
            throw new IllegalArgumentException("无效的IP地址格式: " + ipAddress);
        }

        String[] octets = ipAddress.split("\\.");
        long result = 0;

        for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(octets[i]);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException("IP地址段超出范围: " + octet);
            }
            result |= (long) octet << (24 - (8 * i));
        }

        return result & 0xFFFFFFFFL; // 确保结果为无符号32位整数
    }

    private static long ensure18Digits(long id) {
        // 64位ID的范围是0到18,446,744,073,709,551,615
        // 我们需要确保结果在100,000,000,000,000,000到999,999,999,999,999,999之间
        long min18Digit = 100_000_000_000_000_0L;
        long max18Digit = 999_999_999_999_999_9L;

        // 通过模运算确保18位
        return min18Digit + (Math.abs(id) % (max18Digit - min18Digit));
    }

    public static String getUUIDByPrefixName(String prefix, Integer length) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
        return prefix + uuid;
    }

    public static void main(String[] args) {
        for(int i = 0; i < 10; ++i) {
            System.out.println(UUID.randomUUID());
            System.out.println(UUID.randomUUID().toString().length());
        }

    }

    public static String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "dsj";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}
