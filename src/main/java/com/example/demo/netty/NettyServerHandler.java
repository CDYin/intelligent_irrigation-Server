package com.example.demo.netty;

import com.alibaba.fastjson.JSON;
import com.example.demo.config.AutowiredUtils;
import com.example.demo.entity.Record;
import com.example.demo.entity.User;
import com.example.demo.service.RecordService;
import com.example.demo.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
// netty服务端处理类
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private UserService userService = AutowiredUtils.getBean(UserService.class);
    private RecordService recordService = AutowiredUtils.getBean(RecordService.class);

    //管理一个全局map，保存连接进服务端的通道数量
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext>
            CHANNEL_MAP = new ConcurrentHashMap<>();
    //有客户端连接服务器会触发此函数
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();

        String clientIp = insocket.getAddress().getHostAddress();
        int clientPort = insocket.getPort();

        //获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();

        System.out.println();
        //如果map中不包含此连接，就保存连接
        if (CHANNEL_MAP.containsKey(channelId)) {
            log.info("客户端【" + channelId + "】是连接状态，连接通道数量: " + CHANNEL_MAP.size());
        } else {
            //保存连接
            CHANNEL_MAP.put(channelId, ctx);

            log.info("客户端【" + channelId + "】连接netty服务器[IP:" + clientIp + "--->PORT:" + clientPort + "]");
            log.info("连接通道数量: " + CHANNEL_MAP.size());
        }
    }
    //有客户端终止连接服务器会触发此函数
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();

        ChannelId channelId = ctx.channel().id();

        //包含此客户端才去删除
        if (CHANNEL_MAP.containsKey(channelId)) {
            //删除连接
            CHANNEL_MAP.remove(channelId);

            System.out.println();
            log.info("客户端【" + channelId + "】退出netty服务器[IP:" + clientIp + "--->PORT:" + insocket.getPort() + "]");
            log.info("连接通道数量: " + CHANNEL_MAP.size());
        }
    }

    //有客户端发消息会触发此函数
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println();
        log.info("加载客户端报文......");
        log.info("【" + ctx.channel().id() + "】" + " :" + msg);
        /**
         *  下面可以解析数据，保存数据，生成返回报文，将需要返回报文写入write函数
         */
        Map<String,Object> ReceiveData = JSON.parseObject(String.valueOf(msg));
        System.out.println(ReceiveData);
        String type = (String) ReceiveData.get("type");
        System.out.println(type);
        switch (type){
            //树莓派向服务器传递数据存储到数据库
            case "01" :
                String temperature = (String) ReceiveData.get("temperature");
                String humidity = (String) ReceiveData.get("humidity");
                String currentDate = (String) ReceiveData.get("date");
                String currentTime = (String) ReceiveData.get("time");
                String remark = (String) ReceiveData.get("remark");
                Record record = new Record();
                record.setRecordData(temperature,humidity,currentDate,currentTime,remark);
                recordService.setRecordData(record);
                System.out.println("浇水数据保存成功");
                break;
            //用户从安卓端传递自己的注册信息存储到数据库
            case "10" :
                String username = (String) ReceiveData.get("username");
                String password = (String) ReceiveData.get("password");
                User register_UserResult = userService.getUserByName(username);
                if (register_UserResult!=null){
                    System.out.println("用户已存在，拒绝注册");
                    String sendMsg_saveUser = "{\"SaveResult\":\"refuse\",\"type\":\"10\"}x";
                    this.channelWrite(ctx.channel().id(),sendMsg_saveUser);
                }else {
                    System.out.println("type:10 开始存储用户信息");
                    User user = new User();
                    user.setUserData(username, password);
                    userService.setUserData(user);
                    System.out.println("用户数据保存成功");
                    String sendMsg_saveUser = "{\"SaveResult\":\"OK\",\"type\":\"10\"}x";
                    this.channelWrite(ctx.channel().id(), sendMsg_saveUser);
                }
                break;
            //用户从安卓端请求查询浇水记录，从数据库中取出数据
            case "11" :
                String searchTime = (String) ReceiveData.get("time");
                String searchDate = (String) ReceiveData.get("date");
                if (searchTime!=null){
                    Record airData = recordService.getAirData(searchDate,searchTime);
                    if (airData!=null) {
                        String getTemperature = airData.getTemperature();
                        String getHumidity = airData.getHumidity();
                        String sendMsg_findAirData = "{\"temperature\":\"" + getTemperature + "\"," +
                                "\"humidity\":\"" + getHumidity + "\"," +
                                "\"type\":\"11\"}x";
                        System.out.println(sendMsg_findAirData);
                        this.channelWrite(ctx.channel().id(), sendMsg_findAirData);
                    }else {
                        String sendMsg_findAirData = "{\"temperature\":\"null\"," +
                                "\"humidity\":\"null\"," +
                                "\"type\":\"11\"}x";
                        System.out.println(sendMsg_findAirData);
                        this.channelWrite(ctx.channel().id(), sendMsg_findAirData);
                    }
                }else {
                    List<Record> result = recordService.getWateringRecord(searchDate);
                    if (result.size()!=0) {
                        System.out.println(result);
                        String sendMsg_findRecordData = "";
                        for (Record save_record : result) {
                            String save_temperature = save_record.getTemperature();
                            String save_humidity = save_record.getHumidity();
                            String save_currentDate = save_record.getCurrentDate();
                            String save_currentTime = save_record.getCurrentTime();
                            String save_remark = save_record.getRemark();

                            sendMsg_findRecordData += "{\"temperature\":\"" + save_temperature + "\"," +
                                    "\"humidity\":\"" + save_humidity + "\"," +
                                    "\"currentDate\":\"" + save_currentDate + "\"," +
                                    "\"currentTime\":\"" + save_currentTime + "\"," +
                                    "\"remark\":\"" + save_remark + "\"," +
                                    "\"type\":\"11\"}x";
                        }
                        this.channelWrite(ctx.channel().id(), sendMsg_findRecordData);
                    }else {
                        String sendMsg_findRecordData = "{\"temperature\":\"null\"," +
                                "\"humidity\":\"null\"," +
                                "\"currentDate\":\"null\"," +
                                "\"currentTime\":\"null\"," +
                                "\"remark\":\"null\"," +
                                "\"type\":\"11\"}x";
                        this.channelWrite(ctx.channel().id(), sendMsg_findRecordData);
                    }
                }
                break;
            //用户登录时验证密码是否正确
            case "00" :
                System.out.println("type:00 开始验证用户信息");
                String searchUsername = (String) ReceiveData.get("username");
                System.out.println(searchUsername);
                String searchPassword = (String) ReceiveData.get("password");
                System.out.println(searchPassword);
                User login_UserResult = userService.getUserByName(searchUsername);
                System.out.println(login_UserResult);
                if (login_UserResult==null){
                    String sendMsg_findUser ="{\"CheckResult\":\"refuse\",\"type\":\"00\"}x";
                    this.channelWrite(ctx.channel().id(), sendMsg_findUser);
                    System.out.println("验证未通过，拒绝登录");
                }else {
                    String checkPassword = login_UserResult.getPassword();
                    System.out.println(checkPassword);
                    if (searchPassword.equals(checkPassword)){
                        System.out.println("验证通过，允许登录");
                        String sendMsg_findUser = "{\"CheckResult\":\"OK\",\"type\":\"00\"}x";
                        this.channelWrite(ctx.channel().id(), sendMsg_findUser);
                    }else {
                        String sendMsg_findUser ="{\"CheckResult\":\"refuse\",\"type\":\"00\"}x";
                        this.channelWrite(ctx.channel().id(), sendMsg_findUser);
                        System.out.println("验证未通过，拒绝登录");
                    }
                }
                break;
        }
    }

    private void channelWrite(ChannelId channelId, Object msg) {
        ChannelHandlerContext ctx = CHANNEL_MAP.get(channelId);

        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }

        if (msg == null || msg == "") {
            log.info("服务端响应空的消息");
            return;
        }

        //将客户端的信息直接返回写入ctx
        ctx.write(msg);
        //刷新缓存区
        ctx.flush();
    }

    //当客户端的所有ChannelHandler中4s内没有write事件，则会触发userEventTriggered方法
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }
    //发生异常会触发此函数
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println();
        ctx.close();
        log.info(ctx.channel().id() + " 发生了错误,此连接被关闭" + "此时连通数量: " + CHANNEL_MAP.size());
    }
}
