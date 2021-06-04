package com.example.demo.socketTest;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tcpsclinet {
    public static void main(String[] args) throws Exception {
        // 建立tcp服务
        Socket socket = new Socket("10.10.88.245", 8888);

        // 获取socket输出流对象
        OutputStream outputStream = socket.getOutputStream();

        // 写数据
//        String data = "{\"username\":\"13934596183\",\"type\":\"10\",\"password\":\"13752172651\"}";
//        String data = "{\"username\":\"11111\",\"type\":\"00\",\"password\":\"13752172651\"}";
//        String data = "{\"date\":\"20210502\",\"type\":\"11\"}";
          String data = "{\"date\":\"2021512\",\"time\":\"14\",\"type\":\"11\"}";
        outputStream.write(data.getBytes());



        // 与服务器端交互
        InputStream inputStream = socket.getInputStream();
        byte[] bt = new byte[1024];
//                获取接收到的字节和字节数
        int length = inputStream.read(bt);
//                获取正确的字节
        byte[] bs = new byte[length];
        System.arraycopy(bt, 0, bs, 0, length);
        String str = new String(bs, "UTF-8");
        System.out.println(str);
        String[] split_result = str.split("x");
        for(String xxx:split_result)
        {
            System.out.println(xxx);
            Map<String,Object> ReceiveData = JSON.parseObject(xxx);
            System.out.println(ReceiveData.get("temperature"));
        }
//        Map<String,Object> ReceiveData = JSON.parseObject(split_result[0]);
//        System.out.println(ReceiveData.get("temperature"));
//        List<String> sss = (List<String>) ReceiveData.get("data");
//        StringBuilder xxx = sss.delete(0,1).delete(sss.length()-1,sss.length());
//        String[] yyy = xxx.toString().split("Record");
//        System.out.println(sss.get(0));
        // 关闭资源
        socket.close();

    }
}
