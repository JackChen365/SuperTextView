package com.cz.widget.supertextview.sample.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cz.widget.supertextview.sample.R;
import com.cz.widget.supertextview.sample.data.Data;
import com.cz.widget.supertextview.sample.view.TableLayout;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author :Created by cz
 * @date 2019-06-27 14:30
 * @email bingo110@126.com
 * 表格布局的数据适配器演示
 * @see TableLayout
 */
public class TableLayoutAdapter extends TableLayout.TableAdapter {
    private static final int HEADER_TYPE=0;
    private static final int CELL_ITEM=1;
    private final LayoutInflater layoutInflater;
    private final List<List<String>> items;

    public TableLayoutAdapter(Context context, List<List<String>> items) {
        this.layoutInflater = LayoutInflater.from(context);
        this.items=items;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount(int row) {
        return items.get(row).size();
    }

    @Override
    public float getColumnWidth(int row, int column) {
        int columnCount = getColumnCount(row);
        return column==columnCount-1 ? 300f : 0f;
    }

    @Override
    public int getViewType(int row, int column) {
        return 0==row ? HEADER_TYPE : CELL_ITEM;
    }

    public String getItem(int row,int column){
        return items.get(row).get(column);
    }

    @Override
    public View getView(ViewGroup parent, int viewType) {
        if(viewType==HEADER_TYPE){
            return layoutInflater.inflate(R.layout.simple_header_text_item,parent,false);
        } else {
            return layoutInflater.inflate(R.layout.simple_table_item,parent,false);
        }
    }

    @Override
    public void onBindView(View view, int row, int column) {
        int viewType = getViewType(row, column);
        final String item = getItem(row, column);
        if(viewType==HEADER_TYPE){
             final TextView textView=view.findViewById(R.id.textView);
             textView.setText(item);
        } else {
            Context context = view.getContext();
            float imageSize = context.getResources().getDimension(R.dimen.image_size);
            final ImageView imageView=new ImageView(context);
            imageView.setBackgroundResource(R.drawable.primary_selector);
            imageView.measure(
                    View.MeasureSpec.makeMeasureSpec((int) imageSize, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec((int) imageSize, View.MeasureSpec.EXACTLY));
            imageView.layout(0,0,imageView.getMeasuredWidth(),imageView.getMeasuredHeight());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Click image1!", Toast.LENGTH_SHORT).show();
                }
            });

            final ImageView imageView1=view.findViewById(R.id.imageView);
            imageView1.setBackgroundResource(R.drawable.primary_selector);
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Click image2!", Toast.LENGTH_SHORT).show();
                }
            });
            final TextView textView=view.findViewById(R.id.textView);
            //加载图片一张随机的图片
            String image = Data.getImage();
            //加载ViewSpan内的对象
            Glide.with(view.getContext()).load(image).transition(withCrossFade()).into(imageView);
            String image1 = Data.getImage();
            //加载常规布局内的对象
            Glide.with(view.getContext()).load(image1).transition(withCrossFade()).into(imageView1);

            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(item);
            final Random random=new Random();
            for(int i=0;i<1;i++){
                float red = random.nextInt(255);
                float green = random.nextInt(255) / 2f;
                float blue = random.nextInt(255) / 2f;
                int color= 0xff000000 | ((int) (red   * 255.0f + 0.5f) << 16) |
                        ((int) (green * 255.0f + 0.5f) <<  8) | (int) (blue  * 255.0f + 0.5f);
                //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            }
            textView.setText(stringBuilder);
        }
    }
}
