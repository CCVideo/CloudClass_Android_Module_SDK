[TOC]
## 1.概述
提供云课堂基础SDK功能，包括推流，拉流，排麦组件，聊天以及白板组件。为用户提供快速，简便的方法开展自己的实时互动课堂。

### 1.1 功能特性
安卓端SDK目前支持了音视频sdk、白板插件、聊天插件以及排麦插件

#### 1.1.1 音视频SDK功能
| |  |  |
| --- | --- | --- |
| 功能特性 | 描述 | 备注 |
| 推流  |支持推流到服务器|
| 拉流   |支持从服务器订阅流|
|获取流状态|支持获取流的状态(发报数、收报数、丢包数、延时)|
| 前后摄像头切换 | 支持手机前后摄像头切换 |  |
| 后台播放 | 支持直播退到后台只播放音频 |  |
| 支持https协议 | 支持接口https请求 |  |

#### 1.1.2 白板插件功能
| |  |  |
| --- | --- | --- |
| 功能特性 | 描述 | 备注 |
| 文档翻页 |支持接收服务端的文档翻页数据|
| PPT动画  |支持接收服务器的PPT动画数据|
| 画笔功能  |支持画笔、清除、撤销、历史数据|
| 授权标注功能  |学生被授权，支持画笔功能|
| 设为讲师功能  |学生被设为讲师，支持画笔、清除、翻页ppt|

#### 1.1.3 聊天插件功能
| |  |  |
| --- | --- | --- |
| 功能特性 | 描述 | 备注 |
| 文本、表情发送 |支持接收服务端的文本和表情数据|
| 图片发送       |支持接收服务器的图片数据|
| 禁言          |分为指定用户的禁言，以及全体禁言|

#### 1.1.3 排麦插件功能
| |  |  |
| --- | --- | --- |
| 功能特性 | 描述 | 备注 |
| 自由连麦  |互动者可自由连麦,无需老师确认|
| 自动连麦  |互动者进入房间后自动连麦|
| 举手连麦  |互动者可举手申请连麦,需老师确认才可连麦|

### 1.2 阅读对象
本文档为技术文档，需要阅读者：
* 具备基本的Android开发能力
* 准备接入CC视频的基础SDK相关功能
* CC基础版本SDK，为了使用硬件媒体编解码器，建议API级别19以上。

## 2.开发准备

### 2.1 开发环境
* Android Studio : Android 开发IDE
* Android SDK : Android 官方SDK



## 3.快速集成
基础流程如图：
![Alt](https://doc.bokecc.com/class/developer/android/document/pic.jpg)
注：快速集成主要提供的是推流和拉流的功能(核心功能)。白板、聊天以及排麦组件另有开发文档描述。

首先，需要下载最新版本的SDK，[下载地址]()

### 3.1 第一步：引入远程包

```
//项目根build.gradle增加私有库

repositories {
        // 配置私有仓库地址
        maven {
            url 'http://nexus-app.bokecc.com/repository/sdk-group/'
        }
        ...
}
allprojects {
    repositories {
        // 配置私有仓库地址
        maven {
            url 'http://nexus-app.bokecc.com/repository/sdk-group/'
        }
        ...
    }
}
```

```java
//项目module的build.gradle
dependencies {
    //云课堂sdk引用
    api 'com.bokecc:ClassBaseLib:6.x.x'//基础库
    api 'com.bokecc:docLib:1.x.x'//文档库 
}
```

注意：ccclassroom-base为基础组件包，必须导入。
主要SDK组件如下：

|            名称                            | 描述      |
| :--------------------------------------- | :------- |
| ClassBaseLib	 | CC音视频核心库	|
| docLib.jar	| CC文档核心库	|

#### 3.2第二步：gradle配置

创建java、res 文件夹同等级目录jniLibs ，然后将下载的.so文件复制到该目录，然后配置app的build.gradle，如下代码：
```java
android {
   ...
    defaultConfig {
        ...
        ndk {
            abiFilters "armeabi-v7a","arm64-v8a", "x86"//依据自身需求
        }
    }
   ...

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```


```java
	
```

### 3.3 第三步：快速使用SDK
预览展示控件和订阅展示控件：

#### 3.3.1 初始化SDK并且创建BaseActivity

Application文件的 onCreate 方法添加如下代码：

```java
 /**
     * 初始化
     * @param context  上下文
     * @param isLogOut 是否输出日志到服务器
     */
CCInteractSDK.init(this.getApplicationContext(), true);
```

BaseActivity文件的 onCreate 方法添加如下代码(建议初始化都在基础类进行)：
```java
  mCCAtlasClient = CCAtlasClient.getInstance();//基础SDK 音视频相关
  mCCBarLeyManager = CCBarLeyManager.getInstance();//排麦组件 
  mCCDocViewManager = CCDocViewManager.getInstance();//文档组件
  mCCChatManager = CCChatManager.getInstance();//聊天组件
```

#### 3.3.2 Android6.0需要动态申请权限

参考使用第三方权限库： [permissionsdispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)
```java
必要权限：CAMERA,RECORD_AUDIO,READ_EXTERNAL_STORAGE
```


#### 3.3.3 代码逻辑顺序
* （1）解析房间链接，丛云课堂后台获取
```java
例如：http://cloudclass.csslcloud.net/index/talker/?roomid=587C97AC7426B69C9C33DC5901307461&userid=83F203DAC2468694
解析：
域名：http://cloudclass.csslcloud.net/index/ 
角色：talker（学生CCAtlasClient.TALKER）/presenter(老师CCAtlasClient.PRESENTER)/inspector(隐身者CCAtlasClient.INSPECTOR)/旁听者
roomid：587C97AC7426B69C9C33DC5901307461
userid：83F203DAC2468694

```
* （2） 获取session  
```java
 /** token获取方式暂不确定
     * 获取sessionid接口
     * @param roomId   房间id
     * @param userId   用户id
     * @param role     用户角色 （学生CCAtlasClient.TALKER）
     * @param username 用户名
     * @param password 用户密码
     * @param callBack 回调
     */
 mCCAtlasClient.login(mRoomId,
                    mUserId, mRole, mNickName, mPwd, new CCAtlasCallBack<String>() {});
```

* （3）join房间  

```
		/**
     * 加入直播间
     *
     * @param sessionId  会话id 通过接口获取
     * @param userAcount 用户账号
     * @param areaCode   节点
     * @param isUpdateRtmpLayout  设置是否合流布局里面有学生画面，默认是false有画面，true无画面。
     * @param callBack   {@link CCAtlasCallBack}
     */
    public void join(final String sessionId, final String userAcount, final String areaCode, Boolean isUpdateRtmpLayout,
                     final CCAtlasCallBack<CCInteractBean> callBack) {});
```
**注意**：现在才可以真正的使用其他组件

#### 3.3 .4 CCAtlasClient基础组件快速接入
##### 3.3.4.1  判断是否开播
(1)当第一次进入房间判断是否开启直播（主动获取）
```java
 mCCAtlasClient.isRoomLive()
```
(2)进入房间后监听回调，判断房间开始或者结束（被动监听）
```java
mCCAtlasClient.setOnClassStatusListener(new CCAtlasClient.OnClassStatusListener(){
        @Override
        public void onStart() {//开始上课事件通知
          
        }

        @Override
        public void onStop() {//下课事件通知
        
        }
    });
 
```
##### 3.3.4.2 开始直播与结束直播
老师角色需要该接口，之后才可以推拉流；然而学生角色只需要判断直播状态，参考**3.3.4.1  判断是否开播**
```java
  //开始直播
  mAtlasClient.startLive(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("start live success");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                showToast("start live failed [ " + errMsg + " ]");
            }
        });
  //停止直播     
   mAtlasClient.stopLive(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("stop live success");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                showToast("stop live failed [ " + errMsg + " ]");
            }
        });      
```

##### 3.3.4.3 拉流与停止拉流、

（1）创建并初始化渲染控件CCSurfaceRenderer，并添加到父布局中
```java
RelativeLayout mLocalContainer,mRemoteMixContainer;
CCSurfaceRenderer mLocalRenderer, mRemoteMixRenderer;
private void initRenderer() {
				//本地流
        try {
            mLocalRenderer = new CCSurfaceRenderer(this);
            mCCAtlasClient.initSurfaceContext(mLocalRenderer);
						mLocalRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            //1、创建本地流、渲染本地流,其中本地流样式默认是音视频,ture 是前摄像头、false是后摄像头,
            mCCAtlasClient.createLocalStream(mContext, mCCAtlasClient.getMediaMode(), true);
            mCCAtlasClient.attachLocalCameraStram(mLocalRenderer);
            mLocalContainer.removeAllViews();
            View view = mCCAtlasClient.getSurfaceViewList().get(0);
            mLocalContainer.addView(view != null ? view : mLocalRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
				//远程流
        mRemoteMixRenderer = new CCSurfaceRenderer(this);
        mCCAtlasClient.initSurfaceContext(mRemoteMixRenderer);
        mRemoteMixRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    
    }
```
（2） 拉流 并且将流渲染到控件上
```java
//1、通过监听，控制流订阅
mCCAtlasClient.setOnNotifyStreamListener(new CCAtlasClient.OnNotifyStreamListener() {
            @Override
            public void onStreamAllowSub(SubscribeRemoteStream mStream) {
                //添加流，去订阅
            }

            @Override
            public void onStreamRemoved(SubscribeRemoteStream mStream) {
                //移除流，取消订阅
            }

            @Override
            public void onStreamError() {
                //流错误
            }
        });
//2、刚开始进来调用下面方法，才可以触发订阅回调
mCCAtlasClient.setSubscribeRemoteStreams();
//3、订阅方法
​```java
mCCAtlasClient.SubscribeStream(mStream.getRemoteStream(), new CCAtlasCallBack<CCStream>() {
            @Override
            public void onSuccess(CCStream stream) {
                showToast("subscribe success");
                try {
                    if (stream != null) {
                        stream.attach(mRemoteMixRenderer);
                        SurfaceView view = stream.getSurfaceView();
                        if (view != null)
                            mRemoteMixContainer.addView(view);
                    } else {
                        mStream.attach(mRemoteMixRenderer);
                        mRemoteMixContainer.addView(mRemoteMixRenderer);
                    }
                } catch (StreamException e) {
                    e.printStackTrace();
                }
            }
						@Override
            public void onFailure(int errCode, String errMsg) {
                Log.e(TAG, "subscribe failed [ " + errMsg + " ]");
            }
        });

//4、取消订阅方法 ，最好封装一下
        try {
            if(remoteStream!=null)
            mCCAtlasClient.unSubscribeStream(remoteStream.getRemoteStream(), new 			CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                		dismissProgress();
                		showToast("unsubscribe success");
                    try {
                       remoteStream.getRemoteStream().detach(mRemoteMixRenderer);
                    } catch (StreamException e) 
                       e.printStackTrace();
                    } finally {
                        mRemoteMixRenderer.cleanFrame();
                    }
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
										dismissProgress();
                		showToast("unsubscribe failed [ " + errMsg + " ]");
                }
            });
        } catch (StreamException e) {
            e.printStackTrace();
        }
```
##### 3.3.4.4 推流与停止推流
```java
//1、创建本地流、渲染本地流
//ture 是前摄像头、false是后摄像头
 mCCAtlasClient.setCameraType(true);
 mCCAtlasClient.setAppOrientation(isPortrait);//是否是竖屏
//其中本地流样式默认是音视频
 mCCAtlasClient.createLocalStream(mCCAtlasClient.getMediaMode());
 mCCAtlasClient.attachLocalCameraStram(mLocalRenderer);
//2、推流 成功过 true 是不要合流、false  是要合流（合流需要开通账号权限）
 mCCAtlasClient.publish(false, new CCAtlasCallBack<Void>() {});
//3、停止推流
  mCCAtlasClient.unpublish(new CCAtlasCallBack<Void>() {});
```
##### 3.3.4.5 离开房间调用
离开房间一定要调用以下方法：
​```java
 mCCAtlasClient.leave(null);
```
##### 3.3.4.6 切换摄像头
​```java
 mAtlasClient.switchCamera(new CCAtlasCallBack<Boolean>() {});
```
##### 3.3.4.7 音视频开发完成，其他相关回调，请查阅回调文档

#### 3.3.5 CCDocViewManager文档组件快速接入
##### 3.3.5.1 布局
```java
 <RelativeLayout
            android:id="@+id/id_lecture_doc_area"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#ffffff"
            android:clickable="true">

            <com.bokecc.ccdocview.DocWebView
                android:id="@+id/id_lecture_docppt_display"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:visibility="gone"/>

            <com.bokecc.ccdocview.DocView
                android:id="@+id/id_lecture_doc_display"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>
 </RelativeLayout>               
```
##### 3.3.5.2 初始化
```java
//1、初始化自定义控件 ：DocWebView 文档相关；DocView 画笔，以及组件SDK
 mCCDocViewManager = CCDocViewManager.getInstance();//文档组件
//2、设置画笔 isInterceptor true可以用画笔，用户角色CCAtlasClient.PRESENTER(老师红色笔)、CCAtlasClient.TALKER（学生蓝色画笔）
mDocView.setTouchInterceptor(boolean isInterceptor, int role)；
//3.设置文档展示界面
mDocViewManager.setDocHistory(mDocView, mDocWebView);
//4.白板与ppt动画的交换
mDocWebView.setDocSetVisibility(mDocView);
mDocView.setDocWebViewSetVisibility(mDocWebView);
//5.设置白板的宽高
ViewGroup.LayoutParams params = idLectureDocArea.getLayoutParams();
int width = DensityUtil.getWidth(this);
int height = width * 16/9;
mDocView.setWhiteboard(width, height,true);
params.height = height;
idLectureDocArea.setLayoutParams(params);//父布局
```
##### 3.3.5.3 画板回调及常用方法
```java
//1、文档加载回调
mDocWebView.setOnDpCompleteListener(new DocWebView.OnDpCompleteListener() {
            @Override
            public void dpLoadComplete(int w, int h) {
                Log.i("wdh--->", "dpLoadComplete: ");
            }

            @Override
            public void dpLoading() {
                Log.i("wdh", "dpLoading: ");
            }
        });
//2、设置为橡皮檫
 mDocView.setEraser(true);
//3、撤销所有人的画笔
 mDocView.teacherUndo();
//4、设置缩放 注意2（橡皮檫）和4不可以同时设为true
mDocView.setGestureAction(true);
```
#### 3.3.6 CCChatManager聊天组件快速接入

```java
//1、初始化聊天组件
CCChatManager chatManager = CCChatManager.getInstance();
//2、开启直播后才可以获取历史数据
 chatManager.getChatHistory(chatMsgHistoryCCChatCallBack);
//监听聊天消息回调
CCChatCallBack<ChatMsgHistory> chatMsgHistoryCCChatCallBack = new CCChatCallBack<ChatMsgHistory>() {
        @Override
        public void onSuccess(ChatMsgHistory chatMsgHistory) {
            ArrayList<ChatPublic> chatPublics = chatMsgHistory.getChatHistoryData();
            for (int i = 0; i < chatPublics.size(); i++) {
                ChatMsg msg = chatPublics.get(i).getMsg();
                CCUser from = chatPublics.get(i).getFrom();
                final ChatEntity chatEntity = new ChatEntity();
                chatEntity.setType(msg.getType());
                chatEntity.setUserId(from.getUserId());
                chatEntity.setUserName(from.getUserName());
                chatEntity.setMsg(msg.getMsg());
                chatEntity.setTime(msg.getTime());
                chatEntity.setUserRole(from.getUserRole());
               //更新聊天列表
            }
        }

        @Override
        public void onFailure(String err) {

        }
    };
//3、监听聊天信息
//监听聊天消息回调
chatManager.setOnChatListener(onChatListener);
CCChatManager.OnChatListener onChatListener = new CCChatManager.OnChatListener() {
        @Override
        public void onReceived(CCUser from, ChatMsg msg, boolean self) {
            final ChatEntity chatEntity = new ChatEntity();
            chatEntity.setType(msg.getType());
            chatEntity.setUserId(from.getUserId());
            chatEntity.setUserName(from.getUserName());
            chatEntity.setMsg(msg.getMsg());
            chatEntity.setTime(msg.getTime());
            chatEntity.setSelf(self);
            chatEntity.setUserRole(from.getUserRole());
          //更新聊天列表
        }

        @Override
        public void onError(String err) {

        }
    };
//4、禁言回调
chatManager.setOnGagListener(onGagListener);
//禁言用户
CCChatManager.OnGagListener onGagListener = new CCChatManager.OnGagListener() {
        @Override
        public void onChatGagOne(final String userid, final boolean isAllowChat) {
         //禁言个人
        }

        @Override
        public void onChatGagAll(final boolean isAllowChat) {
         //禁言全部
        }
    };
//5、发送消息
content = chatManager.transformMsg(content); chatManager.sendMsg(content);

//6、发送图片（建议压缩上传，详情参考Demo）
chatManager.updatePic1(file);
```

#### 3.3.7 CCBarLeyManager排麦组件快速接入（根据业务需求添加排麦组件）

```java
//1、初始化基础类
barLeyManager = CCBarLeyManager.getInst
ance();

//3、监听麦序的状态；接收状态013，对应自由连麦、举手、自动连麦
barLeyManager.setOnSpeakModeUpdateListener(onLianmaiModeUpdateListener);
  CCBarLeyManager.OnSpeakModeUpdateListener onLianmaiModeUpdateListener = new CCBarLeyManager.OnSpeakModeUpdateListener() {
        @Override
        public void onBarLeyMode(final int mode) {
          
        }
    };
//4、监听麦序的更新；自由连麦和举手连麦会有麦序的更新
 barLeyManager.setOnQueueMaiUpdateListener(onQueueMaiUpdateListener);
  CCBarLeyManager.OnQueueMaiUpdateListener onQueueMaiUpdateListener = new CCBarLeyManager.OnQueueMaiUpdateListener() {
        @Override
        public void onUpdateBarLeyStatus(ArrayList<CCUser> users) {
       
        }
    };
//5、监听上下麦
 barLeyManager.setOnNotifyMaiStatusLisnter(onNotifyMaiStatusLisnter);
 CCBarLeyManager.OnNotifyMaiStatusLisnter onNotifyMaiStatusLisnter = new CCBarLeyManager.OnNotifyMaiStatusLisnter() {
        @Override
        public void onUpMai(int oldStatus) {
           //上麦后去推流
        }

        @Override
        public void onDownMai() {
           //停止推流
        }
    };
//6、监听邀请状态（监听web端老师对学生的邀请）
  barLeyManager.setOnNotifyInviteListener(onNotifyInviteListener);
   CCBarLeyManager.OnNotifyInviteListener onNotifyInviteListener = new CCBarLeyManager.OnNotifyInviteListener() {
        @Override
        public void onInvite() {
          //接收老师邀请，可以上麦，也可以拒绝，建议写个AlertDialog
          // barLeyManager.acceptTeacherInvite(new CCBarLeyCallBack<Void>() {});
          // barLeyManager.refuseTeacherInvite(new CCBarLeyCallBack<Void>() {});
        }

        @Override
        public void onCancel() {
           //老师取消邀请
        }
    };
//7、踢出房间
barLeyManager.setOnKickOutListener(new CCBarLeyManager.OnKickOutListener() {
            @Override
            public void onKickOut() {
                showToast("对不起，您已经被踢出该直播间");
                finish();
                ccAtlasClient.leave(null);
            }
        });

```
##  4.混淆配置
ccclassroom-base.jar已经混淆过，如果需要对应用进行混淆，需要在混淆的配置文件增加如下代码，以防止SDK的二次混淆：
```java
-keep public class com.bokecc.sskt.base.**{*;}
-keep public interface com.bokecc.sskt.base.**{*;}
-keep public class com.intel.webrtc.base.**{*;}
-keep public interface com.intel.webrtc.base.**{*;}
-keep public class com.intel.webrtc.conference.**{*;}
-keep public interface com.intel.webrtc.conference.**{*;}
-keep public class org.webrtc.**{*;}
-keep public interface org.webrtc.**{*;}
-keep class io.agora.**{*;}
-keep class com.zego.**{*;}
```
## 5.详细API查询
[API查询](https://doc.bokecc.com/class/developer/android/document/sdk.html)

## 6.Q&A
### 6.1 暂无