[TOC]

# 0. 基础SDK以及音视频

CC视频推拉流以及各个组件都依赖CCAtlasClient这个核心类, 获取核心类的实例:CCAtlasClient mAtlasClient = CCAtlasClient.getInstance();

获取一个context实例：CCInteractSDK.init(this.getApplicationContext(), true);

## 0.1 获取sessionid
登入房间需要获取sessionid才可以登入房间
```javascript
public void login(String roomId, String userId, @CCAtlasClient.Role int role,
                      String username, String password,final CCAtlasCallBack<String> callBack) {
```
object 参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| roomId  | String | 房间ID   | 必选     |
| userId| String | 用户ID   | 必选     |
| role| int | 用户角色   | 必选     |
| username  | String |  用户名  | 必选     |
| password  | String | 用户密码   | 必选     |
| callBack| CCAtlasCallBack | 回调   | 必选     |

## 0.1 获取sessionid
登入房间需要获取sessionid才可以登入房间
```javascript
public void login(String roomId, String userId, @CCAtlasClient.Role int role,
                      String username, String password,final CCAtlasCallBack<String> callBack) {
```
object 参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| roomId  | String | 房间ID   | 必选     |
| userId| String | 用户ID   | 必选     |
| role| int | 用户角色   | 必选     |
| username  | String |  用户名  | 必选     |
| password  | String | 用户密码   | 必选     |
| callBack| CCAtlasCallBack | 回调   | 必选     |

### 0.1.1 登录成功

join登录成功以后，给客户返回配置信息data
```javascript
public void join(String sessionId, String userAcount, String areaCode, final CCAtlasCallBack<CCInteractBean> callBack) {
```
object 参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| sessionId | String | sessionid   | 必选     |
| userAcount| String | 用户ID   | 必选     |
| areaCode| String | 节点区域，可以传null   | 可选     |
| callBack| CCAtlasCallBack | 回调   | 必选     |

返回 CCInteractBean部分数据格式如下

```javascript
{

     result:"OK",
     data:{
    
            "desc": "9DC1A878A164F696",   //房间描述
    
            "talker_bitrate": 200,      //学生推流码率
    
            "publisher_bitrate": 200,    //老师推流码率
    
           "live": {
          		"id": 1231,   // 直播id
              	"status": 0,   // 1为开始
              	"last": "2000" // 如果status为1，则last为直播持续时长，单位毫秒
    			"startTime":"1503908480000"  //直播开始时间戳
            }
    
            "max_streams": 6,   //允许最大视频流数
    
            "name": "ha",      //用户昵称
    
            "result": "OK",
    
            "user": {
    
    	         "id": "6e373a456cd04b45b00f7b97986a45fc",   //用户唯一id
    
    	         "name": "123123123",   //用户name
    
    	         "role": "talker",    //'talker'学生   'presenter'老师
    
    	         "roomid": "EC05E15A770D84AC9C33DC5901307461", // 房间id
    
            },
    
            "video_mode": 1,   // 视频模式1为音视频模式  ， 2为仅音频模式
    
            "is_follow": '',  // 默认为空，如果是跟随模式，则为流id
    
            "max_users": 1, // 最大支持连麦人数
    
            "allow_chat": true  // 是否允许发言，默认为True, 房间级配置
    
            "allow_audio": true   // 是否允许麦克风 默认为True, 房间级配置
    
            "allow_speak": true  // 是否允许上麦，默认为True 房间级配置
    
            "rtspurl": "" //第三方推流地址
    	}
    }

```

# 1. 流相关方法

## 1.1 本地流相关方法

### 1.1.1 创建本地流
创建本地流，需要
```javascript
 public CCStream createLocalStream(@NonNull LocalStreamConfig config) throws StreamException {
```

参数说明：

| 参数名称   | 参数类型 | 说明                         | 是否必须 |
| ---------- | -------- | ---------------------------- | -------- |
| config    | LocalStreamConfig | 创建本地流自定义参数         | 必选     |

### 1.1.2 关闭远程流声音

```javascript
public void pauseAudio(@NonNull CCStream stream, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称   | 参数类型 | 说明       | 是否必须 |
| ---------- | -------- | ---------- | -------- |
| stream   | CCStream | 远程流 | 必选     |
| success    | function | 成功回调   | 可选     |
| fail       | function | 失败回调   | 可选     |


### 1.1.3 开启远程流声音

```javascript
  public void playAudio(@NonNull CCStream stream, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称   | 参数类型 | 说明       | 是否必须 |
| ---------- | -------- | ---------- | -------- |
| stream     | CCStream | 本地流名称 | 必选     |
| success    | function | 成功回调   | 可选     |
| fail       | function | 失败回调   | 可选     |

### 1.1.4 关闭本地流视频画面

```javascript
 public void pauseVideo(@NonNull CCStream stream, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称   | 参数类型 | 说明       | 是否必须 |
| ---------- | -------- | ---------- | -------- |
| stream     | CCStream | 本地流名称 | 必选     |
| success    | function | 成功回调   | 可选     |
| fail       | function | 失败回调   | 可选     |

### 1.1.5 开启本地流视频画面

```javascript
public void playVideo(@NonNull CCStream stream, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称   | 参数类型 | 说明       | 是否必须 |
| ---------- | -------- | ---------- | -------- |
| stream     | CCStream | 本地流名称 | 必选     |
| success    | function | 成功回调   | 可选     |
| fail       | function | 失败回调   | 可选     |


### 1.1.6 销毁本地流的资源

```javascript
public void destoryLocalStream() {
```

### 1.1.7  推送本地流

```javascript
  public synchronized void publish(final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称   | 参数类型 | 说明               | 是否必须 |
| ---------- | -------- | ------------------ | -------- |
| success    | function | 成功回调（含参数） | 可选     |
| fail       | function | 失败回调（含参数） | 可选     |


### 1.1.8  取消推送本地流

```javascript
 private synchronized void _publish(final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称   | 参数类型 | 说明               | 是否必须 |
| ---------- | -------- | ------------------ | -------- |
| success    | function | 成功回调（含参数） | 可选     |
| fail       | function | 失败回调（含参数） | 可选     |

## 1.2 远程流方法

### 1.2.1 订阅远程流

```javascript
public synchronized void SubscribeStream(CCStream remoteStream, CCAtlasCallBack<CCStream> callBack)
            throws StreamException {
```

参数说明：

| 参数名称  | 参数类型 | 说明                                 | 是否必须 |
| --------- | -------- | ------------------------------------ | -------- |
| remoteStream | CCStream | 要订阅的流对象                    | 必选     |
| success   | CCStream | 成功回调（含参数）                   | 可选     |
| fail      | function | 失败回调（含参数）                   | 可选     |

### 1.2.2 取消订阅远程流

```javascript
 public synchronized void unSubscribeStream(CCStream remoteStream, CCAtlasCallBack<Void>
            callBack) throws StreamException {
```

参数说明：

| 参数名称    | 参数类型 | 说明               | 是否必须 |
| ----------- | -------- | ------------------ | -------- |
| unSubStream | object   | 要取消订阅的流对象 | 必选     |
| success     | function | 成功回调（含参数） | 可选     |
| fail        | function | 失败回调（含参数） | 可选     |


## 1.3 监听流相关事件

事件监听，建议在初始化sdk后做监听

### 1.3.1 监听流服务事件通知

监听atlas的流服务事件，不使用排麦组件则监听该事件，使用排麦组件监听排麦流服务事件。

首先获取流服务的监听事件：mAtlasClient.setOnNotifyStreamListener(mClientObserver);

使用例子：（demo里有实现的代码逻辑，可供参考）

```javascript
 private CCAtlasClient.OnNotifyStreamListener mClientObserver = new CCAtlasClient.OnNotifyStreamListener() {

        @Override
        public void onStreamAllowSub(CCStream stream) {
           //这块监听是监听到有流可订阅，逻辑可以根据需要设置。
        }

        @Override
        public void onStreamRemoved(CCStream stream) {
            //这块监听是监听到流移除事件，逻辑可以根据需要设置
        }

        @Override
        public void onStreamError() {
            //这块监听是流错误事件，可以根据自己需要设置
        }
    };
```

## 1.4  部分高级功能

### 1.4.1 推流至cdn平台

直播开启后，讲师端推流成功后，可以调用该方法，将房间内的流推入cdn平台

```javascript
 public void addExternalOutput(@NonNull String serverUrl, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| url      | String   | 推流地址           | 可选     |
| success  | function | 成功回调（含参数） | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |


### 1.4.2 取消推流至cdn平台

讲师端调用推流至cdn平台成功后，如果要取消推送，可以调用该方法

```javascript
 public void removeExternalOutput(@NonNull String serverUrl, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| url      | String   | 推流地址           | 可选     |
| success  | function | 成功回调（含参数） | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |


### 1.4.3 更新推流平台地址

```javascript
 public void updateExternalOutput(@NonNull String serverUrl, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| url      | String   | 推流地址           | 必选     |
| success  | function | 成功回调（含参数） | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |


### 1.4.4 mix 流

此方法只有讲师角色有权限调用，将本地流合并入混流

```javascript
public void mix(final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| success  | function | 成功回调（含参数） | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |

### 1.4.5 unMix 流

此方法只有讲师角色有权限调用，将已加入混流的流取消混流

```javascript
public void unmix(final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| success  | function | 成功回调（含参数） | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |

### 1.4.6 设置混流位置

此方法只有讲师角色有权限调用，为已加入混流的流设置显示区域

```javascript
public void setRegion(@NonNull CCStream stream, final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称  | 参数类型 | 说明               | 是否必须 |
| --------- | -------- | ------------------ | -------- |
| stream | stream | 设置混流参数       | 必选     |
| success   | function | 成功回调（含参数） | 可选     |
| fail      | function | 失败回调（含参数） | 可选     |


### 1.4.7 获取混流位置

此方法只有讲师角色有权限调用，获取已加入混流的流的显示区域

```javascript
 public void getRegion(@NonNull CCStream stream, final CCAtlasCallBack<String> callBack) {
```

参数说明：

| 参数名称  | 参数类型 | 说明               | 是否必须 |
| --------- | -------- | ------------------ | -------- |
| stream    | stream   | 获取混流位置参数   | 必选     |
| success   | function | 成功回调（含参数） | 可选     |
| fail      | function | 失败回调（含参数） | 可选     |

### 1.4.8 获取单条流状态

此方法用于获取已经订阅到或已经推出去的流对象的音视频状态

```javascript
 public void getConnectionStats(@NonNull CCStream stream, final CCAtlasCallBack<ConnectionStatsWrapper> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明                           | 是否必须 |
| -------- | -------- | ------------------------------ | -------- |
| stream   | stream   | 已经订阅到或已经推出去的流对象 | 必选     |
| success  | function | 成功回调（含参数）             | 可选     |
| fail     | function | 失败回调（含参数）             | 可选     |


### 1.4.9 获取城市节点列表

此方法主要是获取到城市节点的列表
```javascript
 public void dispatch(String userid,final CCAtlasCallBack<CCCityBean> callBack){
```
参数说明：

| 参数名称 | 参数类型 | 说明                           | 是否必须 |
| -------- | -------- | ------------------------------ | -------- |
| userid   | String  | 账户ID             | 必选     |
| callBack | CCAtlasCallBack  | 回调      | 可选     |

# 2. 公有的SDK接口

## 2.1 主动调用事件

### 2.1.1  开启直播

直播开启接口由老师角色去控制

```javascript
 public void startLive(final CCAtlasCallBack<Void> callBack) {
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| success  | function | 成功回调（含参数） | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |


### 2.1.2 关闭直播

直播关闭接口由老师角色去控制

```javascript
public void stopLive(final CCAtlasCallBack<Void> callBack)
```

参数说明：

| 参数名称 | 参数类型 | 说明               | 是否必须 |
| -------- | -------- | ------------------ | -------- |
| success  | function | 成功回调           | 可选     |
| fail     | function | 失败回调（含参数） | 可选     |


### 2.1.3 判断直播是否开启

老师端是否开启了直播，返回true开始直播，false结束直播

```javascript
 public boolean isRoomLive() {
```

### 2.1.4 获取本地流ID

```javascript
 public String getLocalStreamId() {
```

### 2.1.4 获取直播间用户列表

```javascript
  public @Nullable ArrayList<CCUser> getUserList() {
```
### 2.1.5 资源释放接口，退出房间，需要释放资源

```javascript
   public void disconnectSocket() {
```

## 2.2 被动监听事件

事件监听，建议在初始化sdk后做监听

### 2.2.1 监听开启/结束直播事件通知

当老师角色调用开启/结束直播接口后，房间内所有人都会监听到该事件

首先获取开启/结束直播监听事件： mAtlasClient.setOnClassStatusListener(mClassStatusListener);

使用例子：（demo里有实现的代码逻辑，可供参考）

```javascript
 private CCAtlasClient.OnClassStatusListener mClassStatusListener = new CCAtlasClient.OnClassStatusListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }
    };
```
### 2.2.2 用户加入房间、退出房间通知

人员加入房间/退出房间通知事件

首先获取加入/退出房间监听事件：ccAtlasClient.setOnUserRoomStatus(mUpdateUserList);

使用例子：（demo里有实现的代码逻辑，可供参考）
```
 //人员加入房间/退出房间通知事件
    private CCAtlasClient.OnUserRoomStatus mUpdateUserList = new CCAtlasClient.OnUserRoomStatus() {
        @Override
        public void OnExitRoomUser(CCUserRoomStatus ccUserRoomStatus) {
            message(ccUserRoomStatus.getUserName() + "退出房间");
        }

        @Override
        public void OnJoinRoomUser(CCUserRoomStatus ccUserRoomStatus) {
            message(ccUserRoomStatus.getUserName() + "加入房间");
        }
    };
```

### 2.2.3 学员举手通知

人员在举手连麦模式下，举手通知事件

首先获取举手通知事件：ccAtlasClient.setOnUserHand(mUserHand);

使用例子：（demo里有实现的代码逻辑，可供参考）

```
//人员在举手连麦模式下，举手通知事件
    private CCAtlasClient.OnUserHand mUserHand = new CCAtlasClient.OnUserHand() {
        @Override
        public void UserHand(CCUser user) {
            message(user.getUserName() + "举手了");
        }
    };
```

### 2.2.4 用户自定义pusher 事件

首先设置自己的pusher事件：

```
 JSONObject data = new JSONObject();
        try {
            data.put("action","nihao");
            //用户自己定义socket事件
            ccAtlasClient.sendPublishMessage(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
```

监听自己的pusher事件：ccAtlasClient.setOnPublishMessageListener(mPublishMessage);

使用例子：（demo里有实现的代码逻辑，可供参考）

```
//用户监听自己设置的socket事件
    private CCAtlasClient.OnPublishMessageListener mPublishMessage = new CCAtlasClient.OnPublishMessageListener() {
        @Override
        public void onPublishMessage(JSONObject object) {
            try {
                message(object.getString("action"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
```

# 3、音视频属性流程
## 1、视频
### 1.1 开启或者关闭指定用户id的学生视频，该接口是老师端操作
```
public boolean toggleVideo(boolean flag, @NonNull String userId)
```
| 参数 | 参数说明  | 
| -------- | -------- | 
| flag     | true：开始视频，false;关闭视频| 
| userId   | 指定的用户id| 

### 1.2 开启视频
开启自己的视频，是否需要通知别人
```
public void enableVideo(boolean isDoBroadcast)
```
| 参数 | 参数说明  | 
| -------- | -------- | 
| isDoBroadcast| 是否通知其他用户| 

### 1.3 关闭视频
关闭自己的视频，是否需要通知别人
```
public void disableVideo(boolean isDoBroadcast)
```
| 参数 | 参数说明  | 
| -------- | -------- | 
| isDoBroadcast| 是否通知其他用户| 

## 2、音频
### 2.1 开启或者关闭指定用户id的学生音频，该接口是老师端操作
```
public boolean toggleAudio(boolean flag, @NonNull String userId)
```
| 参数 | 参数说明  | 
| -------- | -------- | 
| flag     | true：开始音频，false;关闭音频| 
| userId   | 指定的用户id| 
### 2.2 开启音频
开启自己音频，是否需要通知别人
```
public void enableAudio(boolean isDoBroadcast)
```
| 参数 | 参数说明  | 
| -------- | -------- | 
| isDoBroadcast| 是否通知其他用户| 
### 2.3 关闭音频
关闭自己视频，是否需要通知别人
```
public void disableAudio(boolean isDoBroadcast)
```
| 参数 | 参数说明  | 
| -------- | -------- | 
| isDoBroadcast| 是否通知其他用户| 

## 3、音视频更新通知，回调给应用层
### 3.1 学生多媒体状态被动变化回调
需要去设置监听多媒体事件
```
public void setOnMediaListener(OnMediaListener onMediaListener)

public interface OnMediaListener{
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

## 4、插播音视频（自定义控件CCMediaSurfaceView）

### 4.1使用方法布局文件使用，如下

```
  <com.bokecc.sskt.base.view.CCMediaSurfaceView
            android:id="@+id/id_ccmediasurface"
            android:layout_width="160dp"
            android:layout_height="90dp"
            android:visibility="visible" />
```
### 4.2自定义控件的回调

### 4.2.1 是不是显示自定义控件

业务需要：视频显示控件、音频隐藏控件，可根据此监听写自己的业务逻辑

```
public interface OnIsVisiableMadieListener {
        /**
         * @param isShow 是否显示插播音视频
         */
        void isShowMadie(boolean isShow);
    }
```

### 4.2.2 获取真实视频的宽高

业务需要：获取真实视频的宽高，进行做视频，防止视频变形；音频的时候宽高都是0

```
public interface OnVideoWHListener {
        void setVideoWH(int w, int h);
    }
```

### 4.2.3 视频加载

业务需要：视频需要有个加载过程

```
public interface OnProgressShowHide {
        void show();//显示加载动画

        void hide();//隐藏加载动画
    }
```
   

### 4.3视频播放操作方法

如果没有特殊需求，不建议使用，以为只是展示web端的，并且内部都已经实现
```
getVideoType()//获取视频类型true 视频，false 音频，进入房间的时候时候判断

seekToVideo() //视频跳转

pauseVideo() //视频暂停

```

