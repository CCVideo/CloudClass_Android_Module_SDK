[TOC]
该文档主要说明 DocWebView 的主要 API
该文档主要说明 DocWebView 类外部常用接口，内部使用接口，此处不做说明介绍；

# DocWebView


## 一、概览
### API 汇总

|  | 返回值|          函数名                                                    |
| -------------- | -------------: | :----------------------------------------------------------- |
| 1 | void | [setDocBackGroundColor(String defaultBackgroundColor)](# 1.setDocBackGroundColor) |
| 2 | 	void | [setOnDpCompleteListener(OnDpCompleteListener mOnDpCompleteListening)](# 2.setOnDpCompleteListener) |
| 3 | void | [setCanScroll(boolean canScroll)](# 3.setCanScroll) |
| 4 | void | [needEvent(boolean needEvent)](# 4.needEvent) |


## 二、接口介绍

### 1.setDocBackGroundColor

```java
public void setDocBackGroundColor(String defaultBackgroundColor)
```

 1.作用
 	设置文档默认的背景颜色,如"#FFFFFF",默认#FFFFFF；

 2.注意
	无

 3.参数

| 参数名                 | 类型   | 说明                             |
| ---------------------- | ------ | -------------------------------- |
| defaultBackgroundColor | String | 默认背景颜色，默认为白色 #FFFFFF |

 4.返回
	无

 5.异常
	无

### 2.setOnDpCompleteListener

```java
public void setOnDpCompleteListener(OnDpCompleteListener mOnDpCompleteListening)

```

 1.作用
 	设置文档加载相关回调；

 2.注意

​	无

 3.参数

| 参数名                 | 类型                 | 说明 |
| ---------------------- | -------------------- | ---- |
| mOnDpCompleteListening | OnDpCompleteListener |      |

```java
public interface OnDpCompleteListener {

    /**
     * 加载完成
     * @param w 宽度
     * @param h 高度
     */
    void dpLoadComplete(int w, int h);

    /**
     * 正在加载
     */
    void dpLoading();

    /**
     * 加载图片动画翻页完成
     * @param index 页码
     */
    void dpAnimationChange(int index);
    /**
     * 加载出错
     */
    void dpLoadError();
}
```

 4.返回
	无

 5.异常
 	无

### 3.setCanScroll

```java
public void setCanScroll(boolean canScroll)
```

 1.作用
 	设置文档是否能否滚动

 2.注意
	无

 3.参数

| 参数名    | 类型    | 说明                             |
| --------- | ------- | -------------------------------- |
| canScroll | boolean | 默认背景颜色，默认为白色 #FFFFFF |

 4.返回
	无

 5.异常
	无

### 4.needEvent

```java
public void needEvent(boolean needEvent)
```

 1.作用
 	是否能够接收 touch 事件，针对宽图做的特殊处理，适用于宽度适配长图处理，如果仅采用窗口适配模式可以忽略

 2.注意
	无

 3.参数

| 参数名    | 类型    | 说明 |
| --------- | ------- | ---- |
| canScroll | boolean |      |

 4.返回
	无

 5.异常
	无







