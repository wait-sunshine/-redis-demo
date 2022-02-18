package com.it;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * @Author ZYH
 * @Date 2022/02/17 9:09
 */
public class PhoneCode {

    public static void main(String[] args) {
//        先生成验证码存到redis中
//        verifyCode("12345678");
//        然后手动验证  验证码是从redis中查看出来的
        getRedisCode("12345678","793889");
    }

    //验证码校验
    public static void getRedisCode(String phone, String code) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");
        String vcode = jedis.get("VerifyCode" + phone + ":code");
        if (vcode.equals(code)) {
            System.out.println("验证成功");
        } else {
            System.out.println("验证失败");
        }
    }

    //每个手机每天只能发送三次，把验证码存到redis中，设置过期时间为120秒
    public static void verifyCode(String phone) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");
        String countKey = "VerifyCode" + phone + ":count";
        String codeKey = "VerifyCode" + phone + ":code";
        String count = jedis.get(countKey);
        if (count == null) {
            jedis.setex(countKey, 24 * 60 * 60, "1");
        } else if (Integer.valueOf(count) < 3) {
            jedis.incr(count);
        } else if (Integer.valueOf(count) > 2) {
            System.out.println("今天发送次数超过三次了");
            jedis.close();
            return;
        }
        String vcode = getCode();
        jedis.setex(codeKey, 120, vcode);
        jedis.close();
    }

    //生成验证码
    public static String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int x = random.nextInt(10);
            code += x;
        }

        return code;
    }

}
