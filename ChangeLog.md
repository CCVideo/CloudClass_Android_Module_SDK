

[TOC]

# 《组件化SDK变更说明》
# version_3.6.0
> 版本 3.6.0   时间：2018-3-29

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



