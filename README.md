# FlowLayout
FlowLayout是自定义ViewGroup，实现了流式布局，自动换行的功能，并且可以控制最大行数，设置水平间距，垂直间距和子View垂直居中。
效果图:
![截图](https://img-blog.csdnimg.cn/20200704160439443.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2FueWFueWFuMDc=,size_16,color_FFFFFF,t_70)

使用方法：
### 在布局中声明，并设置自定义属性：
```
   <com.ayy.flowlayout.FlowLayout
        android:id="@+id/flow_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:horizontal_space="10dp" //水平间距
        app:vertical_space="10dp" //垂直间距
        app:max_line="3" //控制行数
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent">
        
    </com.ayy.flowlayout.FlowLayout    
        
```
### 直接在xml中包裹子View

```
    <com.ayy.flowlayout.FlowLayout
        android:id="@+id/flow_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:horizontal_space="10dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:max_line="3"
        app:vertical_space="10dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:text="哈哈" />
    </com.ayy.flowlayout.FlowLayout>
```

### 或者在代码中addView

```
TextView textView = new TextView(this);
flowLayout.addView(textView);
```



