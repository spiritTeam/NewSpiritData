package com.spiritdata.commons.logvisit.mem;

import java.util.concurrent.LinkedBlockingQueue;

import com.spiritdata.commons.logvisit.persis.po.LogVisitPo;

import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;

/**
 * 访问日志数据收集内存结构
 * @author wh
 */
public class LogVisitMemory {
    //java的占位单例模式===begin
    private static class InstanceHolder {
        public static LogVisitMemory instance=new LogVisitMemory();
    }
    public static LogVisitMemory getInstance() {
        InstanceHolder.instance.init();
        return InstanceHolder.instance;
    }

    //信息收集队列：访问日志数据收集后先放入本队列，之后再由一个线程把他写入持久化中，目前是数据库
    protected BlockingQueue<LogVisitPo> logDataQueue=null;

    /**
     * 参数初始化，必须首先执行这个方法
     */
    public void init() {
        if (logDataQueue==null) logDataQueue=new LinkedBlockingQueue<LogVisitPo>();
    }

    /**
     * 从队列中获得需要处理的数据
     * @throws InterruptedException 
     */
    public LogVisitPo takeQueue() throws InterruptedException {
        return this.logDataQueue.take();
    }

    /**
     * 存储收集到的数据信息到队列
     * @param vlp Api数据收集
     * @throws InterruptedException 
     */
    public void put2Queue(LogVisitPo alPo) throws InterruptedException {
        if (alPo.getBeginTime()==null) alPo.setBeginTime(new Timestamp(System.currentTimeMillis()));
        this.logDataQueue.put(alPo);
    }
}