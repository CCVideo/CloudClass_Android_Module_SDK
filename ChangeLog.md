[TOC]

# 《组件化SDK变更说明》
# version_3.1.0
> 版本 3.1.0   时间：2018-09-12

### 一、新增功能

1、增加了排麦、举手、进入房间提醒

2、支持橡皮檫，激光笔，荧光笔

3、直播时间

### 二、修改API

### 三、新增API


[TOC]

# 《组件化SDK变更说明》
# version_3.2.0
> 版本 3.2.0   时间：2018-11-20

### 一、新增功能
1、老师可以撤销所有人画笔；
2、增加助教角色；
3、增加文档以及人员列表同步；

### 二、修改API
无

### 三、新增API

1、Docview类
```objc
/** 老师撤销所有人画笔 */
public void teacherUndo()
```
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
[TOC]


# 《组件化SDK变更说明》
# version_3.4.0
> 版本 3.4.0   时间：2019-1-23

### 一、新增功能
1、添加文档回调成功接口

如果已经对接上个版本的sdk（3.3.0），只需要替换ccclassroom-docmodule.jar包就可以。

文档请参照《白板与文档插件.md》或者是本文档的说明《新增API》

### 二、修改API
无

### 三、新增API

1、设置文档加载回调的监听api接口

```
 public void setOnDpCompleteListener(OnDpCompleteListener mOnDpCompleteListening) 
```

2、使用的案例

其中mDocWebView，是自定义控件DocWebView的对象

```
mDocWebView.setOnDpCompleteListering(new DocWebView.OnDpCompleteListening (){

            @Override
            public void dpAnimateLoadComplete(int w, int h) {
                //ppt极速动画加载完成、切换、翻页回调
            }
            @Override
            public void dpImageLoadComplete(int w, int h) {
                //静态文档、图片加载完成、切换、翻页回调
            }
        });
```

[TOC]

# 《组件化SDK变更说明》
# version_3.5.0
> 版本 3.5.0   时间：2019-3-25

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

[TOC]

# 《组件化SDK变更说明》
# version_3.6.0
> 版本 3.6.0   时间：2019-3-29

### 一、新增功能
1、优化节点调度

2、节点网络探测

### 二、修改API
1、dispatch接口

```

     /**
     * 城市节点接口
     * @param callBack 回调
     */

    public void dispatch(final CCAtlasCallBack<CCCityListSet> callBack) {
```

2、 CCCityListSet工具类的说明：

```

     //获取节点列表
	public ArrayList<CCCityInteractBean> getLiveListSet() {
		
	//CCCityInteractBean工具类的说明
	public class CCCityInteractBean implements Serializable {
   		 //节点区域
   		 private String mDataAreaCode;
  		 //d节点城市名称
   		 private String mDataLoc;
   		 //节点的延迟
    	 private String pingTime;
	}

```

### 三、新增API
无


[TOC]

# 《组件化SDK变更说明》
# version_3.7.0
> 版本 3.7.0   时间：2019-4-29

### 一、新增功能
1、断网重连方法
2、流媒体的升级

### 二、修改API
1、推流方法

```

    /**
     * 推流
     * ismix，默认是false推合流，true不推合流
     */

     public synchronized void publish(boolean isMix, final CCAtlasCallBack<Void> callBack) {
```

### 三、新增API
1、重连方法

```

    /**
     * 重连函数
     */

    public void streamServerReConnect(final CCAtlasCallBack<String> callBack) {

```
断网超过15秒后，监听这个事件，一旦事件触发，并且15秒后网连接上了，这时候需要调用重连方法。
```

     /**
     * 设置流服务断开监听事件
     */
    public void setOnAtlasServerListener(OnAtlasServerListener onAtlasServerListener) {
        mOnAtlasServerListener = onAtlasServerListener;
    }

    /**
     * 流服务断开监听接口
     */
    public interface OnAtlasServerListener {
        /**
         * 流服务断开通知
         */
        void onAtlasServerDisconnected();
    }

```

[TOC]

# 《组件化SDK变更说明》
# version_3.8.0
> 版本 3.8.0   时间：2019-5-17

### 一、新增功能

1、添加ppt文档动画回调接口

2、添加自定义字段接口

3、修复人员状态不同的问题。

### 二、修改API
无

### 三、新增API
1、更新自定义字段接口

```

     /**
     * 更新自定义字段方法（status只能传0~99,并且是int型的）
     * @param status
     */
    public void updateCustomStatus(int status, final CCAtlasCallBack<String> callBack){

```
监听更新字段的事件方法
```

     /**
     * 设置更新custom监听方法
     * @param customStatus
     */
    public void setUpdateCustomStatus(CustomCallback customStatus){
        mCustomCallback = customStatus;
    }

    public interface CustomCallback {
    /**
     * 更新custom状态
     * @param status
     */
    void customStatusUpdated(CCCustomBean status);
    }

```

2、文档ppt动画翻页监听事件

```

     /**
     * 设置文档回调监听方法
     * @param customStatus
     */
  	 public void setOnDpCompleteListener(OnDpCompleteListener mOnDpCompleteListening) {
        this.mOnDpCompleteListening = mOnDpCompleteListening;
    }
	//文档回调监听事件
    public interface OnDpCompleteListener {
		//文档加载完成回调，以及普通ppt翻页事件也会回调
        void dpLoadComplete(int w, int h);
		//文档加载中回调
        void dpLoading();
		//文档ppt动画翻页回调
        void dpAnimationChange(int index);
    }

```

[TOC]

# 《组件化SDK变更说明》
# version_4.0.0
> 版本 4.0.0   时间：2019-8-1

### 一、新增功能

1、CCstream对象里面添加了流渲染器，调用subscribe接口的时候，会在CCStream对象里面添加一个surfaceview，客户可以直接用来渲染。如果该渲染器为空，默认还是走的之前的渲染器。如果不为空就用该渲染器去渲染。

### 二、修改API
无

### 三、新增API

```

	/**
	*获取渲染器
	*/
	if(stream!= null && stream.getSurfaceView() != null  ){
			videoStreamView.setSurfaceViewList(stream.getSurfaceView());
	 }

```

