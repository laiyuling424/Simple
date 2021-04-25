package com.lyl.runtime.skin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyl.runtime.skin.core.ISkinChangeListener;
import com.lyl.runtime.skin.core.SkinAppCompatViewInflater;

public class SkinActivity extends AppCompatActivity {

    private SkinAppCompatViewInflater mSkinAppCompatViewInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory2(layoutInflater, this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        if (isSkin()){
            if (mSkinAppCompatViewInflater == null){
                mSkinAppCompatViewInflater = new SkinAppCompatViewInflater(context);
            }
            mSkinAppCompatViewInflater.setName(name);
            mSkinAppCompatViewInflater.setAttrs(attrs);
            return mSkinAppCompatViewInflater.createSkinView();
        }

        return super.onCreateView(parent, name, context, attrs);
    }

    protected boolean isSkin() {
        return false;
    }


    protected void skinDynamic(String skinPath, int themeColorId) {
        SkinManager.getInstance().loadSkin(skinPath);
        applyViews(getWindow().getDecorView());
    }

    /**
     * 控件回调监听，匹配上则给控件执行换肤方法
     */
    protected void applyViews(View view) {
        if (view instanceof ISkinChangeListener) {
            ISkinChangeListener viewsMatch = (ISkinChangeListener) view;
            viewsMatch.changeSkin();
        }

        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                applyViews(parent.getChildAt(i));
            }
        }
    }
}