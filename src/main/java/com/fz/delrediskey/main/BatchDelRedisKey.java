package com.fz.delrediskey.main;

import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class BatchDelRedisKey {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("该jar包需要四个参数!!");
            System.out.println("请按照如下格式输入redis参数，并用空格隔开：");
            System.out.println("地址 端口 密码 需要删除的key");
            return;
        }
        long t0 = System.currentTimeMillis();
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String password = args[2];
        String pattern = args[3];
        System.out.println("host: " + host);
        System.out.println("port: " + port);
        System.out.println("password: " + password);
        System.out.println("pattern: " + pattern);
        Jedis jedis = null;
        try {
            jedis = new Jedis(host, port);
            jedis.auth(password);
        }catch (Exception e) {
            System.out.println("连接redis失败，请检查传参是否正确");
            return;
        }
        String batchDel = null;
        try {
            batchDel = readLua("bathDel.lua");
        } catch (IOException e) {
            System.out.println("获取脚本文件失败");
            return;
        }
        Object eval = jedis.eval(batchDel, Arrays.asList(pattern), Arrays.asList(""));
        long t1 = System.currentTimeMillis();
        System.out.println("清理 ”" + pattern + " “" + eval.toString() + "个，耗时: " + ((t1-t0)/1000 + 1) + "秒");
    }

    private static String readLua(String luaPath) throws IOException {
        InputStream path = new ClassPathResource(luaPath).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(path));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = reader.readLine()) != null){
            sb.append(line + "\n");
        }
        return sb.toString();
    }
}
