package com.bokecc.room.ui.model;

/**
 * 作者 ${CC视频}.<br/>
 */

public class MyEBEvent {
    public int what;
    public Object obj;
    public Object obj2; // 扩展一个字段
    public Object obj3; // 扩展一个字段

    public MyEBEvent() {
    }

    public MyEBEvent(int what) {
        this.what = what;
    }

    public MyEBEvent(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public MyEBEvent(int what, Object obj, Object obj2) {
        this.what = what;
        this.obj = obj;
        this.obj2 = obj2;
    }

    public MyEBEvent(int what, Object obj, Object obj2, Object obj3) {
        this.what = what;
        this.obj = obj;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }
}
