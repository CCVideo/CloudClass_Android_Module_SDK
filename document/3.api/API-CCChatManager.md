[TOC]
该文档主要说明 CCChatManager 的主要 API
该文档主要说明 CCChatManager 类外部常用接口，内部使用接口，此处不做说明介绍；

# CCChatManager


## 一、概览
### API 汇总

|  | 返回值|          函数名                                                    |
| -------------- | -------------: | :----------------------------------------------------------- |
| 1 | CCChatManager | [getInstance()](# 1.getInstance) |
| 2 | 	void | [sendMsg(String msg)](# 2.sendMsg) |
| 3 | void | [sendPic(String url)](# 3.sendPic) |
| 4 | boolean | [isRoomGag()](# 4.isRoomGag) |
| 5 | boolean | [isGag()](# 5.isGag) |
| 6 | void | [setOnChatListener(OnChatListener chatListener)](# 6.setOnChatListener) |
| 7 | void | [setOnGagListener(OnGagListener onGagListener)](# 7.setOnGagListener) |
| 8 | void | [cancelChatListener()](# 8.cancelChatListener) |
| 9 | void | [gagAll(CCChatCallBack<Void> callBack)](# 9.gagAll) |
| 10 | void | [cancelGagAll(CCChatCallBack<Void> callBack)](# 10.cancelGagAll) |
| 11 | void | [changeRoomAudioState(final boolean isAllow, final CCChatCallBack<Void> callBack)](# 11.changeRoomAudioState) |
| 12 | void | [allKickDownMai(final CCChatCallBack<Void> callBack)](# 12.allKickDownMai) |
| 13 | void | [changeRoomStudentBitrate(int level, CCChatCallBack<Void> callBack)](#13.changeRoomStudentBitrate) |
| 14 | void | [changeRoomTeacherBitrate(int level, CCChatCallBack<Void> callBack)](# 14.changeRoomTeacherBitrate) |
| 15 | boolean | [setMediaMode(int mediaMode, CCChatCallBack<Void> callBack)](# 15.setMediaMode) |
| 16 | boolean | [setLianmaiMode(int lianmaiMode, CCChatCallBack<Void> callBack)](# 16.setLianmaiMode) |


## 二、接口介绍

### 1.getInstance

```java
public CCChatManager getInstance()
```

 1.作用
 	获取CCChatManager实例；

 2.注意
	无

 3.参数

 4.返回
	无

 5.异常
	无

### 2.sendMsg

```java
public void sendMsg(String msg)

```

 1.作用
 	发送文本消息；

 2.注意

​	无

 3.参数
| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| msg | String | 文本消息内容 |

 4.返回
	无

 5.异常
 	无
### 3.sendPic

```java
public void sendPic(String url)

```

 1.作用
 	发送图片消息；

 2.注意

​	无

 3.参数
| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| url | String | 消息内容 |

 4.返回
	无

 5.异常
 	无
 	### 4.isRoomGag

```java
public boolean isRoomGag()

```

 1.作用
 	当前房间是否禁言；

 2.注意

​	无

 3.参数

​	无

 4.返回
	true 禁言 false 未禁言

 5.异常
 	无
 	### 5.isGag

```java
public boolean isGag()

```

 1.作用
 	当前用户是否被禁言；

 2.注意

​	无

 3.参数

​	无

 4.返回
	true 禁言 false 未禁言

 5.异常
 	无
 	### 6.setOnChatListener

```java
public void setOnChatListener(OnChatListener chatListener)
      

```

 1.作用
 	设置聊天回调；

 2.注意

​	无

 3.参数
| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| chatListener | OnChatListener | 文本消息内容 |
```java
public interface OnChatListener extends ErrorListener {
        /**
         * 收到聊天通知
         *
         * @param from 聊天发起者 {@link CCUser}
         * @param msg  聊天内容 {@link ChatMsg}
         * @param self 是否是自己发送的
         */
        void onReceived(CCUser from, ChatMsg msg, boolean self);
    }
```
 4.返回
	无

 5.异常
 	无

 	### 7.setOnGagListener

```java
public void setOnGagListener(OnGagListener onGagListener)

```

 1.作用
 	学生被禁言回调；

 2.注意

​	无

 3.参数

| 参数名        | 类型          | 说明         |
| ------------- | ------------- | ------------ |
| onGagListener | OnGagListener | 文本消息内容 |

```java

    public interface OnGagListener {
        /**
         * 禁言单个学生通知
         *
         * @param userid      当前被老师操作的用户id
         * @param isAllowChat <ul><li>true解除禁言</li><li>false禁言</li></ul>
         */
        void onChatGagOne(String userid, boolean isAllowChat);

        /**
         * 禁言全体通知
         *
         * @param isAllowChat <ul><li>true老师解除全体禁言</li><li>false老师开启全体禁言</li></ul>
         */
        void onChatGagAll(boolean isAllowChat);
    }

```
 4.返回
	无

 5.异常
 	无

 	### 8.cancelChatListener

```java
public void cancelChatListener()

```

 1.作用
 	取消聊天接听器；

 2.注意

​	无

 3.参数

​	无

 4.返回
	无

 5.异常
 	无

 	### 9.gagAll

```java
public void gagAll(CCChatCallBack<Void> callBack)

```

 1.作用
 	全部禁言；

 2.注意

​	无

 3.参数

| 参数名   | 类型           | 说明         |
| -------- | -------------- | ------------ |
| callBack | CCChatCallBack | 文本消息内容 |

 4.返回
	无

 5.异常
 	无

 	### 10.cancelGagAll

```java
public void cancelGagAll(CCChatCallBack<Void> callBack)

```

 1.作用
 	取消全体禁言；

 2.注意

​	无

 3.参数

| 参数名   | 类型           | 说明     |
| -------- | -------------- | -------- |
| callBack | CCChatCallBack | 禁言回调 |

 4.返回
	无

 5.异常
 	无

 	### 11.changeRoomAudioState

```java
public void changeRoomAudioState(final boolean isAllow, final CCChatCallBack<Void> callBack)
```

 1.作用
 	改变房间麦克风状态；

 2.注意

​	无

 3.参数

| 参数名   | 类型           | 说明                                        |
| -------- | -------------- | ------------------------------------------- |
| isAllow  | boolean        | true 允许开启麦克风，false 不允许开启麦克风 |
| callBack | CCChatCallBack | 回调                                        |

 4.返回
	无

 5.异常
 	无

 	### 12.allKickDownMai

```java
public void allKickDownMai(final CCChatCallBack<Void> callBack) 

```

 1.作用
 	全体踢下麦；

 2.注意

​	无

 3.参数

| 参数名   | 类型           | 说明 |
| -------- | -------------- | ---- |
| callBack | CCChatCallBack | 回调 |

 4.返回
	无

 5.异常
 	无

 	### 13.changeRoomStudentBitrate

```java
public void changeRoomStudentBitrate(int level, CCChatCallBack<Void> callBack)
```

 1.作用
 	更新学生码率；

 2.注意

​	无

 3.参数

| 参数名   | 类型           | 说明     |
| -------- | -------------- | -------- |
| level    | String         | 码率等级 |
| callBack | CCChatCallBack | 回调     |

 4.返回
	无

 5.异常
 	无

 	### 14.changeRoomTeacherBitrate

```java
public void changeRoomTeacherBitrate(int level, CCChatCallBack<Void> callBack)

```

 1.作用
 	更新老师码率；

 2.注意

​	无

 3.参数

| 参数名   | 类型           | 说明     |
| -------- | -------------- | -------- |
| level    | String         | 码率等级 |
| callBack | CCChatCallBack | 回调     |

 4.返回
	无

 5.异常
 	无

 	### 15.setMediaMode

```java
 public boolean setMediaMode(int mediaMode, CCChatCallBack<Void> callBack)

```

 1.作用
 	设置连麦媒体模式；

 2.注意

​	无

 3.参数

| 参数名    | 类型           | 说明                                                         |
| --------- | -------------- | ------------------------------------------------------------ |
| mediaMode | int            | 媒体设置 仅音频  MEDIA_MODE_AUDIO(0); <br/>媒体设置 音视频  MEDIA_MODE_BOTH (1) |
| callBack  | CCChatCallBack | 回调                                                         |

 4.返回
	true 执行, false 拒绝-没有权限

 5.异常
 	无



 	### 16.setLianmaiMode

```java
public boolean setLianmaiMode(int lianmaiMode, CCChatCallBack<Void> callBack) 

```

 1.作用
 	设置连麦模式；

 2.注意

​	无

 3.参数

| 参数名      | 类型           | 说明                                                         |
| ----------- | -------------- | ------------------------------------------------------------ |
| lianmaiMode | int            | 连麦模式 自由连麦   LIANMAI_MODE_FREE = 0; <br/>连麦模式 点名连麦   LIANMAI_MODE_NAMED = 1; <br/>连麦模式 自动连麦   LIANMAI_MODE_AUTO = 3; |
| callBack    | CCChatCallBack | 回调                                                         |

 4.返回
	true 执行, false 拒绝-没有权限

 5.异常
 	无
















