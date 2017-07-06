package com.spiritdata.plugins.sms.ali;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AliSmsMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static AliSmsMemory instance=new AliSmsMemory();
    }
    public static AliSmsMemory getInstance() {
        InstanceHolder.instance.init();
        return InstanceHolder.instance;
    }

    //短信发送队列：进入这个队列的短信消息将被发送出去
    protected BlockingQueue<AliSmsMessage> msgDataQueue=null;

    /**
     * 参数初始化，必须首先执行这个方法
     */
    public void init() {
        if (msgDataQueue==null) msgDataQueue=new LinkedBlockingQueue<AliSmsMessage>();
    }

    /**
     * 从队列中获得需要处理的短信数据
     * @throws InterruptedException 
     */
    public AliSmsMessage takeQueue() throws InterruptedException {
        return this.msgDataQueue.take();
    }

    /**
     * 存储短信数据到队列
     * @param asMsg 阿里短信消息
     * @throws InterruptedException 
     */
    public void put2Queue(AliSmsMessage asMsg) throws InterruptedException {
        this.msgDataQueue.put(asMsg);
    }
}