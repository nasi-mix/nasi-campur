package me.qfdk.nasicampur.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Integer id;
    /* 微信id*/
    private String wechatName;
    /*开始时间*/
    private Date startTime;
    /*结束时间*/
    private Date endTime;
    /*用户是否激活*/
    private boolean isEnable;
    /*二维码*/
    private String qrCode;
    /*容器id*/
    private String containerId;
    /*容器状态*/
    private String containerStatus;
    /*容器端口*/
    private String containerPort;
    /*图标*/
    private String icon;
    /*显示名称*/
    private String nickname;
    /*网络发出量*/
    private double networkTx;
    /*网络接收量*/
    private double networkRx;

    private boolean enableSelfControl;

    private String containerLocation;

    private String pontLocation;

    public User() {

    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean enable) {
        isEnable = enable;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerStatus() {
        return containerStatus;
    }

    public void setContainerStatus(String containerStatus) {
        this.containerStatus = containerStatus;
    }

    public String getContainerPort() {
        return containerPort;
    }

    public void setContainerPort(String containerPort) {
        this.containerPort = containerPort;
    }

    public double getNetworkTx() {
        return networkTx;
    }

    public void setNetworkTx(double networkTx) {
        this.networkTx = networkTx;
    }

    public double getNetworkRx() {
        return networkRx;
    }

    public void setNetworkRx(double networkRx) {
        this.networkRx = networkRx;
    }

    public boolean isEnableSelfControl() {
        return enableSelfControl;
    }

    public void setEnableSelfControl(boolean enableSelfControl) {
        this.enableSelfControl = enableSelfControl;
    }

    public User(String wechatName, Date startTime, Date endTime, boolean isEnable, String qrCode, String containerId, String containerStatus, String containerPort, String icon, String nickname, double networkTx, double networkRx, boolean enableSelfControl) {
        this.wechatName = wechatName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isEnable = isEnable;
        this.qrCode = qrCode;
        this.containerId = containerId;
        this.containerStatus = containerStatus;
        this.containerPort = containerPort;
        this.icon = icon;
        this.nickname = nickname;
        this.networkTx = networkTx;
        this.networkRx = networkRx;
        this.enableSelfControl = enableSelfControl;
    }

    public String getContainerLocation() {
        return containerLocation;
    }

    public void setContainerLocation(String containerLocation) {
        this.containerLocation = containerLocation;
    }

    public String getPontLocation() {
        return pontLocation;
    }

    public void setPontLocation(String pontLocation) {
        this.pontLocation = pontLocation;
    }
}
