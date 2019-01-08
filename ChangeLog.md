[TOC]

# 《组件化SDK变更说明》
# version_3.3.0
> 版本 3.3.0   时间：2018-12-29

### 一、新增功能
1、上报本地的数据到服务端接口，需要在加入房间以后，或者是建议在离开房间的时候或者是结束直播的时候调用，这样上报的数据会比较全。

2、插播视频接口

### 二、修改API
1、获取节点切换接口，需要传的是账户id

```java
public void dispatch(String userid,final CCAtlasCallBack<CCCityBean> callBack){
```

### 三、新增API

1、上报数据的接口

```java
public void CCReportLogInfo() {
```

2、插播视频监听事件

业务需要：视频显示控件、音频隐藏控件，可根据此监听写自己的业务逻辑

```
public interface OnIsVisiableMadieListener {
        /**
         * @param isShow 是否显示插播音视频
         */
        void isShowMadie(boolean isShow);
    }
```

业务需要：获取真实视频的宽高，进行做视频，防止视频变形；音频的时候宽高都是0

```
public interface OnVideoWHListener {
        void setVideoWH(int w, int h);
    }
```

