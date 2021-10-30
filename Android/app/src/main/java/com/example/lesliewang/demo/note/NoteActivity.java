package com.example.lesliewang.demo.note;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lesliewang.demo.R;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
/*
生词本
 */
public class NoteActivity extends AppCompatActivity {

    WordViewModel wordViewModel;//存储和管理界面相关的数据
    RecyclerView recyclerView;//可回收视图
    MyAdapter myAdapter1,myAdapter2;//适配器
    private LiveData<List<Word>> filteredWords;//LiveData可观察的数据存储器类
    private List<Word> allWords;
    private static final String VIEW_TYPE_SHP = "view_type_shp";//SharedPreferences名称
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";//卡片视图选择的标志符
    private DividerItemDecoration dividerItemDecoration;//分隔线

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//线性列表
        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);//实例化
        myAdapter1 = new MyAdapter(false,wordViewModel);
        myAdapter2 = new MyAdapter(true,wordViewModel);

        //动画完了之后刷新序列号
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i <= lastPosition; i++) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.textViewNumber.setText(String.valueOf(i + 1));

                        }
                    }
                }

            }
        });

        SharedPreferences shp = getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean isUsingCardView = shp.getBoolean(IS_USING_CARD_VIEW, false);
        //第一个视图加上分隔线
        dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);

        if (isUsingCardView) {
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }


        //toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        //观察全部单词数据发生改变
        filteredWords = wordViewModel.getAllWordsLive();//获取列表
        filteredWords.observe(this, new Observer<List<Word>>() {//观察
            @Override
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();

                allWords = words;
                if (temp != words.size()) {
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }

            }
        });

        delete();

    }
    //滑动删除
    private void delete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word wordToDelete = allWords.get(viewHolder.getAdapterPosition());
                wordViewModel.deleteWords(wordToDelete);
                Snackbar.make(findViewById(R.id.notelayout),"删除了一个词汇",Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wordViewModel.insertWords(wordToDelete);
                            }
                        })
                        .show();

            }
            //在滑动的时候，画出浅灰色背景和垃圾桶图标，增强删除的视觉效果

            Drawable icon = ContextCompat.getDrawable(NoteActivity.this,R.drawable.ic_delete_forever_black_24dp);
            Drawable background = new ColorDrawable(Color.LTGRAY);
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                int iconLeft,iconRight,iconTop,iconBottom;
                int backTop,backBottom,backLeft,backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) /2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX > 0) {
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconLeft = itemView.getLeft() + iconMargin ;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                } else if (dX < 0){
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconRight = itemView.getRight()  - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                } else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
            }

        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 设置菜单
        getMenuInflater().inflate(R.menu.main2, menu);
        //Toolbar的搜索框
        SearchView searchView = (SearchView) menu.findItem(R.id.note_search).getActionView();
        //searchView.setMaxWidth(1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String pattern = newText.trim();
                filteredWords.removeObservers(NoteActivity.this);      //先移除之前添加的observer
                filteredWords = wordViewModel.findWordsWithPattern(pattern);
                filteredWords.observe(NoteActivity.this, new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = myAdapter1.getItemCount();

                        allWords = words;
                        if (temp != words.size()) {
                            myAdapter1.submitList(words);
                            myAdapter2.submitList(words);
                        }

                    }
                });
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //设置toolar上的按钮点击效果
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }else if (id == R.id.change) {
            // 获得SharedPreferences，名称为VIEW_TYPE_SHP
            SharedPreferences shp = getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
            boolean isUsingCardView = shp.getBoolean(IS_USING_CARD_VIEW, false);//读取
            SharedPreferences.Editor editor = shp.edit();
            if (isUsingCardView) {
                recyclerView.setAdapter(myAdapter1);
                recyclerView.addItemDecoration(dividerItemDecoration);//添加分隔线
                editor.putBoolean(IS_USING_CARD_VIEW, false);//存储
            } else {
                recyclerView.setAdapter(myAdapter2);
                recyclerView.removeItemDecoration(dividerItemDecoration);//卡片视图删除分隔线
                editor.putBoolean(IS_USING_CARD_VIEW, true);//存储
            }
            editor.apply();//提交（非同步）

            return true;

        }else if (id ==R.id.clear){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("清空数据");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wordViewModel.deleteAllWords();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create();
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
