package com.spiritdata.plugins.sms.ali;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spiritdata.framework.ext.spring.SpringGetBean;

public class AliSmsListener extends Thread {
    private Logger logger=LoggerFactory.getLogger(this.getClass());

    /**
     * 启动“访问日志”数据收集监听线程
     */
    public static void begin() {
        AliSmsListener sms=new AliSmsListener();
        sms.setName("“阿里云短信”发送监听");
        sms.start();
    }

    /**
     * 启动任务服务的处理主进程
     */
    @Override
    public void run() {
        AliSmsMemory asm=AliSmsMemory.getInstance();
        AliSmsConfig asConfig=null;
        int i=0;
        while (asConfig==null&&i++<6) {
            try {
                asConfig=SpringGetBean.getBean("smsConfig");
            } catch(Exception e) { }
            try { sleep(500); } catch(Exception e) { }
        }
        AliSMS aliSms=new AliSMS(asConfig);

        while (true) {
            try {
                AliSmsMessage smsMsg=asm.takeQueue();
                if (smsMsg!=null) {
                    String retJson=aliSms.sendSms(smsMsg);
                    logger.info("发送短信给["+smsMsg.getPhoneNum()+"]:"+retJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}