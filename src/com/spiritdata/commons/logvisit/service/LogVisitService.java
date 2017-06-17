package com.spiritdata.commons.logvisit.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.commons.logvisit.persis.po.LogVisitPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;

/**
 * 访问日志管理服务类，提供如下服务：<br/>
 * <pre>
 * 1-与持久化数据交互的功能在这个服务中提供
 * 2-用户切换功能
 * 3-根据日志分类获得分类服务
 * </pre>
 * @author wh
 */
@Service
public class LogVisitService {
    @Resource(name="defaultDAO")
    private MybatisDAO<LogVisitPo> logDao; //api调用

    @PostConstruct
    public void initParam() {
        logDao.setNamespace("ApiLog");
    }

    /**
     * 保存访问日志信息到数据库
     * @param vlp 访问日志信息
     */
    public void Save2DB(LogVisitPo lvPo) {
        if (StringUtils.isNullOrEmptyOrSpace(lvPo.getId())) lvPo.setId(SequenceUUID.getPureUUID());
        logDao.insert(lvPo);
    }
}