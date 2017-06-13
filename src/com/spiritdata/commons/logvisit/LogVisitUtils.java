package com.spiritdata.commons.logvisit;

import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;

import com.spiritdata.commons.logvisit.persis.pojo.LogVisitPo;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;

public abstract class LogVisitUtils {
    public static LogVisitPo buildApiLogDataFromRequest(HttpServletRequest request) {
        LogVisitPo lvPo=new LogVisitPo();
        lvPo.setId(SequenceUUID.getPureUUID());
        lvPo.setMethod(request.getMethod());
        String tempStr=request.getQueryString();
        tempStr=request.getRequestURL()+(StringUtils.isNullOrEmptyOrSpace(tempStr)?"":"?"+tempStr);
        lvPo.setReqUrl(tempStr);
        lvPo.setBeginTime(new Timestamp(System.currentTimeMillis()));
        return lvPo;
    }
}