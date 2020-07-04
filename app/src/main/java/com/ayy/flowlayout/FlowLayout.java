package com.ayy.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import static android.widget.FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY;

/**
 * @author anyanyan
 * 流式布局，热标签自动换行
 * 样式如下：
 * -- - --- --
 * ---- -- -
 * -------  --
 * -- -
 */
public class FlowLayout extends ViewGroup {
    /**
     * 所有行
     */
    private List<List<View>> allLineViews;
    /**
     * 每行的高度
     */
    private List<Integer> allLineHeights;

    /**
     * 最大行数 -1为不控制
     * 控制最多显示几行
     */
    private int maxLine = -1;
    /**
     * 水平间距
     */
    private int horizontalSpace;
    /**
     * 垂直间距
     */
    private int verticalSpace;


    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        maxLine = typedArray.getInt(R.styleable.FlowLayout_max_line, -1);
        horizontalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_horizontal_space, 0);
        verticalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_vertical_space, 0);
        typedArray.recycle();
    }

    /**
     * 初始化或者清空数据
     */
    private void initOrClearData() {
        if (allLineViews == null) {
            allLineViews = new ArrayList<>();
        }
        allLineViews.clear();
        if (allLineHeights == null) {
            allLineHeights = new ArrayList<>();
        }
        allLineHeights.clear();
    }


    /**
     * 测量，计算总高度和宽度，并保存每一行的高度和每一行的view
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initOrClearData();
        int selfNeedWidth = 0;
        int selfNeedHeight = 0;
        int selfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);
        int lineNum = 0;
        //每行的宽度
        int lineWidth = 0;
        //每行的高度
        int lineHeight = 0;
        int childCount = getChildCount();
        List<View> lineViews = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams childLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = 0;
            int childHeight = 0;
            int visibility = childView.getVisibility();
            if (visibility != GONE) {
                int childWithMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        getPaddingStart() + getPaddingEnd(),
                        childLayoutParams.width);
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        getPaddingTop() + getPaddingBottom(),
                        childLayoutParams.height);

                childView.measure(childWithMeasureSpec, childHeightMeasureSpec);

                childWidth = childView.getMeasuredWidth()
                        + childLayoutParams.leftMargin
                        + childLayoutParams.rightMargin;
                childHeight = childView.getMeasuredHeight()
                        + childLayoutParams.topMargin
                        + childLayoutParams.bottomMargin;
            }

            //需要换行
            if (lineWidth + childWidth > selfWidth) {
                allLineViews.add(lineViews);
                allLineHeights.add(lineHeight);
                selfNeedWidth = Math.max(selfNeedWidth, lineWidth);
                selfNeedHeight += lineHeight + verticalSpace;
                //判断行数控制条件,达到行数时，直接跳出循环，不再测量剩下的子View
                lineNum++;
                if (maxLine > 0 && lineNum >= maxLine) {
                    break;
                }
                lineViews = new ArrayList<>();
                lineWidth = 0;
                lineHeight = childHeight;
            }
            //不需换行，累加数据
            lineViews.add(childView);
            lineHeight = Math.max(lineHeight, childHeight);
            lineWidth += childWidth + horizontalSpace;

            //处理最后一行
            if (i == childCount - 1) {
                allLineViews.add(lineViews);
                allLineHeights.add(lineHeight);
                selfNeedWidth = Math.max(selfNeedWidth, lineWidth);
                selfNeedHeight += lineHeight;
            }
        }
        selfNeedHeight = selfNeedHeight + getPaddingTop() + getPaddingBottom();
        //测量完成后，设置宽高
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //确切大小，不实用测量的高度
        if (widthMode == MeasureSpec.EXACTLY) {
            selfNeedWidth = selfWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            selfNeedHeight = selfHeight;
        }
        setMeasuredDimension(selfNeedWidth, selfNeedHeight);
    }

    /**
     * 布局子view
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //遍历所有行数据
        int currentTop = getPaddingTop();
        for (int i = 0; i < allLineViews.size(); i++) {
            List<View> lineViews = allLineViews.get(i);
            int currentLeft = getPaddingLeft();
            int lineHeight = allLineHeights.get(i);
            //遍历每一行数据
            for (int j = 0; j < lineViews.size(); j++) {
                View childView = lineViews.get(j);
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                int left = currentLeft + layoutParams.leftMargin;
                if (j != 0) {
                    left += horizontalSpace;
                }

                int right = left + childView.getMeasuredWidth();
                int gravity = layoutParams.gravity;
                int top;
                switch (gravity) {
                    case Gravity.CENTER_VERTICAL:
                        top = currentTop + (lineHeight - childView.getMeasuredHeight()) / 2
                                + layoutParams.topMargin - layoutParams.bottomMargin;
                        break;
                    default:
                        top = currentTop + layoutParams.topMargin;
                        break;
                }
                int bottom = top + childView.getMeasuredHeight();
                childView.layout(left, top, right, bottom);
                currentLeft = right + layoutParams.rightMargin;
            }
            currentTop += allLineHeights.get(i)+verticalSpace;
        }
    }

    /**
     * 子view无LayoutParams时调用
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * 检查layoutParams不合法时，调用此方法转换为合法的LayoutParams
     */
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = UNSPECIFIED_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.FlowLayout_Layout);
            gravity = typedArray.getInt(R.styleable.FlowLayout_Layout_android_layout_gravity, UNSPECIFIED_GRAVITY);
            typedArray.recycle();

        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
