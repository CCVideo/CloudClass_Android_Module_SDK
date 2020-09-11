[TOC]
该文档主要说明文档库管理类 CCDocViewManager 的主要 API
该文档主要说明CCDocViewManager类外部常用接口，内部使用接口，此处不做说明介绍；

# CCDocViewManager


## 一、概览
### API 汇总

|  | 返回值|          函数名                                                    |
| -------------- | -------------: | :----------------------------------------------------------- |
| 1 | 	CCDocViewManager | [getInstance()](# 1.getInstance) |
| 2 | 	void | [addInteractListeners()](# 2.addInteractListeners) |
| 3 | 	void | [setDocHistory(DocView docView, DocWebView docWebView)](# 3.setDocHistory) |
| 4 | 	void | [getRoomDocs( String roomid, final AtlasCallBack<RoomDocs> callBack)](# 4.getRoomDocs) |
| 5 | 	void |[getRoomDoc( String roomId,  String docId, final AtlasCallBack<DocInfo> callBack) ](# 5.getRoomDoc)|
| 6 |	void |[ delDoc(String roomId, String docId, final AtlasCallBack<Void> callBack) ](# 6.delDoc)|
| 7 |	boolean |[ docPageChange(String docid, String fileName, int totalPage, String url,                              boolean usesdk, int curPage, int mode, int width, int height) ](# 7.docPageChange)|
| 8 |	boolean |[ pptAnimationChange(String docid, int step, int curPage) ](# 8.pptAnimationChange)|
| 9 |	void | [setOnTalkerAuthDocListener(OnTalkerAuthDocListener onTalkerAuthDocListener)](# 9.setOnTalkerAuthDocListener)|
| 10 |	boolean | [authUserTeacher( String userId) ](# 10.authUserTeacher)|
| 11 |	boolean | [cancleAuthUserTeacher( String userId)](# 11.cancleAuthUserTeacher) |
| 12 |	boolean |[ authUserDraw( String userId) ](# 12.authUserDraw)|
| 13 |	boolean |[ cancleAuthUserDraw( String userId) ](# 13.cancleAuthUserDraw)|
| 14 |	void | [setDocPageChangeListener(OnDocPageChangeListener onDocPageChangeListener) ](# 14.setDocPageChangeListener)|
| 15 |	void | [release() ](# 15.release)|


## 二、接口介绍

### 1.getInstance

```java
public static CCDocViewManager getInstance();
```

 1.作用
 	获取文档库操作类实例对象；

 2.注意
	不再使用的时候需要调用 release 方法释放资源

 3.参数
	无

 4.返回
	CCDocViewManager

 5.异常
	无

### 2.addInteractListeners

```java
public void addInteractListeners() ；

```

 1.作用
 	添加文档相关监听；

 2.注意
	无

 3.参数

​	无

 4.返回
	无

 5.异常
 	无


### 3.setDocHistory

```java
public void setDocHistory(DocView docView, DocWebView docWebView);

```

 1.作用
 	关联文档相关控件；

 2.注意
	使用DocView和DocWebView 的实例对象。

 3.参数

| 参数名     | 类型       | 说明            |
| ---------- | ---------- | --------------- |
| docView    | DocView    | DocView 画板    |
| docWebView | DocWebView | DocWebView 文档 |

 4.返回
	无

 5.异常
 	无


### 4.getRoomDocs

```java
public void getRoomDocs( String roomid, final AtlasCallBack<RoomDocs> callBack)

```

 1.作用
 	获取直播间文档；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| roomid | String     | 房间 id |
| callBack | AtlasCallBack     | 回调 |

 4.返回
	无

 5.异常
	无
	

### 5.getRoomDoc

```java
public void getRoomDoc( String roomId,  String docId, final AtlasCallBack<DocInfo> callBack)

```

 1.作用
 	获取指定文档；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| roomId | String    | 直播间id |
| docId | String | 文档id |
| callBack | AtlasCallBack | 回调 |


 4.返回
	无

 5.异常
	无
	
### 6.delDoc

```java
public void delDoc(String roomId, String docId, final AtlasCallBack<Void> callBack) 

```

 1.作用
 	删除文档；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| roomId | String | 直播间 id |
| docId | String | 文档id |
| callBack | AtlasCallBack | 回调 |


 4.返回
	无

 5.异常
	无
	
	
### 7.docPageChange

```java
public boolean docPageChange(String docid, String fileName, int totalPage, String url,
                                 boolean usesdk, int curPage, int mode, int width, int height)

```

 1.作用
 	设置文档翻页方法；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| docid | String     | 文档id |
| fileName | String | 文档名称 |
| totalPage | int | 文档总页数 |
| url | String | 当前文档页URL，白板的话为"#" |
| usesdk | boolean | 文档模式，早期参数 |
| curPage | int | 当前页码 |
| mode | int | 文档模式 |
| width | int | width |
| height | int | height |

 4.返回
	 true 正常发送 false 发送失败

 5.异常
	无
	
	
### 8.pptAnimationChange

```java
public boolean pptAnimationChange(String docid, int step, int curPage)

```

 1.作用
 	文档ppt动画处理；

 2.注意
	无

 3.参数

| 参数名  | 类型   | 说明          |
| ------- | ------ | ------------- |
| docid   | String | 文档id        |
| step    | int    | ppt动画的步数 |
| curPage | int    | 当前页        |

 4.返回

boolean  true 正常发送 false 发送失败

 5.异常
	无
	

### 9.setOnTalkerAuthDocListener

```java
public void setOnTalkerAuthDocListener(OnTalkerAuthDocListener onTalkerAuthDocListener)

```

 1.作用
 	老师授权学生权限操作文档监听回调通知（含授权标注和设为讲师功能）

 2.注意
 	

```java
public interface OnTalkerAuthDocListener {
    /**
     * 授权标注通知
     *
     * @param userid      当前被老师操作的用户id
     * @param isAllowDraw <ul><li>true授权标注</li><li>false取消授权</li></ul>
     */
    void onAuth(String userid, boolean isAllowDraw);

    /**
     * 设为讲师状态通知
     *
     * @param userid              当前被老师操作的用户id
     * @param isAllowsetupteacher <ul><li>true设为讲师</li><li>false取消设为讲师</li></ul>
     */
    void onSetTeacherStatus(String userid, boolean isAllowsetupteacher);

    /**
     * 设为讲师翻页监听
     */
    void onSetTeacherToPage(DocInfo mDocInfo, int position);

    /**
     * 设为讲师文档发生变化通知
     */
    void onSetTeacherToDoc(DocInfo mDocInfo, int position);

    /**
     * 学生被设为讲师以后，老师需要去监听学生的翻页事件
     *
     * @param position 当前被老师操作的用户id
     */
    void onTeacherToTalkerAuth(int position);
}
```

 3.参数

| 参数名                  | 类型                    | 说明   |
| ----------------------- | ----------------------- | ------ |
| onTalkerAuthDocListener | OnTalkerAuthDocListener | 文档id |

 4.返回
	无

 5.异常
	无
	
### 10.authUserTeacher

```java
public boolean authUserTeacher( String userId)

```

 1.作用
 	设为讲师

 2.注意
 	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| userId | String     | 指定用户id |

 4.返回
	boolean  true 正常发送 false 发送失败

 5.异常
	无
	
	
### 11.cancleAuthUserTeacher

```java
public boolean cancleAuthUserTeacher( String userId) 

```

 1.作用
 	取消设为讲师

 2.注意
	无

 3.参数

| 参数名 | 类型   | 说明       |
| ------ | ------ | ---------- |
| userId | String | 指定用户id |

 4.返回
	boolean  true 正常发送 false 发送失败

 5.异常
	无
	
### 12.authUserDraw

```java
public boolean authUserDraw( String userId) {
```

 1.作用
 	授权学生绘制；

 2.注意
	无

 3.参数

| 参数名 | 类型   | 说明       |
| ------ | ------ | ---------- |
| userId | String | 指定用户id |

 4.返回
	boolean  true 正常发送 false 发送失败

 5.异常
	无
	
	
### 13.cancleAuthUserDraw

```java
public boolean cancleAuthUserDraw( String userId)

```

 1.作用
 	取消授权学生绘制；

 2.注意
	无

 3.参数

| 参数名 | 类型   | 说明       |
| ------ | ------ | ---------- |
| userId | String | 指定用户id |

 4.返回
	boolean  true 正常发送 false 发送失败

 5.异常
	无
	
	

### 14.setDocPageChangeListener

```java
 public void setDocPageChangeListener(OnDocPageChangeListener onDocPageChangeListener)
```

 1.作用
 	设置翻页监听器；

 2.注意
	

```java
public interface OnDocPageChangeListener {
    void onDocPageChange(String docID);
}
```

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| onDocPageChangeListener | OnDocPageChangeListener | 监听回调 |

 4.返回
	无

 5.异常
	无
	
	
### 15.release

```java
public void release()

```

 1.作用
 	释放文档相关资源；

 2.注意
	当文档不再使用，不如页面退出时调用

 3.参数

​	无

 4.返回
	无

 5.异常
	无
	







