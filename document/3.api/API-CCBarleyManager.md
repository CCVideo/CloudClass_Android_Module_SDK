[TOC]

场景视频通过全球部署的虚拟网络，提供可以灵活搭配的API组合；
该文档主要说明带界面框架 CCBarleyLibrary的接口，该文档主要说明CCBarleyManager类常用接口，
内部使用接口，此处不做说明介绍；

# CCBarleyManager


## 一、概览
### API 汇总

| 返回值|          函数名                                                    |
| -------------: | :----------------------------------------------------------- |
| CCBarleyManager | + getInstance(); |
| void | - (void) handsup(final CCBarLeyCallBack<Void> callBack);|
| void | - (void) cancleInviteUserSpeak(String userId, CCBarLeyCallBack<Void> callBack);|
| void | - (void) refuseTeacherInvite(CCBarLeyCallBack<Void> callBack);|
| void | - (void) handsUpCancel(final CCBarLeyCallBack<Void> callBack);|
| void | - (void) kickUserFromSpeak(String userId, CCBarLeyCallBack<Void> callBack);|
| void | - (void) handsDown(CCBarLeyCallBack<Void> callBack);|
| void | - (void) Studenthandup(boolean flag, final CCBarLeyCallBack<Void> callBack);|
| void | - (void) certainHandup(String userId, final CCBarLeyCallBack<Void> callBack);|
| void | - (void) inviteUserSpeak(String userId, final CCBarLeyCallBack<Void> callBack);|
| void | - (void) acceptTeacherInvite(final CCBarLeyCallBack<Void> callBack);|
| void | - (void) setSpeakMode(int lianmaiMode, CCBarLeyCallBack<Void> callBack);|
| boolean | - (boolean) kickUserFromRoom(String userId);|
| void | - (void ) setOnNotifyMaiStatusLisnter(OnNotifyMaiStatusLisnter onNotifyMaiStatusLisnter);|
| void | - (void) setOnNotifyInviteListener(OnNotifyInviteListener onNotifyInviteListener);|
| void | - (void) setOnQueueMaiUpdateListener(OnQueueMaiUpdateListener onQueueMaiUpdateListener);|
| void | - (void) setOnSpeakModeUpdateListener(OnSpeakModeUpdateListener onLianmaiModeUpdateListener);|
| void | - (void) setOnHandupListener(OnHandupListener onHandupListener);|
| void | - (void) setOnUserListUpdateListener(OnUserListUpdateListener onUserListUpdateListener);|
| void | - (void) setOnKickOutListener(OnKickOutListener onKickOutListener);|
| void | - (void) setOnCancelHandUpListener(OnCancelHandUpListener onCancelHandUpListener);|
| void | - (void) setOnCCStreamPlaceholder(CCStreamPlaceholder mCCStreamPlaceholder);|


## 二、接口介绍
  
### 1.getInstance

```java
/**
 * 初始化对象
 * @return
 */
public static CCBarLeyManager getInstance();

```

 1.作用
	
	初始化；

 2.注意
	
	无

 3.参数
	
	无

 4.返回	
 	
 	无

 5.异常
	
	无


### 2.handsup:

```java
/**
 * 请求排麦/举手
 *
 * @param callBack
 */
public void handsup(final CCBarLeyCallBack<Void> callBack)；


```

 1.作用
	
	请求排麦/举手；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功回调，失败回调；

 5.异常
	
	无


### 3.cancleInviteUserSpeak:

```java
/**
 * 取消邀请
 *
 * @param userId 用户id
 */
public void cancleInviteUserSpeak(String userId, CCBarLeyCallBack<Void> callBack)
;

```

 1.作用
	
	取消邀请；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String 	| 用户ID|
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|

 4.返回
	
	成功回调，失败回调；

 5.异常
	
	无


### 4.refuseTeacherInvite

```refuseTeacherInvite
/**
 * 拒绝老师连麦邀请
 */
public void refuseTeacherInvite(CCBarLeyCallBack<Void> callBack);

```

 1.作用
	
	拒绝老师连麦邀请；

 2.注意
	
	无

 3.参数
 
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功回调，失败回调；

 5.异常
	
	无


### 5.handsUpCancel

```java
/**
 * 取消连麦
 */
public void handsUpCancel(final CCBarLeyCallBack<Void> callBack);

```

 1.作用
	
	取消连麦；

 2.注意
	
	无

 3.参数
	 
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|

 4.返回
	
	成功返回 ，失败返回；

 5.异常
 	
 	无


### 6.kickUserFromSpeak:

```java
/**
 * 踢人下麦
 *
 * @param userId 用户id
 */
public void kickUserFromSpeak(String userId, CCBarLeyCallBack<Void> callBack);

```

 1.作用
  
  踢人下麦；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String 	| 用户ID|
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功返回 ，失败返回；

 5.异常
	
   无



### 7.handsDown:

```java
/**
 * 主动下麦
 *
 * @param callBack
 */
public void handsDown(CCBarLeyCallBack<Void> callBack);

```

 1.作用
	
	主动下麦；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功返回 ，失败返回；

 5.异常
 	
 	无


### 8.Studenthandup:

```java
/**
 * 自动连麦下的举手
 *
 * @param flag     <ul><li>true 举手</li><li>false 取消举手</li></ul>
 * @param callBack {@link CCBarLeyCallBack}
 */
public void Studenthandup(boolean flag, final CCBarLeyCallBack<Void> callBack) ;

```

 1.作用
	
	自动连麦下的举手；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| flag  	| boolean | true 举手；false 取消举手|
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功返回 ，失败返回；

 5.异常

	无


### 9.certainHandup:

```java
/**
 * 同意举手
 *
 * @param userId 互动者ID
 */
public void certainHandup(String userId, final CCBarLeyCallBack<Void> callBack);

```

 1.作用
	
	同意举手；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String 	| 用户ID|
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功返回 ，失败返回；

 5.异常
   
   无

### 10.inviteUserSpeak:

```objc
/**
 * 邀请学生上麦
 *
 * @param userId 互动者ID
 */
public void inviteUserSpeak(String userId, final CCBarLeyCallBack<Void> callBack);

```

 1.作用
	
	邀请学生上麦；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String 	| 用户ID|
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|


 4.返回
	
	成功返回 ，失败返回；

 5.异常
	
	无


### 11.acceptTeacherInvite:

```java
/**
 * 接收老师邀请
 */
public void acceptTeacherInvite(final CCBarLeyCallBack<Void> callBack);

```

 1.作用
	
	接收老师邀请；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|

 4.返回
	
	成功返回 ，失败返回；

 5.异常
	
	无


### 12.setSpeakMode:

```java
/**
 * 设置连麦模式
 *
 * @param lianmaiMode {@link @CCAtlasClient.LianmaiMode}
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限</li></ul>
 */
public boolean setSpeakMode(int lianmaiMode, CCBarLeyCallBack<Void> callBack) ;

```

 1.作用
	
	设置连麦模式(只对老师有效)；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| lianmaiMode  	| int 	| 连麦模式 0是自由，1是点名，3是自动|
| CCBarLeyCallBack  	| CCBarLeyCallBack | 回调函数|

 4.返回
	
	成功返回 ，失败返回；

 5.异常
	
	无



### 13.kickUserFromRoom:

```Java
/**
 * 踢出指定id的学生
 *
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean kickUserFromRoom(String userId)



```

 1.作用
	
	踢出指定id的学生；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userID  	| String 	| 用户id|


 4.返回
	
	true 执行</li><li>false 拒绝-没有权限/数据出错；

 5.异常
	
	无

### 14.setOnNotifyMaiStatusLisnter:

```java
/**
 * 学生排麦状态回调
 * <p>
 *
 * @param onNotifyMaiStatusLisnter {@link OnNotifyMaiStatusLisnter}
 */
public void setOnNotifyMaiStatusLisnter(OnNotifyMaiStatusLisnter onNotifyMaiStatusLisnter)



```

 1.作用
	
	学生排麦状态回调；

 2.注意
	
	无

 3.参数
 
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onNotifyMaiStatusLisnter  	| OnNotifyMaiStatusLisnter | 排麦状态回调|

 4.返回
 
```java
/**
 * 麦状态监听器
 */
public interface OnNotifyMaiStatusLisnter {
    /**
     * 上麦通知
     *
     * @param oldStatus 上一次连麦状态
     */
    void onUpMai(int oldStatus);

    /**
     * 下麦通知
     */
    void onDownMai();
}

```

 5.异常
	
 无



### 15.setOnNotifyInviteListener:

```java
/**
 * 学生收到老师上买邀请回调
 * <p>
 *
 * @param onNotifyInviteListener {@link OnNotifyInviteListener}
 */
public void setOnNotifyInviteListener(OnNotifyInviteListener onNotifyInviteListener) {
    Tools.log(TAG, "setOnNotifyInviteListener");
    mOnNotifyInviteListener = onNotifyInviteListener;
};

```

 1.作用
	
	学生收到老师上买邀请回调；

 2.注意
	
	无

 3.参数
 
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onNotifyInviteListener  	| OnNotifyInviteListener | 回调|

 4.返回
```java
/**
 * 邀请通知监听器
 */
public interface OnNotifyInviteListener {
    /**
     * 邀请通知
     */
    void onInvite();

    /**
     * 取消邀请通知
     */
    void onCancel();
}

	
```

 5.异常
	
	无


### 16.setOnQueueMaiUpdateListener:

```objc
/**
 * 学生排麦麦序更新回调
 */
public void setOnQueueMaiUpdateListener(OnQueueMaiUpdateListener onQueueMaiUpdateListener) {
    mOnQueueMaiUpdateListener = onQueueMaiUpdateListener;
};

```

 1.作用
	
	学生排麦麦序更新回调；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onQueueMaiUpdateListener  	| OnQueueMaiUpdateListener 	|回调|


 4.返回
```java
/**
 * 排麦麦序更新监听器
 */
public interface OnQueueMaiUpdateListener {
    /**
     * 更新通知
     *
     * @param users 用户列表
     */
    void onUpdateBarLeyStatus(ArrayList<CCUser> users);
}

```

 5.异常
	
	无


### 17.setOnSpeakModeUpdateListener:

```objc
/**
 * 连麦模式更新回调
 * <p>
 *
 * @param onLianmaiModeUpdateListener {@link OnSpeakModeUpdateListener}
 */
public void setOnSpeakModeUpdateListener(OnSpeakModeUpdateListener onLianmaiModeUpdateListener) {
    mOnLianmaiModeUpdateListener = onLianmaiModeUpdateListener;
};

```

 1.作用
	
	连麦模式更新回调；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onSpeakModeUpdateListener  	| OnSpeakModeUpdateListener 	| 回调|


 4.返回
```java
/**
 * 连麦模式更新监听器
 */
public interface OnSpeakModeUpdateListener {
    /**
     * 更新通知
     */
    void onBarLeyMode(int mode);
}

```

 5.异常
	
	无

### 18.setOnHandupListener

```java
/**
 * 设置举手回调
 *
 * @param onHandupListener {@link OnHandupListener}
 */
public void setOnHandupListener(OnHandupListener onHandupListener) {
    mOnHandupListener = onHandupListener;
};

```

 1.作用
 
	设置举手回调；

 2.注意
 
	无

 3.参数
	
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onHandupListener  	| OnHandupListener 	| 回调|

 4.返回
 
```java
	/**
 * 举手监听器
 */
public interface OnHandupListener {
    /**
     * 举手通知
     *
     * @param userid   举手的用户
     * @param isHandup <ul><li>true举手</li><li>false取消举手</li></ul>
     */
    void onHandupStatus(String userid, boolean isHandup);
}

```

 5.异常
 
	无


### 19.setOnUserListUpdateListener:

```java
/**
 * 设置用户列表更新回调
 */
public void setOnUserListUpdateListener(OnUserListUpdateListener onUserListUpdateListener) {
    mOnUserListUpdateListener = onUserListUpdateListener;
};

```

 1.作用
 
	设置用户列表更新回调；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| OnUserListUpdateListener  	| OnUserListUpdateListener 	|回调|


 4.返回
```java
/**
 * 用户列表更新监听器
 */
public interface OnUserListUpdateListener {
    /**
     * 更新通知
     *
     * @param users 用户列表 {@link CCUser}
     */
    void onUpdateUserList(ArrayList<CCUser> users);
}

```

 5.异常
	
	无


### 20.setOnKickOutListener:

```java
/**
 * 学生被老师提出房间回调
 *
 * @see #kickUserFromRoom(String)
 */
public void setOnKickOutListener(OnKickOutListener onKickOutListener) {
    mOnKickOutListener = onKickOutListener;
}
;

```

 1.作用
 
	学生被老师提出房间回调；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onKickOutListener  	| OnKickOutListener | 回调 |


 4.返回
 
```java
/**
 * 被老师踢出房间监听器
 */
public interface OnKickOutListener {
    /**
     * 踢出通知
     */
    void onKickOut();

    /**
     * 挤出房间通知
     */
    void onForceOut();
}

```

 5.异常
	
	无


### 21.setOnCancelHandUpListener:

```java
/**
 * 学生取消举手回调通知
 */
public void setOnCancelHandUpListener(OnCancelHandUpListener onCancelHandUpListener) {
    mOnCancelHandUpListener = onCancelHandUpListener;
};

```

 1.作用
 
	学生取消举手回调通知；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onCancelHandUpListener  	| OnCancelHandUpListener | 回调|


 4.返回
```java
/**
 * 取消举手监听器
 */
public interface OnCancelHandUpListener {
    /**
     * 学员取消举手通知
     */
    void OnCancelHandUp(String userId, String userName);
}

```

 5.异常
	
	无


### 22.setOnCCStreamPlaceholder:

```java
/**
 * 通知应用层 添加移除占位图
 */
public void setOnCCStreamPlaceholder(CCStreamPlaceholder mCCStreamPlaceholder) {
    this.mCCStreamPlaceholder = mCCStreamPlaceholder;
};

```

 1.作用
 
	通知应用层 添加移除占位图；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| mCCStreamPlaceholder  	| CCStreamPlaceholder | 回调|


 4.返回
```java
public interface CCStreamPlaceholder {
    /**
     * 添加占位图 CCUser
     */
    void onAddPlaceHolderView(Object result);
    /**
     * 移除占位图 CCUser
     */
    void onRemovePlaceHolderView(Object result);
}

```

 5.异常
	
	无










