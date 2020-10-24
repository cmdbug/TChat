package com.netease.nim.demo.avchat.widgets;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hzlichengda on 14-3-14.
 * if you want to use this parentView with an inner parentView inside ,the inner parentView's id must be R.id.childView
 */
public class ToggleView {

    private View parentView = null;
    private View childView = null;
    private ToggleState state = ToggleState.DISABLE;
    private ToggleListener listener = null;

    public ToggleView(View parentView, ToggleState initState, ToggleListener listener) {
        this.parentView = parentView;
        this.state = initState;
        this.listener = listener;
        init();
    }

    private void init(){
        if(parentView != null){
            parentView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onToggleStateChange();
                }
            });
            if(parentView instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup) parentView;
                childView = viewGroup.getChildAt(0);
            }
            toggle(state);
        }
    }

    public void toggle(ToggleState state){
        switch (state){
            case DISABLE:
                disable(false);
                break;
            case OFF:
                off(false);
                break;
            case ON:
                on(false);
                break;
        }
    }

    private void onToggleStateChange(){
        switch (state){
            case DISABLE:
                disable(true);
                break;
            case OFF:
                on(true);
                break;
            case ON:
                off(true);
                break;
        }
    }

    public void on(boolean callback){
        state = ToggleState.ON;

        parentView.setEnabled(true);
        parentView.setSelected(true);

        if(childView != null){
            childView.setEnabled(true);
            childView.setSelected(true);
        }

        if(listener != null && callback)
            listener.toggleOn(parentView);
    }

    public void off(boolean callback){
        state = ToggleState.OFF;

        parentView.setEnabled(true);
        parentView.setSelected(false);

        if(childView != null){
            childView.setEnabled(true);
            childView.setSelected(false);
        }

        if(listener != null && callback)
            listener.toggleOff(parentView);
    }

    public void disable(boolean callback){
        state = ToggleState.DISABLE;

        parentView.setSelected(false);
        parentView.setEnabled(false);

        if(childView != null){
            childView.setSelected(false);
            childView.setEnabled(false);
        }

        if(listener != null && callback)
            listener.toggleDisable(parentView);
    }

    public void enable(){
        off(false);
    }

    public boolean isEnable(){
        return state != ToggleState.DISABLE;
    }

}
