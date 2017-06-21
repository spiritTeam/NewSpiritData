package com.spiritdata.commons.model;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 所有者对象，封装了ownerId和ownerType
 * <pre>
 * ============================================
 *     ownerType              ownerId
 * --------------------------------------------
 *   1xxxx   系统　
 *     10000 我们自己的系统　　cm/crawl/push等
 *     101xx 其他系统　　　　　wt_Organize表中的Id  
 *   2xxxx   用户
 *     20000 后台系统用户　　　plat_user中的用户Id
 * ============================================
 * </pre>
 * @author wh
 */
public class Owner extends BaseObject {
    private static final long serialVersionUID=-1970271589243412626L;

    private int ownerType;
    private String ownerId;
    private String ownerName;

    public Owner() {
        super();
    }

    public Owner(int ownerType, String ownerId, String ownerName) {
        super();
        this.ownerType=ownerType;
        this.ownerId=ownerId;
        this.ownerName=ownerName;
    }

    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType=ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId=ownerId;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName=ownerName;
    }

    /**
     * 得到所有者的Key
     * @return 所有者key
     */
    public String getKey() {
        return ownerId+"::"+ownerType;
    }

    /**
     * 得到标识字符串
     * @return 标识字符串
     */
    @Override
    public String toString() {
        return ownerId+"_"+ownerType;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        try {
            return obj.hashCode()==this.hashCode();
        } catch(Exception e) {}
        return false;
    }
}