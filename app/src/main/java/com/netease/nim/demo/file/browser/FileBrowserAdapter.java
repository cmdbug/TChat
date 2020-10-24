package com.netease.nim.demo.file.browser;

import android.content.Context;

import com.netease.nim.uikit.common.adapter.TAdapter;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;

import java.util.List;

/**
 * Created by hzxuwen on 2015/4/17.
 */
public class FileBrowserAdapter extends TAdapter{

    public static class FileManagerItem {
        private String name;
        private String path;

        public FileManagerItem(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

    }

    public FileBrowserAdapter(Context context, List<?> items, TAdapterDelegate delegate) {
        super(context, items, delegate);
    }
}
