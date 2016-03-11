package com.newtonker.jigsawdemo.utils;

import android.content.Context;

import com.newtonker.jigsawdemo.model.TemplateEntity;
import com.newtonker.jigsawdemo.widget.TouchSlotLayout;

import java.util.ArrayList;
import java.util.List;

public class TemplateUtils
{
    /**
     * 获取对应type类型的拼图模板
     * @param context
     * @param type
     * @param paths
     * @return
     */
    public static List<TouchSlotLayout> getSlotLayoutList(Context context, int type, List<String> paths)
    {
        if(type < 0 || type > 3 || null == paths)
        {
            return null;
        }

        List<TemplateEntity> entityList = ParserHelper.getInstance(context).getEntityList(type);
        List<TouchSlotLayout> touchSlotLayoutList = new ArrayList<>();

        // 这里的取值130和activity_select_photo.xml中的model_area高度有关；
        int width = DisplayUtils.dp2px(context, 130);
        int height = DisplayUtils.dp2px(context, 130);

        for(TemplateEntity entity : entityList)
        {
            TouchSlotLayout touchSlotLayout = new TouchSlotLayout(context, false);
            touchSlotLayout.setImagePathList(paths);
            touchSlotLayout.setTemplateEntity(entity);
            // 这里必须要设置一个宽高值才能显示出图片
            touchSlotLayout.reDraw(width, height);

            touchSlotLayoutList.add(touchSlotLayout);
        }

        return touchSlotLayoutList;
    }

    /**
     * 获取单个SlotView的Entity
     * @param context
     * @param type
     * @param position
     * @return
     */
    public static TemplateEntity getEntity(Context context, int type, int position)
    {
        if(type < 0 || type > 3)
        {
            return null;
        }

        List<TemplateEntity> entities = ParserHelper.getInstance(context).getEntityList(type);
        if(position < 0 || position >= entities.size())
        {
            return null;
        }

        return entities.get(position);
    }
}
