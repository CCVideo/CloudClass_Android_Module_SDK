

[TOC]

# 《组件化SDK变更说明》
# version_3.5.0
> 版本 3.5.0   时间：2018-3-25

### 一、新增功能
1、添加黑流检测状态

### 二、修改API
无

### 三、新增API

1、设置黑流检测回调的监听api接口

```
  public void setOnStreamStatsListener(OnStreamStatsListener onStreamStatsListener) {
```

2、使用的案例

获取CCPublicStream.isFirstBlackStream 如果为true这个流是黑流的，这时候可以根据自己需要设置界面显示

```
/**
     * 黑流检测监听事件
     */
    public interface OnStreamStatsListener {
        /**
         * 流对象
         */
        void OnStreamStats(CCPublicStream ccPublicStream);
    }
```



