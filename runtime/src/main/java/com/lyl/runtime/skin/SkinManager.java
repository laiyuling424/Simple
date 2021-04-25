package com.lyl.runtime.skin;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.lyl.runtime.skin.model.SkinCache;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * author : lyl
 * e-mail : laiyuling424@gmail.com
 * date   : 4/25/21 4:13 PM
 */
public class SkinManager {

    private static SkinManager instance;
    private Application application;
    private Resources appResources;
    private Resources skinResources;
    private String skinPackageName;
    private Map<String, SkinCache> cacheSkin;

    private boolean isDefaultSkin;

    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    public static SkinManager getInstance() {
        return instance;
    }

    private SkinManager(Application application) {
        this.application = application;
        appResources = application.getResources();
        cacheSkin = new HashMap<>();
    }

    public void loadSkin(String path) {

        if (TextUtils.isEmpty(path)) {
            isDefaultSkin = true;
            return;
        }

        if (cacheSkin.containsKey(path)) {
            isDefaultSkin = false;
            SkinCache skinCache = cacheSkin.get(path);
            if (null != skinCache) {
                skinResources = skinCache.getSkinResources();
                skinPackageName = skinCache.getSkinPackageName();
                return;
            }
        }

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, path);
            skinResources = new Resources(assetManager, appResources.getDisplayMetrics(), appResources.getConfiguration());
            skinPackageName = application.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES).packageName;
            if (TextUtils.isEmpty(skinPackageName)) {
                isDefaultSkin = true;
            }
            if (!isDefaultSkin) {
                cacheSkin.put(path, new SkinCache(skinResources, skinPackageName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            isDefaultSkin = true;
        }
        isDefaultSkin = false;
    }

    private int getSkinResourcesId(int resourceId) {

        String resourceName = appResources.getResourceEntryName(resourceId);
//        String resourceName = appResources.getResourceName(resourceId);
        String resourceTypeName = appResources.getResourceTypeName(resourceId);
        int skinResourceId = skinResources.getIdentifier(resourceName, resourceTypeName, skinPackageName);
        if (skinResourceId == 0) {
            isDefaultSkin = true;
        }
        return isDefaultSkin ? resourceId : skinResourceId;
    }

    public boolean isDefaultSkin() {
        return isDefaultSkin;
    }

    public int getSkinColor(int resourceId) {
        int id = getSkinResourcesId(resourceId);
        return isDefaultSkin ? appResources.getColor(id) : skinResources.getColor(id);
    }

    public ColorStateList getColorStateList(int resourceId) {
        int ids = getSkinResourcesId(resourceId);
        return isDefaultSkin ? appResources.getColorStateList(ids) : skinResources.getColorStateList(ids);
    }

    public Drawable getSkinDrawable(int resourceId) {
        //图片或者颜色
        int id = getSkinResourcesId(resourceId);
        return isDefaultSkin ? appResources.getDrawable(id) : skinResources.getDrawable(id);
    }

    public String getString(int resourceId) {
        int ids = getSkinResourcesId(resourceId);
        return isDefaultSkin ? appResources.getString(ids) : skinResources.getString(ids);
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    public Object getBackgroundOrSrc(int resourceId) {
        String resourceTypeName = appResources.getResourceTypeName(resourceId);

        switch (resourceTypeName) {
            case "color":
                return getSkinColor(resourceId);

            case "mipmap": // drawable / mipmap
            case "drawable":
                return getSkinDrawable(resourceId);
        }
        return null;
    }

    // 获得字体
    public Typeface getTypeface(int resourceId) {
        // 通过资源ID获取资源path，参考：resources.arsc资源映射表
        String skinTypefacePath = getString(resourceId);
        // 路径为空，使用系统默认字体
        if (TextUtils.isEmpty(skinTypefacePath)) return Typeface.DEFAULT;
        return isDefaultSkin ? Typeface.createFromAsset(appResources.getAssets(), skinTypefacePath)
                : Typeface.createFromAsset(skinResources.getAssets(), skinTypefacePath);
    }
}

