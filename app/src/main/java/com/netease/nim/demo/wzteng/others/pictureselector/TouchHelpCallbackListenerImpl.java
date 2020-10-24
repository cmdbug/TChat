package com.netease.nim.demo.wzteng.others.pictureselector;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.yalantis.ucrop.entity.LocalMedia;

import java.util.Collections;
import java.util.List;

/**
 * Created by WZTENG on 2018/1/18 0018.
 */
public class TouchHelpCallbackListenerImpl implements DefaultItemTouchHelpCallback.OnItemTouchCallbackListener {

    private List<LocalMedia> selectList;
    private GridImageAdapter adapter;
    private Context context;

    private ITouchHelpCallback iTouchHelpCallback;

    public TouchHelpCallbackListenerImpl(Context context, List<LocalMedia> selectList, RecyclerView.Adapter adapter) {
        this.context = context;
        this.selectList = selectList;
        this.adapter = (GridImageAdapter) adapter;
    }

    public TouchHelpCallbackListenerImpl(Context context, List<LocalMedia> selectList, RecyclerView.Adapter adapter, ITouchHelpCallback iTouchHelpCallback) {
        this.context = context;
        this.selectList = selectList;
        this.adapter = (GridImageAdapter) adapter;
        this.iTouchHelpCallback = iTouchHelpCallback;
    }

    @Override
    public void onSwiped(int position) {
//        Log.d("wzt", "onSwiped");
        if (selectList != null) {
            selectList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public boolean onMove(int srcPosition, int targetPosition) {
//        Log.d("wzt", "onMove");
        if (srcPosition == selectList.size() || targetPosition == selectList.size() || srcPosition == targetPosition) {
            return false;
        }
        if (selectList != null) {
//            Collections.swap(selectList, srcPosition, targetPosition);//这里要改成全部替换，而不是2个对象间的替换
            changeSelecList(selectList, srcPosition, targetPosition);
            adapter.notifyItemMoved(srcPosition, targetPosition);
            return true;
        }
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        Log.d("wzt", "onSelectedChanged actionState:" + actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setAlpha(0.7f);
            viewHolder.itemView.setScaleX(1.1f);
            viewHolder.itemView.setScaleY(1.1f);
//            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
//            if (vib != null) {
//                vib.vibrate(70);
//            }
        }
        if (iTouchHelpCallback != null) {
            iTouchHelpCallback.onSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        Log.d("wzt", "clearView");
        viewHolder.itemView.setAlpha(1.0f);
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
        if (iTouchHelpCallback != null) {
            iTouchHelpCallback.clearView(recyclerView, viewHolder);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (iTouchHelpCallback != null) {
            iTouchHelpCallback.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private void changeSelecList(List<LocalMedia> list, int from, int to) {
        synchronized (this) {
//            Log.i("wzt", "selectMedia交换时个数:" + list.size());
            if (from > to) {
                int count = from - to;
                for (int i = 0; i < count; i++) {
                    Collections.swap(list, from - i, from - i - 1);
                }
            } else if (from < to) {
                int count = to - from;
                for (int i = 0; i < count; i++) {
                    Collections.swap(list, from + i, from + i + 1);
                }
            }
        }
    }

    public void setSelectList(List<LocalMedia> selectList) {
        this.selectList = selectList;
//        Log.i("wzt", "selectMedia设置时个数:" + selectList.size());
    }

    public interface ITouchHelpCallback {
        void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);
        void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);
        void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                         float dX, float dY, int actionState, boolean isCurrentlyActive);
    }
}
