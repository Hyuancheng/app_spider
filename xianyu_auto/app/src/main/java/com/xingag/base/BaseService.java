package com.xingag.base;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.xingag.util.AppUtils;

import java.util.List;

/***
 * 无障碍服务的基类
 */


public class BaseService extends AccessibilityService
{

    @SuppressLint("StaticFieldLeak")
    private static BaseService mInstance = null;

    private Context mContext = null;

    //无障碍服务管理器
    private AccessibilityManager mAccessibilityManager = null;


    public static BaseService getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new BaseService();
        }
        return mInstance;
    }

    public void init(Context context)
    {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {

    }

    @Override
    public void onInterrupt()
    {

    }

    /**
     * 检查当前无障碍服务是否开启
     *
     * @param serviceName 服务名称
     * @return 服务是否启用
     */
    @SuppressLint("LongLogTag")
    public boolean checkAccessibilityEnabled(String serviceName)
    {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices)
        {
            Log.e("checkAccessibilityEnabled", info.getId());
            if (info.getId().equals(serviceName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 模拟返回操作
     */
    public void performBackClick()
    {
        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        while (!AppUtils.judgeIsMainPage(getRootInActiveWindow()))
        {
            Log.e("xag","现在不是主页面，返回一次");
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    /**
     * 查找对应ID的View  Level>=18
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String id)
    {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }


    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(AccessibilityNodeInfo parentNode, String id)
    {
        if (parentNode == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = parentNode.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text)
    {
        //Level>=21 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
        //Level>=18  4.3.1
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }


    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo)
    {
        if (nodeInfo == null)
        {
            return;
        }
        while (nodeInfo != null)
        {
            if (nodeInfo.isClickable())
            {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }


    /**
     * 模拟下滑操作
     */
    public void performScrollBackward()
    {
        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }


    /**
     * 模拟上滑操作
     */
    public void performScrollForward()
    {
        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }


}
