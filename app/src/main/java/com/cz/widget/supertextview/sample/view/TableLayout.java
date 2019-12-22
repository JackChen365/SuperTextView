package com.cz.widget.supertextview.sample.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.cz.widget.supertextview.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2019-12-22 12:09
 * @email bingo110@126.com
 * todo事件
 * 1. 控件元素回收算法需要重新设计
 */
public class TableLayout extends ViewGroup {
    public static final String TAG="TableLayout";
    /**
     * 表格span元素的数据适配器对象
     */
    private TableAdapter adapter;
    /**
     * 当前显示的控件集
     */
    private ArrayList<ViewInfoItem> viewInfoItems =new ArrayList<>();
    /**
     * 缓存管理对象
     */
    private final RecyclerBin recyclerBin=new RecyclerBin();
    /**
     * 分隔线颜色Drawable对象
     */
    private Drawable border;
    /**
     * 分隔线尺寸
     */
    private float borderSize;
    /**
     * 当前表格信息
     */
    private Layout layout;


    /**
     * 设置分隔线drawable对象
     * @param drawable
     */
    public void setTableBorder(@NonNull Drawable drawable) {
        this.border=drawable;
    }

    /**
     * 设置分隔线尺寸
     * @param borderSize
     */
    public void setTableBorderSize(@FloatRange(from = 0) float borderSize) {
        this.borderSize=borderSize;
    }


    public TableLayout(Context context) {
        this(context,null,R.attr.tableLayout);
    }

    public TableLayout(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.tableLayout);
    }

    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        TypedArray typedArray = context.obtainStyledAttributes(null, R.styleable.TableLayout,R.attr.tableLayout,R.style.TableLayout);
        //设置分隔线
        setTableBorder(typedArray.getDrawable(R.styleable.TableLayout_table_border));
        //设置分隔线尺寸
        setTableBorderSize(typedArray.getDimension(R.styleable.TableLayout_table_borderSize, 0f));
        typedArray.recycle();
    }

    /**
     * 设置数据适配器对象
     * @param adapter
     */
    public void setAdapter(TableAdapter adapter) {
        this.adapter = adapter;
        //清空缓存
        recyclerBin.detachAndScrapAttachedViews();
        //每一列长度数组对象
        int rowCount = adapter.getRowCount();
        for(int row=0;row<rowCount;row++) {
            int columnCount = adapter.getColumnCount(row);
            for (int column = 0; column < columnCount; column++) {
                //获取一个控件,如果缓存中己有,从缓存中取,如果没有从适配器中取
                ViewInfoItem viewInfoItem = recyclerBin.getViewInfoItem(row, column);
                //绑定控件信息
                adapter.onBindView(viewInfoItem.view, row, column);
                //添加信息到集合
                viewInfoItems.add(viewInfoItem);
                //将控件添加到视图
                super.addView(viewInfoItem.view, -1, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            }
        }
    }

    /**
     * 获得当前加载所有view
     * @return
     * @hide
     */
    public List<ViewInfoItem> getChildren(){
        return viewInfoItems;
    }

    /**
     * 不允许外围添加
     * @param child
     * @param index
     * @param params
     */
    @Deprecated
    @Override
    public void addView(View child, int index, LayoutParams params) {
    }

    @Override
    @SuppressLint("Range")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //行数
        int rowCount = adapter.getRowCount();
        //当前行宽度数组
        int[] cellRowArray=new int[rowCount];
        int[] cellColumnArray=null;
        //todo待优化,此处不应直接初始化所有控件
        for(int row=0;row<rowCount;row++){
            int columnCount = adapter.getColumnCount(row);
            //当前行宽度计算
            int[] currentRowWidthArray=new int[columnCount];
            //当前行单元格最高高度
            int cellMeasuredHeight=0;
            for(int column=0;column<columnCount;column++){
                //获取一个控件,如果缓存中己有,从缓存中取,如果没有从适配器中取
                ViewInfoItem viewInfoItem = findTableCell(row,column);
                //测试控件
                measureChild(viewInfoItem.view, MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.AT_MOST));
                //当前控件测试测量宽度高度
                int viewMeasuredWidth = viewInfoItem.view.getMeasuredWidth();
                int viewMeasuredHeight = viewInfoItem.view.getMeasuredHeight();
                //记录单元格宽度
                float columnWidth = adapter.getColumnWidth(row, column);
                //如果用户在数据适配器内配置了当前列宽,使用配置宽
                if(0f!=columnWidth){
                    currentRowWidthArray[column]=(int)columnWidth;
                } else {
                    currentRowWidthArray[column]=viewMeasuredWidth;
                }
                //记录本行最大高度控件
                if(cellMeasuredHeight<viewMeasuredHeight){
                    cellMeasuredHeight=viewMeasuredHeight;
                }
            }
            //记录本列最高view尺寸
            cellRowArray[row]=cellMeasuredHeight;
            //记录单元格宽度
            if(null==cellColumnArray){
                cellColumnArray=currentRowWidthArray;
            } else if(cellColumnArray.length==currentRowWidthArray.length){
                //列数相同
                for(int i=0;i<cellColumnArray.length;i++){
                    //记录每一个单元格,最大数
                    if(cellColumnArray[i]<currentRowWidthArray[i]){
                        cellColumnArray[i]=currentRowWidthArray[i];
                    }
                }
            } else {
                //列数不相同,合并操作
                //如果旧的单元格格数小于新的
                int length=Math.max(cellColumnArray.length,currentRowWidthArray.length);
                int[] newArray=new int[length];
                for(int i=0;i<length;i++){
                    //记录每一个单元格,最大数
                    int cellWidthValue=cellColumnArray.length<i ? 0 :cellColumnArray[i];
                    int rowWidthValue=currentRowWidthArray.length<i ? 0 :currentRowWidthArray[i];
                    newArray[i]=Math.max(cellWidthValue,rowWidthValue);
                }
                //替换为新的数据
                cellColumnArray=newArray;
            }
        }
        //每一列所占权重
        float[] weightArray=new float[cellColumnArray.length];
        for(int i=0;i<cellColumnArray.length;i++){
            weightArray[i]=adapter.getColumnWeight(i);
        }
        //表格测量宽度
        int tableMeasuredWidth = sumArray(cellColumnArray,cellColumnArray.length);
        //表格测量高度
        int tableMeasuredHeight = sumArray(cellRowArray,cellRowArray.length);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(MeasureSpec.AT_MOST==widthMode){
            //自己决定大小
            setMeasuredDimension(tableMeasuredWidth,tableMeasuredHeight);
        } else {
            //宽占满,高固定
            setMeasuredDimension(getMeasuredWidth(),tableMeasuredHeight);
            //宽度占满,由权重与固定尺寸瓜分
            cellColumnArray = getHeaderArray(cellColumnArray,weightArray);
            for(ViewInfoItem viewInfoItem :viewInfoItems){
                //再次测量控件
                int cellWidth = cellColumnArray[viewInfoItem.column];
                measureChild(viewInfoItem.view, MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(viewInfoItem.view.getMeasuredHeight(), MeasureSpec.AT_MOST));
            }
        }
        //初始化当前layout对象,用于绘制计算等
        layout=new Layout(cellRowArray,cellColumnArray,weightArray);
    }

    /**
     * 计算出每一列控件排版宽度
     * @return
     */
    private int[] getHeaderArray(int[] columnArray,float[] weightArray){
        float measuredWidth = getMeasuredWidth();
        //计算第一列根据权重决定每一列尺寸
        int firstColumnCount = adapter.getColumnCount(0);
        int[] headerArray=new int[firstColumnCount];
        for (int column = 0; column < firstColumnCount; column++) {
            headerArray[column] = (int) (measuredWidth/firstColumnCount);
            //当前列所占权重
            float weight = weightArray[column];
            //计录出不需要按权重的控件
            if(0==weight){
                //如果尺寸没有被重置,直接应用
                headerArray[column] = columnArray[column];
                measuredWidth-=columnArray[column];
            }
        }
        //计算剩余空间大小
        float totalWeight = 0;
        for(float weight:weightArray){
            totalWeight+=weight;
        }
        for (int column = 0; column < firstColumnCount; column++) {
            float weight = weightArray[column];
            if(0!=weight){
                //获得按权重划分控件宽度
                headerArray[column]= (int) (weight/totalWeight*measuredWidth);
            }
        }
        return headerArray;
    }

    /**
     * 查找一个表格单元格
     * @param row
     * @param column
     */
    private ViewInfoItem findTableCell(int row,int column){
        List<ViewInfoItem> children = getChildren();
        ViewInfoItem findItem=null;
        for(ViewInfoItem item :children){
            if(item.row==row&&item.column==column){
                findItem=item;
                break;
            }
        }
        return findItem;
    }

    /**
     * 排版控件
     * 测量后尺寸后,进行控件排版
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        List<ViewInfoItem> children = getChildren();
        if(null!=children){
            for(ViewInfoItem viewInfo:children){
                View view = viewInfo.view;
                int locationX = layout.getLocationX(viewInfo.column);
                int locationY = layout.getLocationY(viewInfo.row);
                view.layout(locationX,locationY,locationX+view.getMeasuredWidth(),locationY+view.getMeasuredHeight());
            }
        }
    }

    /**
     * 将int array数组相对并返回结果
     * @param array
     * @return
     */
    int sumArray(int[] array,int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += array[i];
        }
        return sum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制分隔线
        drawTableBorder(canvas);
    }


    /**
     * 绘制table分隔线
     * @param canvas
     */
    private void drawTableBorder(Canvas canvas) {
        final Drawable border=this.border;
        if(null!=border){
            int rowCount = layout.row;
            int columnCount = layout.column;
            float borderSize = this.borderSize;
            int offsetX=getPaddingTop();
            int offsetY=getPaddingLeft();
            int width = layout.getLocationX(columnCount);
            int height = layout.getLocationY(rowCount);
            //纵向分隔
            for(int column=0;column<=columnCount;column++){
                int locationX = layout.getLocationX(column);
                if(column==columnCount){
                    border.setBounds((int) (offsetX+locationX-borderSize), offsetY, (offsetX+locationX), (offsetY+height));
                } else {
                    border.setBounds((offsetX+locationX), offsetY,(int)(offsetX+locationX+borderSize), (offsetY+height));
                }
                border.draw(canvas);
            }
            //横向向分隔
            for(int row=0;row<=rowCount;row++){
                int locationY = layout.getLocationY(row);
                if(row==rowCount){
                    border.setBounds(offsetX, (int) (offsetY+locationY-borderSize), (offsetX+width),offsetY+locationY);
                } else {
                    border.setBounds(offsetX,offsetY+locationY, (offsetX+width),(int)(offsetY+locationY+borderSize));
                }
                border.draw(canvas);
            }
        }
    }

    /**
     * 控件信息
     */
    class ViewInfoItem {
        final View view;
        final int viewType;
        final int row;
        final int column;

        public ViewInfoItem(View view, int viewType, int row, int column) {
            this.view = view;
            this.viewType = viewType;
            this.row = row;
            this.column = column;
        }
    }


    /**
     * @author :Created by cz
     * @date 2019-05-17 11:25
     * @email bingo110@126.com
     * 当前table的排版信息对象.可对后期,合并表格单元格信息等操作提供支持
     */
    class Layout {
        /**
         * 当前表格行数
         */
        final int row;
        /**
         * 当前表格列数
         */
        final int column;
        /**
         * 横向单元格尺寸信息
         */
        final int[] rowArray;
        /**
         * 每一列所占空间权重
         */
        final float[] weightArray;
        /**
         * 总权重
         */
        final float totalWeight;
        /**
         * 纵向单元格尺寸信息
         */
        final int[] columnArray;

        final int[] rowCellArray;

        final int[] columnCellArray;

        public Layout(int[] rowArray, int[] columnArray,float[] weightArray) {
            this.row=rowArray.length;
            this.column=columnArray.length;
            this.weightArray=weightArray;
            this.rowArray = rowArray;
            this.columnArray = columnArray;
            //横向纵向+1,是因为初始从0开始一直到最后一个
            this.rowCellArray =new int[rowArray.length+1];
            this.columnCellArray =new int[columnArray.length+1];

            //初始化纵向位置
            int sum = 0;
            for (int i = 0; i <= rowArray.length; i++) {
                rowCellArray[i]=sum;
                if(i<rowArray.length){
                    sum += rowArray[i];
                }
            }
            //初始化横向位置
            sum = 0;
            for (int i = 0; i <= columnArray.length; i++) {
                columnCellArray[i]=sum;
                if(i<columnArray.length){
                    sum += columnArray[i];
                }
            }
            //总权重
            float weight = 0f;
            for (int i = 0; i < weightArray.length; i++) {
                weight += weightArray[i];
            }
            totalWeight=weight;
        }

        /**
         * 获得所有行列的起始位置-横向
         * @param column
         * @return
         */
        public int getLocationX(int column){
            return columnCellArray[column];
        }

        /**
         * 获得所有行列的起始位置-纵向
         * @param row
         * @return
         */
        public int getLocationY(int row){
            return rowCellArray[row];
        }

        /**
         * 获得每列权重比
         * @return
         */
        public float[] getWeightArray() {
            return weightArray;
        }

        public float getTotalWeight() {
            return totalWeight;
        }

        public int[] getRowArray() {
            return rowArray;
        }

        public int[] getColumnArray() {
            return columnArray;
        }
    }

    class RecyclerBin{
        SparseArray<ArrayList<ViewInfoItem>> scrapViews= new SparseArray<>();

        /**
         * 添加一个控件到缓存集
         * @param viewInfo
         */
        void addScarpView(ViewInfoItem viewInfo){
            viewInfo.view.setSelected(false);
            viewInfo.view.setPressed(false);
            ArrayList<ViewInfoItem> views = scrapViews.get(viewInfo.viewType);
            if(null!=views){
                views.add(viewInfo);
            } else {
                views=new ArrayList<>();
                views.add(viewInfo);
                scrapViews.put(viewInfo.viewType,views);
            }
        }

        /**
         * 将现在界面显示的控件全部放入缓存
         */
        void detachAndScrapAttachedViews(){
            for(ViewInfoItem view: viewInfoItems){
                addScarpView(view);
            }
            viewInfoItems.clear();
        }

        /**
         * 清空所有缓存集
         */
        void recyclerAll(){
            scrapViews.clear();
        }

        /**
         * 获得一个测量过的控件
         * @param row
         * @param column
         * @return
         */
        ViewInfoItem newViewFromAdapter(int row, int column){
            if(null==adapter) throw new NullPointerException("获取View时Adapter不能为空!");
            int viewType = adapter.getViewType(row, column);
            View view = adapter.getView(TableLayout.this, viewType);
            return new ViewInfoItem(view,viewType,row,column);
        }

        /**
         * 获得一个控件,如果缓存中有,从缓存中取,如果缓存没有,则通过数据适配器获取一个
         * @param row
         * @param column
         * @return
         */
        ViewInfoItem getViewInfoItem(int row, int column){
            if(null==adapter) throw new NullPointerException("获取View时Adapter不能为空!");
            ViewInfoItem viewInfoItem=null;
            if(0 < scrapViews.size()){
                int viewType = adapter.getViewType(row, column);
                ArrayList<ViewInfoItem> views = scrapViews.get(viewType);
                if(!views.isEmpty()){
                    viewInfoItem=views.remove(views.size()-1);
                    viewInfoItem=new ViewInfoItem(viewInfoItem.view,viewType,row,column);
                }
            }
            if(null==viewInfoItem){
                //获得一个新的经过测量的控件
                viewInfoItem= newViewFromAdapter(row,column);
            }
            return viewInfoItem;
        }
    }

    /**
     * @author :Created by cz
     * @date 2019-06-27 14:29
     * @email bingo110@126.com
     * 表格数据的数据适配器对象
     */
    public static abstract class TableAdapter{
        /**
         * 获得行数
         * @return
         */
        public abstract int getRowCount();
        /**
         * 获得列数
         * @return
         */
        public abstract int getColumnCount(int row);

        /**
         * 获得本列排版权重,
         * 默认返回0,代表由自己决定大小
         * 每一个对象返回相同的,则代表平分
         * @param column
         * @return
         */
        public float getColumnWeight(int column){
            return 0f;
        }

        /**
         * 获得指定行,指定列的单元格宽
         * @param row 行
         * @param column 当前列
         * @return
         */
        public float getColumnWidth(int row, int column){ return 0f; }

        /**
         * 获得表格内元素的类型
         * @param row
         * @param column
         * @return
         */
        public int getViewType(int row,int column){
            return 0;
        }
        /**
         * 获得一个表格view
         * @param viewType
         * @return
         */
        public abstract View getView(ViewGroup parent, int viewType);

        /**
         * 绑定view信息
         * @param view
         */
        public abstract void onBindView(View view, int row, int column);

    }
}
