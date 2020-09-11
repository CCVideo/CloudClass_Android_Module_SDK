[TOC]
该文档主要说明文档画笔控件DocView 的主要 API
该文档主要说明DocView类外部常用接口，内部使用接口，此处不做说明介绍；

# DocView


## 一、概览
### API 汇总

|  | 返回值|          函数名                                                    |
| -------------- | -------------: | :----------------------------------------------------------- |
| 1 | void | [setDocWebViewSetVisibility()](# 1.setDocWebViewSetVisibility) |
| 2 | 	void | [reset()](# 2.reset) |
| 3 | 	void | [setNoInterceptor(boolean noInterceptor)](# 3.setNoInterceptor) |
| 4 | 	void | [setTouchInterceptor(boolean isNoInterceptor, int role)](# 4.setTouchInterceptor) |
| 5 | 	void |[setRolePaint(int role) ](# 5.setRolePaint)|
| 6 |	boolean |[ isWhiteboard() ](# 6.isWhiteboard)|
| 7 |	void |[ setGestureAction(boolean isGesture)](# 7.setGestureAction)|
| 8 |	void |[ setStrokeWidth(float width)](# 8.setStrokeWidth)|
| 9 |	void | [setColor(int color, int colorStr)](# 9.setColor) |
| 10 |	void | [undo() ](# 10.undo) |
| 11 |	void | [teacherUndo()](# 11.teacherUndo) |
| 12 |	void |[ setWhiteboard(int width, int height, boolean isWhiteBoard)](# 12.setWhiteboard)|
| 13 |	void |[ setDocFrame(int width,int height,int mode) ](# 13.setDocFrame)|
| 14 |	void | [setDocBackground(String url, int currentPage, String currentDocId, String currentFileName) ](# 14.setDocBackground) |
| 15 |	void | [rotate(boolean isRotate) ](# 15.rotate) |
| 16 |	void |[clear() ](# 16.clear) |
| 17 |	void |[clearAll()](# 17.clearAll) |
| 18 |	int | [getDocWidth()](# 18.getDocWidth) |
| 19 |	int | [getDocHeight() ](# 19.getDocHeight) |
| 20 |	void | [recycle() ](# 20.recycle) |
| 21 |	void | [setColor(String color)](# 21.setColor) |

## 二、接口介绍

### 1.setDocWebViewSetVisibility

```java
public void setDocWebViewSetVisibility(DocWebView mDocWebView) ;
```

 1.作用
 	关联文档ppt控件；

 2.注意
	无

 3.参数

| 参数名      | 类型       | 说明        |
| ----------- | ---------- | ----------- |
| mDocWebView | DocWebView | 文档ppt控件 |

 4.返回
	无

 5.异常
	无

### 2.reset

```java
public void reset() ；

```

 1.作用
 	重置画板参数；

 2.注意
	重置后需要再次授权才能绘制

 3.参数

​	无

 4.返回
	无

 5.异常
 	无


### 3.setNoInterceptor

```java
public void setNoInterceptor(boolean noInterceptor)

```

 1.作用
 	设置是否不拦截绘制操作；

 2.注意
	无

 3.参数

| 参数名        | 类型    | 说明                                  |
| ------------- | ------- | ------------------------------------- |
| noInterceptor | boolean | noInterceptor true 不拦截，false 拦截 |

 4.返回
	无

 5.异常
 	无


### 4.setTouchInterceptor

```java
public void setTouchInterceptor(boolean isNoInterceptor, int role)

```

 1.作用
 	初始化角色设置，包括默认画笔颜色及是否不拦截绘制；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| isNoInterceptor | boolean | true 不拦截，false 拦截 |
| role | int  | 回调 |

 4.返回
	无

 5.异常
	无
	

### 5.setRolePaint

```java
public void setRolePaint(int role)

```

 1.作用
 	设置角色的画笔颜色，此为初始化设置，通常调用setTouchInterceptor方法即可；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| role | int | 直播间id |

 4.返回
	无

 5.异常
	无
	
### 6.isWhiteboard

```java
public boolean isWhiteboard()

```

 1.作用
 	判断当前是不是白板；

 2.注意
	无

 3.参数

​	无

 4.返回
	boolean true 是白板， false 不是白板

 5.异常
	无
	
	

### 7.setGestureAction

```java
public void setGestureAction(boolean isGesture) 
```

 1.作用
 	设置是否支持手势放大缩放文档；

 2.注意
	无

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| isGesture | boolean | true 支持手势 false 不支持 |

 4.返回
	无

 5.异常
	无
	
	
### 8.setStrokeWidth

```java
public void setStrokeWidth(float width) 

```

 1.作用
 	设置画笔线宽；

 2.注意
	无

 3.参数

| 参数名 | 类型  | 说明 |
| ------ | ----- | ---- |
| width  | float | 线宽 |

 4.返回

​	无

 5.异常
	无
	

### 9.setColor

```java
 public void setColor(int color, int colorStr) 

```

 1.作用
 	设置颜色， 

 2.注意
 	此外还是一个参数设置颜色的方法 ，参考 21 setColor（String color）

 3.参数

| 参数名   | 类型 | 说明                      |
| -------- | ---- | ------------------------- |
| color    | int  | 颜色色值                  |
| colorStr | int  | 十六进制颜色对应的 int 值 |

 4.返回
	无

 5.异常
	无
	

### 10.undo

```java
 public void undo() 

```

 1.作用
 	撤销画笔，只撤销自己的画笔，通常学生使用

 2.注意
 	无

 3.参数

 	无

 4.返回
     无

 5.异常
	无
	
	
### 11.teacherUndo

```java
public void teacherUndo()
```

 1.作用
 	老师撤销学生以及自己的画笔，不受限制

 2.注意
	无

 3.参数

​	无

 4.返回
	无

 5.异常
	无
	
### 12.setWhiteboard

```java
public void setWhiteboard(int width, int height, boolean isWhiteBoard)
```

 1.作用
 	初始化文档区大小

 2.注意
	此方法为老版本文档设置宽高方法，可满足仅用窗口适配模式下文档处理。如果有宽度适配需求建议使用mDocView.setDocFrame(width,height,mode); 

 3.参数

| 参数名       | 类型    | 说明           |
| ------------ | ------- | -------------- |
| width        | int     | 文档区宽       |
| height       | int     | 文档区高       |
| isWhiteBoard | boolean | 是否默认是白板 |

 4.返回
	无

 5.异常
	无
	
	
### 13.setDocFrame

```java
public void setDocFrame(int width,int height,int mode)

```

 1.作用
 	设置文档区宽高的方法

 2.注意

​	此方法为新版本文档宽高的设置方法，如果老版本方法项目需求可以继续使用，可以忽略此方法

​	setWhiteboard 方法是基础模式（适应窗口模式）下设置宽高的方法， setDocFrame 是基础模式（宽度适配模式）下设置宽高的方法（宽度适配模式可以切换是否是宽度适配）

基础模式切换通过CCDocViewManager#setDocHistory (IDocView docView, IPPTView pptView,int mode) 方法设置，基础模式只能选一种

 3.参数

| 参数名 | 类型 | 说明                                                         |
| ------ | ---- | ------------------------------------------------------------ |
| width  | int  | 文档区宽                                                     |
| height | int  | 文档区高                                                     |
| mode   | int  | DOC_MODE_FIT（1） 适应窗口 ， DOC_MODE_FILL_WIDTH（2） 宽度适配 |

 4.返回
	无

 5.异常
	无
	

### 14.setDocBackground

```java
public void setDocBackground(String url, int currentPage, String currentDocId, String currentFileName)
```

 1.作用
 	设置文档相关信息

 2.注意
	url 当前图片地址，此参数并未使用

 3.参数

| 参数名   | 类型            | 说明   |
| -------- | --------------- | ------ |
| url | String | 当前图片地址，此参数并未使用 |
| currentPage | int | 当前页 |
| currentDocId | String | 当前文档 id |
| currentFileName | String | 当前文档名称 |

 4.返回
	无

 5.异常
	无
	
	

### 15.rotate

```java
public void rotate(boolean isRotate)

```

 1.作用
 	文档横竖屏切换使用；

 2.注意
	此方法为老版本文档方法，已不推荐使用

 3.参数

| 参数名   | 类型    | 说明     |
| -------- | ------- | -------- |
| isRotate | boolean | isRotate |

 4.返回
	无

 5.异常
	无

### 16.clear

```java
 public void clear()
```

 1.作用
 	清除当前页view的绘图信息；

 2.注意
	无

 3.参数

​	无

 4.返回
	无

 5.异常
	无	

### 17.clearAll

```java
public void clearAll()

```

 1.作用
 	清除所有本地画笔数据；

 2.注意
	此方法在课程结束时调用

 3.参数

​	无	

 4.返回
	无

 5.异常
	无	

### 18.getDocWidth

```java
public int getDocWidth()

```

 1.作用
 	获取文档原始数据宽度；

 2.注意
	无

 3.参数

​	无

 4.返回
	int 宽度

 5.异常
	无	

### 19.getDocHeight

```java
public int getDocHeight()

```

 1.作用
 	获取文档原始数据高度；

 2.注意
	无

 3.参数

​	无

 4.返回
	int 高度

 5.异常
	无	

### 20.recycle

```java
public void recycle()

```

 1.作用
 	资源回收方法；

 2.注意
	此方法通常不用调用，CCDocViewManager#release 方法内部会调用此方法

 3.参数

​	无

 4.返回
	无

 5.异常
	无	

### 21.setColor

```java
 public void setColor(String color) 

```

 1.作用
 	设置颜色

 2.注意
 	

 3.参数

| 参数名 | 类型   | 说明                  |
| ------ | ------ | --------------------- |
| color  | String | 颜色色值，如"#ff0000" |

 4.返回
	无

 5.异常
	无





