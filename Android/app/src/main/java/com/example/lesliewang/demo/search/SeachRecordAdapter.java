package com.example.lesliewang.demo.search;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.search.BaseRecycleAdapter;
import com.example.lesliewang.demo.search.SearchActivity;
import com.example.lesliewang.demo.tools.LanguageAnalysisTools;

import java.util.List;

import static com.example.lesliewang.demo.R.id.tv_delete;


/**
 *搜索历史记录Adapter
 */

public class SeachRecordAdapter extends BaseRecycleAdapter<String> {
    SearchActivity context;

    public SeachRecordAdapter(List<String> datas, Context mContext,SearchActivity context) {
        super(datas, mContext);
        this.context = context;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {

        TextView textView= (TextView) holder.getView(R.id.tv_record);

        textView.setText(datas.get(position));
        final String text = (String)textView.getText();
        //点击文字或空白处进入单词页面
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int language = LanguageAnalysisTools.getLanguage(text);
                if (language == 0) {//Chinese
                    context.transmitData2(text);//Adapter中调用SearchActivity的跳转页面的方法
                }else if(language == 1){
                    context.transmitData(text);//Adapter中调用SearchActivity的跳转页面的方法
                }

            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.transmitData(text);
            }
        });
        //点击删除图片删除该记录
        holder.getView(tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null!=mRvItemOnclickListener){
                    mRvItemOnclickListener.RvItemOnclick(position);
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.search_item;
    }
}
