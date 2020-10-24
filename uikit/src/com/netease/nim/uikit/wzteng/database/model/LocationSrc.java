package com.netease.nim.uikit.wzteng.database.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by WZTENG on 2018/1/23 0023.
 */

@Entity
public class LocationSrc {
    @Id(autoincrement = true)
    private Long srcID;
    @Property
    private String addrTxt;
    @Property
    private String imagePath;
    @Generated(hash = 471446990)
    public LocationSrc(Long srcID, String addrTxt, String imagePath) {
        this.srcID = srcID;
        this.addrTxt = addrTxt;
        this.imagePath = imagePath;
    }
    @Generated(hash = 1636822473)
    public LocationSrc() {
    }
    public Long getSrcID() {
        return this.srcID;
    }
    public void setSrcID(Long srcID) {
        this.srcID = srcID;
    }
    public String getAddrTxt() {
        return this.addrTxt;
    }
    public void setAddrTxt(String addrTxt) {
        this.addrTxt = addrTxt;
    }
    public String getImagePath() {
        return this.imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
