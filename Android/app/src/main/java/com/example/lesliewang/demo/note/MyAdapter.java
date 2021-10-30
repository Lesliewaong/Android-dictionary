package com.example.lesliewang.demo.note;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lesliewang.demo.R;

//适配器
public class MyAdapter extends ListAdapter<Word,MyAdapter.MyViewHolder> {
    private boolean useCardView;
    private WordViewModel wordViewModel;

    MyAdapter(boolean useCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }


    //创建时呼叫
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        //设置卡片视图
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_card,parent,false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal,parent,false);
        }
        final MyViewHolder holder = new MyViewHolder(itemView);
        //点击跳转网页
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://m.youdao.com/dict?le=eng&q=" + holder.textViewEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        //中文显示切换
        holder.aSwitchChineseInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);
                if (isChecked) {
                    holder.textViewChinese.setVisibility(View.GONE);//隐藏中文
                    word.setChineseInvisible(true);
                    wordViewModel.updateWords(word);//更新
                } else {
                    holder.textViewChinese.setVisibility(View.VISIBLE);//显示中文
                    word.setChineseInvisible(false);
                    wordViewModel.updateWords(word);//更新
                }
            }
        });
        return holder;
    }
    //绑定时呼叫
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_holder,word);
        //设置显示
        holder.textViewNumber.setText(String.valueOf(position + 1));//从0开始
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
        //设置中文是否可见
        if (word.isChineseInvisible()) {
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChineseInvisible.setChecked(true);//不可见true
        } else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisible.setChecked(false);
        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber,textViewEnglish,textViewChinese;
        Switch aSwitchChineseInvisible;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);//序号
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);//英文
            textViewChinese = itemView.findViewById(R.id.textViewChinese);//中文
            aSwitchChineseInvisible = itemView.findViewById(R.id.switchChineseInvisible);//中文显示开关
        }
    }
}