[TOC]

场景视频通过全球部署的虚拟网络，提供可以灵活搭配的API组合；
该文档主要说明带界面框架 ClassBaseLib的接口，该文档主要说明CCAtlasClient类常用接口，
内部使用接口，此处不做说明介绍；

# CCAtlasClient


## 一、概览
### API 汇总

| 返回值|          函数名                                        <!---->            |
| -------------: | :----------------------------------------------------------- |
| CCAtlasClient | + getInstance(); |
| void | - (void) setVideoMirrorMode(int mode);|
| boolean | - (boolean) isRoomLive();|
| int | - (int) getRole();|
| String | - (String) getRoomId();|
| String | - (String) getUserId();|
| String | - (String) getUserIdInPusher();|
| Room | - (Room)  getRoom();|
| String | - (String) getLiveTime();|
| ArrayList<CCUser> | - (void) getUserList();|
| int | - (int) getMediaMode();|
| void | - (void) getRoomMsg(String roomId, final CCAtlasCallBack<String> callBack) ;|
| void | - (void) dispatch(String userid, CCAtlasCallBack<CCCityListSet> callBack);|
| void | - (void ) login(String roomId, String userId, int role, String nickname, String password, CCAtlasCallBack<String> callBack);|
| void | - (void) join(String sessionId, String appId, String areaCode, boolean isUpdateRtmpLayout, CCAtlasCallBack<CCInteractBean> callBack);|
| void | - (void) getWarmVideoUrl(CCAtlasCallBack callBack);|
| SurfaceView | - (SurfaceView) startPreview(Context context, int renderMode);|
| void | - (void) stopPreview();|
| void | - (void) publish(final CCAtlasCallBack<Void> callBack);|
| void | - (void) unpublish(final CCAtlasCallBack<Void> callBack);|
| void | - (void) startLive(int isRecord, final CCAtlasCallBack<Void> callBack);|
| void | - (void) stopLive(final CCAtlasCallBack<Void> callBack);|
| void | - (void) SubscribeStream(CCStream remoteStream, int renderMode, CCAtlasCallBack<CCStream> callBack);|
| void | - (void ) SubscribeStream(CCStream remoteStream, int renderMode, CCAtlasCallBack<CCStream> callBack,boolean mirror);|
| boolean | - (boolean) isSubscribeStream(CCStream remoteStream);|
| void | - (void)  unSubscribeStream(CCStream remoteStream, CCAtlasCallBack<Void> callBack);|
| SurfaceView | - (SurfaceView) setSubRender(Context context, CCStream stream, int renderMode);|
| SurfaceView | - (SurfaceView) setSubRender(Context context, CCStream stream, int renderMode,boolean mirror);|
| void | - (void) leave(final CCAtlasCallBack<Void> callBack);|
| void | - (void) setResolution(int resolution);|
| int | - (int) getDefaultResolution();|
| boolean | - (boolean) setAppOrientation(int orientation);|
| boolean | - (boolean) setLocalVideoMirror(boolean mirror);|
| boolean | - (boolean) switchCamera();|
| void | - (void) enableVideo(boolean isDoBroadcast);|
| void | - (void) disableVideo(boolean isDoBroadcast);|
| void | - (void) enableAudio(boolean isDoBroadcast);|
| void | - (void ) disableAudio(boolean isDoBroadcast);|
| boolean | - (boolean) pauseAudio(CCStream stream);|
| boolean | - (boolean) playAudio(CCStream stream);|
| boolean | - (boolean) pauseVideo(CCStream stream);|
| boolean | - (boolean) playVideo(CCStream stream);|
| void | - (void) setSubStreamAudio(boolean isDoBroadcast);|
| int | - (int) getCupNum(String userId);|
| String | - (String) getUserJoinTime(String userId);|
| void | - (void) getRewardHistory(final CCAtlasCallBack<ArrayList<CCUser>> callBack);|
| boolean | - (boolean) toggleVideo(boolean flag, String userId);|
| boolean | - (boolean) toggleAudio(boolean flag, String userId);|
| boolean | - (boolean) gagOne(boolean flag, String userId);|
| boolean | - (boolean) studentNamed();|
| boolean | - (boolean) startNamed(long seconds);|
| void | - (void) sendVoteSelected(String voteId, String publisherId, boolean isSingle, ArrayList<Integer> voteOption);|
| void | - (void) sendBrainStomData(String brainId, String content, String title);|
| void | - (void) setOnServerListener(OnServerListener onServerListener);|
| void | - (void) setOnTeacherDownListener(OnTeacherDownListener onTeacherDownListener);|
| void | - (void) setOnReceiveNamedListener(OnReceiveNamedListener onReceiveNamedListener);|
| void | - (void) setOnStartNamedListener(OnStartNamedListener onStartNamedListener);|
| void | - (void) setOnAnswerNamedListener(OnAnswerNamedListener onAnswerNamedListener);|
| void | - (void) setOnRoomTimerListener(OnRoomTimerListener onRoomTimerListener);|
| void | - (void) setOnRollCallListener(OnRollCallListener onRollCallListener);|
| void | - (void) setOnBrainStomListener(OnBrainStomListener brainStomListener);|
| void | - (void) setOnBallotListener(OnBallotListener ballotListener);|
| void | - (void) setOnSendCupListener(OnSendCupListener onSendCupListener);|
| void | - (void) setOnSendHammerListener(OnSendHammerListener onSendHammerListener);|
| void | - (void) setInteractListener(CCInteractListener interactListener);|
| void | - (void) setOnClassStatusListener(OnClassStatusListener onClassStatusListener);|
| void | - (void) setOnMediaListener(OnMediaListener onMediaListener);|
| void | - (void) setOnUserCountUpdateListener(OnUserCountUpdateListener onUserCountUpdateListener);|
| void | - (void) setOnInterludeMediaListener(OnInterludeMediaListener onInterludeMediaListener);|
| void | - (void) setOnTalkerAudioStatusListener(OnTalkerAudioStatusListener onTalkerAudioStatusListener);|
| void | - (void) setOnMediaSyncListener(OnMediaSyncListener onMediaSyncListener);|
| void | - (void) setOnSwitchSpeak(OnSwitchSpeak onSwitchSpeak);|
| void | - (void) setOnNotifyStreamListener(OnNotifyStreamListener observer);|
| void | - (void) getChatHistory(final CCAtlasCallBack<ChatMsgHistory> callBack);|
| void | - (void) setOnDomSyncListener(OnDomSyncListener onDomSyncListener);|
| boolean | - (boolean) setCameraType(boolean isFrontCamera);|
| void | - (void) mediaSwitchAudioUserid(boolean value, String userid, int desRole, final CCAtlasCallBack<String> callBack);|

## 二、接口介绍
  
### 1.getInstance

```java
/**
 * 初始化对象
 * @return
 */
public static CCAtlasClient getInstance();

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


### 2.setVideoMirrorMode:

```java
/**
 * 设置视频镜像模式，这几种模式只针对使用前置摄像头的时候，要么预览和拉流都是镜像的，要么预览和拉流都不镜像
 * 主播端和观众端都是镜像效果，设置对应枚举值为：1  预览启用镜像，推流启用镜像
 * 主播端镜像，观众端非镜像效果，设置对应枚举值为 0  预览启用镜像，推流不启用镜像
 * 主播和观众端都非镜像效果，设置对应枚举值为：2   预览不启用镜像，推流不启用镜像
 * 主播非镜像，观众镜像效果，设置对应枚举值为：3  预览不启用镜像，推流启用镜像
 *
 * @param mode
 */
public void setVideoMirrorMode(int mode) {
    streamManager.setVideoMirrorMode(mode);
}



```

 1.作用
	
	设置视频镜像模式，这几种模式只针对使用前置摄像头的时候，要么预览和拉流都是镜像的，要么预览和拉流都不镜像；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| mode  	| int | 镜像参数参考|


 4.返回
	
	成功回调，失败回调；

 5.异常
	
	无


### 3.isRoomLive:

```java
/**
 * 判断当前直播间是否在直播
 *
 * @return <ul><li>true 直播</li><li>false 没有直播</li></ul>
 */
public boolean isRoomLive() 


```

 1.作用
	
	判断当前直播间是否在直播；

 2.注意
	
	无

 3.参数

	无

 4.返回
	
	true 直播；false 没有直播；

 5.异常
	
	无


### 4.getRole

```java
/**
 * 获取用户角色 0：教师，1：学生，2：旁听，3：隐身者
 *
 * @return
 */
public int getRole()


```

 1.作用
	
	获取用户角色；

 2.注意
	
	无

 3.参数
 
	无


 4.返回
	
	0：教师，1：学生，2：旁听，3：隐身者；

 5.异常
	
	无


### 5.getRoomId

```java
/**
 * 获取房间id
 *
 * @return
 */
public String getRoomId()

```

 1.作用
	
	获取房间id；

 2.注意
	
	无

 3.参数
	 
	无

 4.返回
	
	获取房间id；

 5.异常
 	
 	无


### 6.getUserId:

```java
/**
 * 获取当前appid
 *
 * @return
 */
public String getUserId()

```

 1.作用
  
  获取当前appid；账户ID；

 2.注意
	
	无

 3.参数

	无


 4.返回
	
	获取当前账户ID；

 5.异常
	
   无



### 7.getUserIdInPusher:

```java
/**
 * 获取当前用户id
 *
 * @return
 */
public String getUserIdInPusher()

```

 1.作用
	
	获取当前用户id;

 2.注意
	
	无

 3.参数

	无


 4.返回
	
	获取当前用户id；

 5.异常
 	
 	无


### 8.getRoom:

```java
/**
 * 获取直播间信息
 *
 * @return {@link Room}
 */
public Room getRoom() 


```

 1.作用
	
	获取直播间信息；

 2.注意
	
	无

 3.参数

	无


 4.返回
	
	获取直播间信息；

 5.异常

	无


### 9.getLiveTime:

```java
/**
 * 获取直播开始时间
 *
 * @return
 */
public String getLiveTime()


```

  1.作用
	
	获取直播开始时间；

 2.注意
	
	无

 3.参数

	无


 4.返回
	
	获取直播开始时间；

 5.异常

	无
	
### 10.getUserList:

```objc
/**
 * 获取直播间用户列表
 *
 * @return 用户列表
 */
public ArrayList<CCUser> getUserList() 

```

 1.作用
	
	获取直播间用户列表；

 2.注意
	
	无

 3.参数

	无


 4.返回
	
	获取直播间用户列表；

 5.异常
	
	无


### 11.getMediaMode:

```java
/**
 * 获取连麦多媒体格式
 *
 * @return
 */
public int getMediaMode()


```

 1.作用
	
	获取连麦多媒体格式；

 2.注意
	
	无

 3.参数

	无

 4.返回
 
```
	/**
	 * 媒体设置 仅音频
 	*/
	public static final int MEDIA_MODE_AUDIO = 0;
	/**
	 * 媒体设置 音视频
	 */
	public static final int MEDIA_MODE_BOTH = 1;
```

 5.异常
	
	无


### 12.getRoomMsg:

```java
/**
 * 获取直播间信息
 * todo  这里返回的数据应该进行解析，返回一个对象
 *
 * @param roomId   房间id
 * @param callBack 回调
 */
public synchronized void getRoomMsg(String roomId, final CCAtlasCallBack<String> callBack)

```

 1.作用
	
	获取直播间信息；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| roomId  	| String 	| 房间ID|
| callBack  	| CCAtlasCallBack | 回调函数|

 4.返回
	
	成功返回 ，失败返回；

 5.异常
	
	无



### 13.dispatch:

```Java
/**
 * 获取可用节点
 * todo  这里返回的数据应该进行解析，返回一个对象
 *
 * @param userid   账户id
 * @param callBack 回调
 */
public void dispatch(String userid, CCAtlasCallBack<CCCityListSet> callBack)


```

 1.作用
	
	获取可用节点；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userID  	| NSString 	| 用户id|
| callBack  	| CCAtlasCallBack | 回调ß|


 4.返回
	节点json

 5.异常
	
	无

### 14.login:

```java
/**
 * 登录接口
 * 获取用户的sessionid
 *
 * @param roomId   房间id
 * @param userId   用户id
 * @param role     用户角色
 * @param nickname 用户昵称
 * @param password 用户密码
 * @param callBack 回调
 */
public synchronized void login(String roomId, String userId, int role, String nickname, String password, CCAtlasCallBack<String> callBack)



```

 1.作用
	
	登录接口,获取用户的sessionid；

 2.注意
	
		用户名只支持字符、文字和数字，不支持特殊字符；

 3.参数
 
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| roomId  	| String | 房间ID|
| userId  	| String | 用户ID|
| role  	| int | 用户角色|
| nickname  	| String | 用户名|
| password  	| String | 用户密码|
| callBack  	| CCAtlasCallBack | 结果回调|

 4.返回
 
 	登录接口,获取用户的sessionid；
 	
 5.异常
	
	无



### 15.join:

```java
/**
 * 加入直播间
 *
 * @param sessionId          会话id 通过登录接口获取
 * @param appId              用户账号
 * @param areaCode           节点
 * @param isUpdateRtmpLayout 设置是否合流布局里面有学生画面，默认是false有画面，true无画面。
 * @param callBack           回调
 * @modify 1.暖场视频需要手动重新获取
 */
public void join(String sessionId, String appId, String areaCode, boolean isUpdateRtmpLayout, CCAtlasCallBack<CCInteractBean> callBack)


```

 1.作用
	
	加入直播间；

 2.注意
	
	无

 3.参数
 
| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| sessionId  	| String | 会话id|
| appId  	| String | 用户账号|
| areaCode  	| String | 节点可以为空|
| isUpdateRtmpLayout  	| boolean | 是否合流布局里|
| callBack  	| OnNotifyInviteListener | 回调|

 4.返回
 
	正常回调

 5.异常
	
	根据提示信息，调整


### 16.getWarmVideoUrl:

```java
/**
 * 获取暖场视频地址
 *
 * @param callBack
 */
public void getWarmVideoUrl(CCAtlasCallBack callBack) 


```

 1.作用
	
	获取暖场视频地址；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack |回调|


 4.返回

	返回暖场视频地址

 5.异常
	
	无


### 17.startPreview:

```java
/**
 * 开始预览
 *
 * @param context    当前页面的句柄
 * @param renderMode 渲染模式
 * RENDER_MODE_HIDDEN = 1;全铺满
 * RENDER_MODE_FIT = 2;自适应
 * @return
 */
public SurfaceView startPreview(Context context, int renderMode)

```

 1.作用
	
	开启预览，获得预览视图View；

 2.注意
	
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| context  | Context | 上下文|
| renderMode  | int | 渲染模式|


 4.返回

	获得预览视图View

 5.异常
	
	无

### 18.stopPreview

```java
/**
 * 停止预览
 */
public void stopPreview() 

```

 1.作用
 
	停止预览；

 2.注意
 
	无

 3.参数
	
	无

 4.返回
 
	无



 5.异常
 
	无


### 19.publish:

```java
/**
 * 开始推流
 *
 * @param callBack
 */
public void publish(final CCAtlasCallBack<Void> callBack)


```

 1.作用
 
	开始推流；

 2.注意
 
	开启预览之后才可以推流；

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callback  	| CCAtlasCallBack |回调|


 4.返回

	正常回调
	
 5.异常
	
	无


### 20.unpublish:

```java
/**
 * 停止推流
 *
 * @param callBack
 */
public void unpublish(final CCAtlasCallBack<Void> callBack) 


```

 1.作用
 
	停止推流；

 2.注意
 
	推流后，才可以停止推流

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack | 回调 |


 4.返回
 
	正常回调

 5.异常
	
	无


### 21.startLive:

```java
/**
 * 开始直播
 *
 * @param isRecord 0不开启录制/1开启录制
 * @param callBack
 */
public void startLive(int isRecord, final CCAtlasCallBack<Void> callBack)


```

 1.作用
 
	开始直播；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack | 回调|


 4.返回
	
	正常回调

 5.异常
	
	无

### 22.stopLive:

```java
/**
 * 结束直播
 *
 * @param callBack
 */
public void stopLive(final CCAtlasCallBack<Void> callBack)

```

 1.作用
 
	结束直播；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack | 回调|


 4.返回
	
	正常回调

 5.异常
	
	无
	
	
### 23.SubscribeStream:

```java
/**
 * 订阅远程流
 *
 * @param remoteStream
 * @param renderMode
 * @param callBack
 */
public void SubscribeStream(CCStream remoteStream, int renderMode, CCAtlasCallBack<CCStream> callBack) 

```

 1.作用
 
	订阅远程流；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| remoteStream  	| CCStream | 订阅流对象|
| renderMode  	| int | 订阅流模式全填充、自适应|
| callBack  	| CCAtlasCallBack | 回调|


 4.返回
	
	正常回调

 5.异常
	
	无
### 24.SubscribeStream:

```java
/**
 * 订阅远程流
 *
 * @param remoteStream
 * @param renderMode
 * @param callBack
 * @param mirror
 */
public void SubscribeStream(CCStream remoteStream, int renderMode, CCAtlasCallBack<CCStream> callBack,boolean mirror)

```

 1.作用
 
	订阅远程流是否开启镜像；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| remoteStream  	| CCStream | 订阅流对象|
| renderMode  	| int | 订阅流模式全填充、自适应|
| callBack  	| CCAtlasCallBack | 回调|
| mirror  	| booleab | 是否开启镜像|


 4.返回
	
	正常回调

 5.异常
	
	无
### 25.isSubscribeStream:

```java
/**
 *  是否订阅过
 * @param remoteStream
 * @return
 */
public boolean isSubscribeStream(CCStream remoteStream)

```

 1.作用
 
	是否订阅过；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| remoteStream  	| CCStream | 订阅流对象|


 4.返回
	
	true 订阅过；false 未订阅

 5.异常
	
	无
	
### 26.unSubscribeStream:

```java
/**
 * 取消订阅远程流
 *
 * @param remoteStream
 * @param callBack
 * @throws StreamException
 */
public void unSubscribeStream(CCStream remoteStream, CCAtlasCallBack<Void> callBack)

```

 1.作用
 
	取消订阅远程流；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| remoteStream  	| CCStream | 订阅流对象|
| callBack  	| CCAtlasCallBack | 回调|


 4.返回
	
	回调成功；失败方法

 5.异常
	
	无
	
### 27.setSubRender:

```java
/**
 * 二次订阅流
 * 不推荐使用，最好UI拿已经订阅好的SurfaceView进行操作
 *
 * @param stream 渲染本地传null，渲染远程传对应的CCStream
 * @return
 */
public SurfaceView setSubRender(Context context, CCStream stream, int renderMode)

```

 1.作用
 
	二次订阅流；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| context  	| Context | 上下文|
| remoteStream  	| CCStream | 订阅流对象|
| renderMode  	| int	 | 订阅模式|


 4.返回
	
	获得视频View

 5.异常
	
	无
	
### 28.setSubRender:

```java
/**
 * 二次订阅流
 * 不推荐使用，最好UI拿已经订阅好的SurfaceView进行操作
 *
 * @param stream 渲染本地传null，渲染远程传对应的CCStream
 * @param mirror mirror
 * @return
 */
public SurfaceView setSubRender(Context context, CCStream stream, int renderMode,boolean mirror)


```

 1.作用
 
	二次订阅流；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| context  	| Context | 上下文|
| remoteStream  	| CCStream | 订阅流对象|
| renderMode  	| int	 | 订阅模式|
| mirror  	| boolean	 | 镜像|


 4.返回
	
	获得视频View

 5.异常
	
	无	
	
### 29.leave:

```java
/**
 * 离开直播间
 *
 * @param callBack
 */
public void leave(final CCAtlasCallBack<Void> callBack)

```

 1.作用
 
	离开直播间；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack | 回调|


 4.返回
	
	回调

 5.异常
	
	无
	
	
### 30.setResolution:

```java
/**
 * 设置分辨率
 *
 * @param resolution
 */
public void setResolution(int resolution)


```

 1.作用
 
	设置分辨率；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| resolution  	| int | Resolution_240P、Resolution_480P、Resolution_720P|


 4.返回
	
	无

 5.异常
	
	无
	
	
### 31.getDefaultResolution:

```java
/**
 * 获取默认分辨率
 *
 * @return
 */
public int getDefaultResolution()

```

 1.作用
 
	获取默认分辨率；

 2.注意
 
	无

 3.参数

	无

 4.返回
	
	默认分辨率；

 5.异常
	
	无
	
		
		
### 32.setAppOrientation:

```java
/**
 * 设置显示图像的方向
 * 从0开始，顺时针旋转
 * 0-3 四个方向  0和 2竖屏 ， 1、3 横屏
 *
 * @param orientation 0-3 四个方向
 */
public boolean setAppOrientation(int orientation)


```

 1.作用
 
	设置显示图像的方向；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| orientation  	| int | true竖屏/false横屏|


 4.返回
	
	true 成功；false 失败
	
 5.异常
	
	无
		
		
### 33.setLocalVideoMirror:

```java
/**
 * 设置本地流镜像
 * 默认是镜像
 *
 * @param mirror true镜像/false非镜像
 */
public boolean setLocalVideoMirror(boolean mirror)


```

 1.作用
 
	设置本地流镜像；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| mirror  	| boolean | 本地流是否镜像|


 4.返回
	
	true 开启镜像成功；false 开启镜像失败

 5.异常
	
	无
	
	
### 34.switchCamera:

```java
/**
 * 切换摄像头
 *
 * @return
 */
public boolean switchCamera()


```

 1.作用
 
	切换摄像头；

 2.注意
 
	无

 3.参数

	无


 4.返回
	
	true 切换成功；false 切换失败

 5.异常
	
	无
	
	
### 35.enableVideo:

```java
/**
 * 开启视频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void enableVideo(boolean isDoBroadcast)


```

 1.作用
 
	 开启本地视频；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| isDoBroadcast  	| boolean | 是否发起广播|


 4.返回
	
	无

 5.异常
	
	无
	
### 36.disableVideo:

```java
/**
 * 关闭视频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void disableVideo(boolean isDoBroadcast)


```

 1.作用
 
	 关闭本地视频；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| isDoBroadcast  	| boolean | 是否发起广播|


 4.返回
	
	无

 5.异常
	
	无

### 37.enableAudio:

```java
/**
 * 开启音频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void enableAudio(boolean isDoBroadcast) 


```

 1.作用
 
	 开启本地音频；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| isDoBroadcast  	| boolean | 是否发起广播|


 4.返回
	
	无

 5.异常
	
	无

### 38.disableAudio:

```java
/**
 * 关闭音频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void disableAudio(boolean isDoBroadcast)



```

 1.作用
 
	 关闭本地音频；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| isDoBroadcast  	| boolean | 是否发起广播|


 4.返回
	
	无

 5.异常
	
	无

### 39.pauseAudio:

```java
/**
 * 暂停远程流音频（不再从服务端拉取音频数据）
 *
 * @param stream
 * @return
 */
public boolean pauseAudio(CCStream stream) 



```

 1.作用
 
	 暂停远程流音频（不再从服务端拉取音频数据）；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| stream  	| CCStream | 订阅成功的流对象|


 4.返回
	
	true 调用成功；false 调用失败；

 5.异常
	
	无

### 40.playAudio:

```java
/**
 * 播放远程流音频（恢复从服务端拉取音频数据）
 *
 * @param stream
 * @return
 */
public boolean playAudio(CCStream stream) 


```

 1.作用
 
	 播放远程流音频（恢复从服务端拉取音频数据）；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| stream  	| CCStream | 订阅成功的流对象|


 4.返回
	
	true 调用成功；false 调用失败；

 5.异常
	
	无

### 41.pauseVideo:

```java
/**
 * 暂停远程流视频（不再从服务端拉取视频数据）
 *
 * @param stream
 * @return
 */
public boolean pauseVideo(CCStream stream) 


```

 1.作用
 
	 暂停远程流视频（不再从服务端拉取视频数据）；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| stream  	| CCStream | 订阅成功的流对象|


 4.返回
	
	true 调用成功；false 调用失败；

 5.异常
	
	无

### 42.playVideo:

```java
/**
 * 播放远程视频（恢复从服务端拉取视频数据）
 *
 * @param stream
 * @return
 */
public boolean playVideo(CCStream stream)


```

 1.作用
 
	播放远程视频（恢复从服务端拉取视频数据）；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| stream  	| CCStream | 订阅成功的流对象|


 4.返回
	
	true 调用成功；false 调用失败；

 5.异常
	
	无

	
	
### 43.getCupNum:

```java
/**
 * 获取奖杯数量
 *
 * @param userId
 * @return
 */
public int getCupNum(String userId)


```

 1.作用
 
	获取奖杯数量；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String | 用户ID|


 4.返回
	
	奖杯数量；

 5.异常
	
	无
	
### 44.getUserJoinTime:

```java
/**
 * 获取加入房间的时间
 *
 * @param userId
 * @return
 */
public String getUserJoinTime(String userId) 



```

 1.作用
 
	获取加入房间的时间，排序使用；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String | 用户ID|


 4.返回
	
	加入房间的时间；

 5.异常
	
	无	
	
### 45.getRewardHistory:

```java
/**
 * 获取奖赏历史记录（刚进来能够获取其他在线人员获得的奖赏记录）
 *
 * @param callBack
 */
public void getRewardHistory(CCAtlasCallBack<ArrayList<CCUser>> callBack)

```

 1.作用
 
	获取奖赏历史记录（刚进来能够获取其他在线人员获得的奖赏记录）；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack | 回调|


 4.返回
	
	回调成功；回调失败

 5.异常
	
	无
	
	
### 46.toggleVideo:

```java
/**
 * 开关指定id的学生视频
 *
 * @param flag   <ul><li>true 开启视频</li><li>false 关闭视频</li></ul>
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean toggleVideo(boolean flag, String userId)


```

 1.作用
 
	开关指定id的学生视频；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| flag  	| boolean | 开关视频|
| userId  	| String | 用户ID|


 4.返回
	
	true 执行；false 拒绝-没有权限/数据出错

 5.异常
	
	无
	
### 47.toggleAudio:

```java
/**
 * 开关指定id的学生音频
 *
 * @param flag   <ul><li>true 开启音频</li><li>false 关闭音频</li></ul>
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean toggleAudio(boolean flag, String userId)



```

 1.作用
 
	开关指定id的学生音频；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| flag  	| boolean | 开关视频|
| userId  	| String | 用户ID|


 4.返回
	
	true 执行；false 拒绝-没有权限/数据出错

 5.异常
	
	无
		
### 48.gagOne:

```java
/**
 * 开启/关闭指定学生禁言
 *
 * @param flag   <ul><li>true 开启禁言</li><li>false 关闭禁言</li></ul>
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean gagOne(boolean flag, String userId)


```

 1.作用
 
	开启/关闭指定学生禁言；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| flag  	| boolean | 开关视频|
| userId  	| String | 用户ID|


 4.返回
	
	true 执行；false 拒绝-没有权限/数据出错

 5.异常
	
	无
		
	
	
### 49.isSubscribeStream:

```java
/**
 * 学生签到
 */
public boolean studentNamed() 


```

 1.作用
 
	学生签到；

 2.注意
 
	无

 3.参数

	无

 4.返回
	
	true 签到成功；false 签到失败
	
 5.异常
	
	无
	
	
### 50.startNamed:

```java
/**
 * 
 *
 * @param seconds 点名持续时间
 */
public boolean startNamed(long seconds)


```

 1.作用
 
	点名持续时间；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| seconds  	| long | 点名持续时间|


 4.返回
	
	true 调用成功；false 调用失败

 5.异常
	
	无
	
	
### 51.sendVoteSelected:

```java
/**
 * 发送答案
 *
 * @param voteId
 * @param publisherId
 * @param isSingle    是否是单选
 * @param voteOption  选项
 * @see Vote
 */
public void sendVoteSelected(String voteId, String publisherId, boolean isSingle, ArrayList<Integer> voteOption) 

```

 1.作用
 
	发送答案；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| voteId  	| String | 答题ID|
| publisherId  	| String | 答题发起者ID|
| isSingle  	| boolean | 单选答案|
| voteOption  	| ArrayList<Integer> | 多选的答案|


 4.返回
	
	无

 5.异常
	
	无
	
### 52.sendBrainStomData:

```java
/**
 * 提交头脑风暴
 *
 * @param brainId 头脑风暴id
 * @param content 头脑风暴内容
 * @param title   头脑风暴标题
 */
public void sendBrainStomData(String brainId, String content, String title) 

```

 1.作用
 
	发送答案；

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| brainId  	| String | 头脑风暴id|
| content  	| String | 问题回答|
| title  	| String | 问题标题|



 4.返回
	
	无

 5.异常
	
	无
	

	
### 53.ccSendCupData:

```java
/**
 * 发送奖杯
 *
 * @param userId
 * @param userName
 * @return
 */
public boolean ccSendCupData(String userId, String userName)


```

 1.作用
 
	发送奖杯；

 2.注意
 
	老师端使用

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| userId  	| String | 用户ID|
| userName  	| String | 用户名称|


 4.返回
	
	true 发送成功；false 发送失败

 5.异常
	
	无
	
	
### 54.sendBallotData:

```java
/**
 * 学生提交投票结果
 *
 * @param ballotId 投票id
 * @param content  投票内容
 * @param title    投票标题
 */
public void sendBallotData(String ballotId, ArrayList<Integer> content, String title)


```

 1.作用
 
	学生提交投票结果；

 2.注意
 
	无
	
 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| ballotId  	| String | 投票id|
| content  	| ArrayList<Integer> | 投票内容|
| title  	| String | 投票标题|


 4.返回
	
	true 请求成功；false 请求失败
	
 5.异常
	
	无
		

### 55.setOnServerListener:

```java
/**
 * pusher和atlas服务断开与连接回调通知
 * <p>
 */
public void setOnServerListener(OnServerListener onServerListener) 

```

 1.作用
 
	pusher和atlas服务断开与连接回调通知

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onServerListener  	| OnServerListener | 回调|


 4.返回
```java
public interface OnServerListener {

    /**连接*/
    void onConnect();
    /**重连*/
    void onReconnect();
    /**重连中*/
    void onReconnecting();
    /**连接断开*/
    @Deprecated
    void onDisconnect(int platform);
    /**重连失败*/
    void onReconnectFailed();
    /**断开连接*/
    void onDisconnect();


}

```

 5.异常
	
	无

### 56.setOnReceiveNamedListener:

```java
/**
 * 收到教师点名回调
 */
public void setOnReceiveNamedListener(OnReceiveNamedListener onReceiveNamedListener)


```

 1.作用
 
	收到教师点名回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onReceiveNamedListener  	| OnReceiveNamedListener | 回调|


 4.返回
```java
public interface OnReceiveNamedListener {
    /**
     * 收到点名通知
     *
     * @param namedTime 点名有效时长
     */
    void onReceived(int namedTime);
}

```

 5.异常
	
	无

### 57.setOnStartNamedListener:

```java
/**
 * 教师开始点名回调
 */
public void setOnStartNamedListener(OnStartNamedListener onStartNamedListener) 

```

 1.作用
 
	教师开始点名回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onStartNamedListener  	| OnStartNamedListener | 回调|


 4.返回
```java
public interface OnStartNamedListener {
    /**
     * 开始点名通知
     *
     * @param isAllow 是否允许点名
     * @param ids     当前参加点名的所有用户id
     */
    void onStartNamedResult(boolean isAllow, ArrayList<String> ids);
}


```

 5.异常
	
	无

### 58.setOnAnswerNamedListener:

```java
/**
 * 学生应该点名回调
 */
public void setOnAnswerNamedListener(OnAnswerNamedListener onAnswerNamedListener) 


```

 1.作用
 
	学生应该点名回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onAnswerNamedListener  	| OnAnswerNamedListener | 回调|


 4.返回
```java
public interface OnAnswerNamedListener {
    /**
     * 应答通知
     *
     * @param answerUserId 应答用户id
     * @param answerIds    当前参加了点名的用户id集合
     */
    void onAnswered(String answerUserId, ArrayList<String> answerIds);
}



```

 5.异常
	
	无

### 58.setOnRoomTimerListener:

```java
/**
 * 房间计时器回调
 */
public void setOnRoomTimerListener(OnRoomTimerListener onRoomTimerListener)



```

 1.作用
 
	房间计时器回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onRoomTimerListener  	| OnRoomTimerListener | 回调|


 4.返回
```java
public interface OnRoomTimerListener {
    /**
     * 开始计时
     *
     * @param startTime 开始时间戳（发起方的可能会有误差）
     * @param lastTime  剩余计时时长
     */
    void onTimer(long startTime, long lastTime);

    /**
     * 停止
     */
    void onStop();
}

```

 5.异常
	
	无
	

### 59.setOnRollCallListener:

```java
/**
 * 答题回调
 */
public void setOnRollCallListener(OnRollCallListener onRollCallListener)


```

 1.作用
 
	学生应该点名回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onAnswerNamedListener  	| OnAnswerNamedListener | 回调|


 4.返回
```java
public interface OnRollCallListener {
    /**
     * 开始答题
     *
     * @param vote {@link Vote}
     */
    void onStart(Vote vote);

    /**
     * 停止答题
     */
    void onStop(String voteId);

    /**
     * 答题结果
     *
     * @param voteResult {@link VoteResult}
     */
    void onResult(VoteResult voteResult);
}


```

 5.异常
	
	无
	
	
### 60.setOnBrainStomListener:

```java
/**
 * 头脑风暴回调通知事件
 */
public void setOnBrainStomListener(OnBrainStomListener brainStomListener)

```

 1.作用
 
	头脑风暴回调通知事件

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| brainStomListener  	| OnBrainStomListener | 回调|


 4.返回
```java
public interface OnBrainStomListener {
    /**
     * 开始头脑风暴数据
     *
     * @param brainStom {@link BrainStom}
     */
    void onStart(BrainStom brainStom);

    /**
     * 结束头脑风暴数据
     *
     * @param id 头脑风暴id
     */
    void onStop(String id);
}


```

 5.异常
	
	无
	
### 61.setOnBallotListener:

```java
/**
 * 投票回调
 */
public void setOnBallotListener(OnBallotListener ballotListener)

```

 1.作用
 
	投票回调
	
 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| ballotListener  	| OnBallotListener | 回调|


 4.返回
```java
public interface OnBallotListener {
    /**
     * 投票开始数据
     *
     * @param ballot {@link Ballot}
     */
    void onStart(Ballot ballot);

    /**
     * 结束投票数据
     *
     * @param id 投票id
     */
    void onStop(String id);

    /**
     * 投票结果结果
     *
     * @param ballotResult {@link BallotResult}
     */
    void onResult(BallotResult ballotResult);
}

```

 5.异常
	
	无
	
### 62.setOnSendCupListener:

```java
/**
 * 发送奖杯回调
 */
public void setOnSendCupListener(OnSendCupListener onSendCupListener)

```

 1.作用
 
	发送奖杯回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onSendCupListener  	| OnSendCupListener | 回调|


 4.返回
```java
public interface OnSendCupListener {
    /**
     * 发送奖杯监听
     */
    void onSendCup(SendReward sendReward);

    /**
     * 一键奖励发送奖杯监听
     */
    void onSendCups(SendReward sendReward);
}

```

 5.异常
	
	无
	
### 63.setOnSendHammerListener:

```java
/**
 * 发送锤子回调通知
 */
public void setOnSendHammerListener(OnSendHammerListener onSendHammerListener)


```

 1.作用
 
	发送锤子回调通知

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onSendHammerListener  	| OnSendHammerListener | 回调|


 4.返回
```java
public interface OnSendHammerListener {
    /**
     * 发送锤子监听
     */
    void onSendHammer(SendReward sendReward);
}


```

 5.异常
	
	无
	
### 64.setInteractListener:

```java

/**
 * 设置互动监听
 *
 * @param interactListener
 */
public void setInteractListener(CCInteractListener interactListener) 


```

 1.作用
 
	设置互动监听

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| interactListener  	| CCInteractListener | 回调|


 4.返回
```java
public interface CCInteractListener {

    /**计时器*/
    void roomtimerlistener(long[] times);

    /**教师发起点名事件/学生端监听点名事件*/
    void StartRollCallListener(NamedInfo mNamedInfo);
    /**教师监听点名列表*/
    void RollCallListListener(NamedResult namedResult);
    /**学生回应点名/教师端监听点名*/
    void AnswerRollCallListener(String userId);

    /**开始答题*/
    void startanswerlistener(Vote vote);
    /**停止答题*/
    void stopanswerlister(String voteId);
    /**答题结果*/
    void answerresultlistener(VoteResult voteResult);

    /**头脑风暴*/
    void sendBrainstom(BrainStom brainStom);
    /**结束头脑风暴*/
    void endBrainstom(String brainStomId);
    /**发起投票*/
    void sendVote(Ballot ballot);
    /**结束投票*/
    void endVote(BallotResult ballotResult);
    /**奖杯*/
    void cup(SendReward rewardData);
    /**鲜花*/
    void flower(SendReward rewardData1);

    /**saas举手*/
    void raiseHands(String userId);
    /**更新用户数据，包括奖杯，鲜花的数量*/
    void updateUserData();

}



```

 5.异常
	
	无
	
### 65.setOnInterWramMediaListener:

```java

/**
 * 暖场视频
 */
public void setOnInterWramMediaListener(OnInterWramMediaListener onInterWramMediaListener)

```

 1.作用
 
	暖场视频回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onInterWramMediaListener  	| OnInterWramMediaListener | 回调|


 4.返回
```java
public interface OnInterWramMediaListener {
    /**
     * 插播暖厂回调
     */
    void onInterWram(Object object);
}


```

 5.异常
	
	无
	
### 66.setOnClassStatusListener:

```java
/**
 * 上课状态变化通知
 */
public void setOnClassStatusListener(OnClassStatusListener onClassStatusListener) 

```

 1.作用
 
	上课状态变化通知

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onClassStatusListener  	| OnClassStatusListener | 回调|


 4.返回
```java
public interface OnClassStatusListener {
    /**
     * 开始上课通知
     */
    void onStart();

    /**
     * 下课通知
     */
    void onStop();
}

```

 5.异常
	
	无
	
### 67.setOnMediaListener:

```java
/**
 * 学生多媒体状态被动变化回调
 */
public void setOnMediaListener(OnMediaListener onMediaListener)

```

 1.作用
 
	学生多媒体状态被动变化回调
	
 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onMediaListener  	| OnMediaListener | 回调|


 4.返回
```java
public interface OnMediaListener {
    /**
     * 麦克风更新通知
     *
     * @param userid       当前操作的用户id
     * @param isAllowAudio <ul><li>true开启麦克风</li><li>false关闭麦克风</li></ul>
     * @param isSelf       <ul><li>true是自己</li><li>false不是自己</li></ul>
     */
    void onAudio(String userid, boolean isAllowAudio, boolean isSelf);

    /**
     * 摄像头更新通知
     *
     * @param userid       当前操作的用户id
     * @param isAllowVideo <ul><li>true开启摄像头</li><li>false关闭摄像头</li></ul>
     * @param isSelf       <ul><li>true是自己</li><li>false不是自己</li></ul>
     */
    void onVideo(String userid, boolean isAllowVideo, boolean isSelf);
}


```

 5.异常
	
	无
	
### 68.setOnInterludeMediaListener:

```java
/**
 * 设置插播音视频回调
 */
public void setOnInterludeMediaListener(OnInterludeMediaListener onInterludeMediaListener)

```

 1.作用
 
	设置插播音视频回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onInterludeMediaListener  	| OnInterludeMediaListener | 回调|


 4.返回
```java
public interface OnInterludeMediaListener {
    /**
     * 插播回调
     */
    void onInterlude(JSONObject object);
}

```

 5.异常
	
	无
	
### 69.setOnTalkerAudioStatusListener:

```java
/**
 * 设置实时更新当前房间是不是需要取消音频
 *
 * @param onTalkerAudioStatusListener
 */
public void setOnTalkerAudioStatusListener(OnTalkerAudioStatusListener onTalkerAudioStatusListener) {
    mOnTalkerAudioStatusListener = onTalkerAudioStatusListener;
}

```

 1.作用
 
	设置实时更新当前房间是不是需要取消音频

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| nTalkerAudioStatusListener  | OnTalkerAudioStatusListener | 回调|


 4.返回
```java
// 0是pause；1是play；
public interface OnTalkerAudioStatusListener {

    void OnTalkerAudioStatus(int talkerAudio);
}


```

 5.异常
	
	无
	
	
### 70.setOnMediaSyncListener:

```java
/**
 * 设置插播音视频同步
 *
 * @param onMediaSyncListener
 */
public void setOnMediaSyncListener(OnMediaSyncListener onMediaSyncListener)


```

 1.作用
 
	设置插播音视频同步

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onMediaSyncListener  	| OnMediaSyncListener| 回调|


 4.返回
```java
public interface OnMediaSyncListener {

    void OnMediaSync(JSONObject object);
}


```

 5.异常
	
	无
	
	
### 71.setOnSwitchSpeak:

```java
/**
 * 上下麦
 *
 * @param onSwitchSpeak
 */
public void setOnSwitchSpeak(OnSwitchSpeak onSwitchSpeak)

```

 1.作用
 
	上下麦

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onSwitchSpeak  	| OnSwitchSpeak | 回调|


 4.返回
```java
public interface OnSwitchSpeak {

    void OnSwitchSpeakOn();

    void OnSwitchSpeakOff();
}


```

 5.异常
	
	无
	
	
###	72.setOnNotifyStreamListener:

```java
/**
 * 通知应用程序处理流事件监听
 *
 * @param observer
 */
public void setOnNotifyStreamListener(OnNotifyStreamListener observer)

```

 1.作用
 
	通知应用程序处理流事件监听

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| observer  	| OnNotifyStreamListener | 回调|


 4.返回
```java
public interface OnNotifyStreamListener {

    /**应用层允许订阅*/
    void onStreamAllowSub(SubscribeRemoteStream stream);
    /**流被移除*/
    void onStreamRemoved(SubscribeRemoteStream stream);

    /**流服务初始成功*/
    void onServerInitSuccess();
    /**流服务初始失败*/
    void onServerInitFail();
    /**流服务连接成功*/
    void onServerConnected();
    /**流服务重新连接*/
    void onServerReconnect();
    /**流发生错误*/
    void onStreamError();
    /**流服务断开*/
    void onServerDisconnected();

    /**开始线路优化*/
    void onStartRouteOptimization();
    /**结束线路优化*/
    void onStopRouteOptimization();
    /**优化线路失败*/
    void onRouteOptimizationError(String msg);
    /**重新加载预览*/
    void onReloadPreview();
    /**学生端下麦*/
    void onStudentDownMai();

    /**
     * 推流状态回调
     * @param publishQuality
     */
    void onPublishQuality(CCPublishQuality publishQuality);
}

```

 5.异常
	
	无
	
	
### 73.getChatHistory:

```java
/**
 * 获取聊天历史
 * 注意：需要先调用join接口成功后才可以调用次接口，否则返回错误
 *
 * @param callBack
 */
public void getChatHistory(CCAtlasCallBack<ChatMsgHistory> callBack)
```

 1.作用
 
	设置插播音视频回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| callBack  	| CCAtlasCallBack<ChatMsgHistory> | 回调|


 4.返回

	回调成功失败

 5.异常
	
	无
	
	
### 74.setOnDomSyncListener:

```java

/**
 * dom拖拽事件
 * @param onDomSyncListener
 */
public void setOnDomSyncListener(OnDomSyncListener onDomSyncListener) 

```

 1.作用
 
	dom拖拽事件

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onDomSyncListener  	| OnDomSyncListener | 回调|


 4.返回
```java

public interface OnDomSyncListener {
    /**
     * @param json 同步信息
     */
    void onOnDomSync(String json);
}

```

 5.异常
	
	无
	
### 75.setCameraType:

```java

/**
 * 设置默认开启摄像头
 *
 * @param isFrontCamera 摄像头类型 {@link .boolean }
 */
public boolean setCameraType(boolean isFrontCamera) 


```

 1.作用
 
	dom拖拽事件

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| isFrontCamera  	| boolean | true前置；false后置|


 4.返回

	true 设置成功；false 设置失败

 5.异常
	
	无
	
### 76.mediaSwitchAudioUserid:

```java

/**
 * 开关麦克风权限接口判断 
 *
 * @param value   开关麦
 * @param userid  被操作者id
 * @param desRole 被操作者角色
 */
public void mediaSwitchAudioUserid(boolean value, String userid, int desRole, CCAtlasCallBack<String> callBack)


```

 1.作用
 
	dom拖拽事件

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| value  	| boolean | 开关麦|
| userid  	| String | 被操作者id|
| desRole  	| int |被操作者角色|
| callBack  	|  CCAtlasCallBack<String>  | 回调|


 4.返回

	回调成功失败;

 5.异常
	
	无
	
	
### 77.setOnUserCountUpdateListener:

```java

/**
 * 设置用户人数回调
 */
public void setOnUserCountUpdateListener(OnUserCountUpdateListener onUserCountUpdateListener)



```

 1.作用
 
	设置用户人数回调

 2.注意
 
	无

 3.参数

| 参数名   	| 类型            | 说明   |
| -------- 	| -------------	| ------ |
| onUserCountUpdateListener  | OnUserCountUpdateListener | 回调|



 4.返回

```java
public interface OnUserCountUpdateListener {
    /**
     * 更新通知
     *
     * @param classCount    当前直播间人数（老师+学生）
     * @param audienceCount 旁听人数
     */
    void onUpdate(int classCount, int audienceCount);
}


```

 5.异常
	
	无	
		
### 78.常用基础变量
	
```java

/**
 * 媒体设置 仅音频
 */
public static final int MEDIA_MODE_AUDIO = 0;
/**
 * 媒体设置 音视频
 */
public static final int MEDIA_MODE_BOTH = 1;

/**
 * 连麦模式 自由连麦
 */
public static final int LIANMAI_MODE_FREE = 0; // 自由
/**
 * 连麦模式 点名连麦
 */
public static final int LIANMAI_MODE_NAMED = 1; // 点名
/**
 * 连麦模式 自动连麦
 */
public static final int LIANMAI_MODE_AUTO = 3; // 自动

/**
 * 用户角色 老师
 */
public static final int PRESENTER = 0;
/**
 * 用户角色 学生
 */
public static final int TALKER = 1;
/**
 * 用户角色 旁听
 */
public static final int AUDITOR = 2;
/**
 * 用户角色 隐身者
 */
public static final int INSPECTOR = 3;
/**
 * 用户角色 助教
 */
public static final int ASSISTANT = 4;
/**
 * 分辨率1280X720P
 */
public static final int Resolution_720P = 720;
/**
 * 分辨率640X480P
 */
public static final int Resolution_480P = 480;
/**
 * 分辨率320X240P
 */
public static final int Resolution_240P = 240;


```





