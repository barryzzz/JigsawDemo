# JigsawDemo


>关于我<br/>
>微博：<a href="http://weibo.com/u/1804503012" target="_blank">newtonker</a>&nbsp;&nbsp;邮箱：<a href="mailto:newtonker@gmail.com" target="_blank">newtonker@gmail.com</a><br/>
>协助开发者<br/>
>Github：<a  href="https://github.com/JochimY" target="_blank">JochimY</a>&nbsp;&nbsp;邮箱：<a href="mailto:jochimyoung@gmail.com" target="_blank">jochimyoung@gmail.com</a>


### 项目简介

* 本项目是一个Android的拼图Demo，功能上类似于Layout，Modiv，美图秀秀的拼图功能。


### 特别说明

* 由于项目的关系，需要开发类似于Layout，Moldiv，美图秀秀之类的拼图功能。在前期调研过程中发现Github上相关的参考资料很少（也许是我没有找到的缘故，欢迎拍砖），要不就是和需求相差很大。在拼图功能开发的前期，和 <a  href="https://github.com/JochimY" target="_blank">JochimY</a> 讨论了几种实现方案，最终选择了Demo中的实现方式。拼图功能完成后，我一直想把拼图的实现方式开源，一是希望能和感兴趣的朋友一起讨论下当前实现方式的问题，二是希望能够得到一些更有建设性的实现方案。这一想法得到了 <a  href="https://github.com/JochimY" target="_blank">JochimY</a> 的大力支持，而且在拼图实现过程中 <a  href="https://github.com/JochimY" target="_blank">JochimY</a> 贡献了大量代码，特别是拼图模板排布和拼图解析的关键代码，在此向 <a  href="https://github.com/JochimY" target="_blank">JochimY</a> 深表感谢。


### 效果图

* 选择图片，并从展示的模板中选中一个拼图效果进入下一步操作

![Image of 1](https://github.com/newtonker/JigsawDemo/raw/master/screenshots/1.gif)


* 在拼图中选择新的图片替换当前选中的图片

![Image of 2](https://github.com/newtonker/JigsawDemo/raw/master/screenshots/2.gif)
![Image of 3](https://github.com/newtonker/JigsawDemo/raw/master/screenshots/3.gif)

* 长按当前选中的图片，并拖拽实现两张图片的交换

![Image of 4](https://github.com/newtonker/JigsawDemo/raw/master/screenshots/4.gif)

* 对当前选中的图片使用滤镜效果，并保存

![Image of 5](https://github.com/newtonker/JigsawDemo/raw/master/screenshots/5.gif)


### 实现原理
#### 1. /asstes/template.xml中定义了模板中各个图片的坐标和环绕规则。

> 举个例子来讲，如下代码代表了一个完整的拼图模板

```xml
<layout
    numOfSlots="3">
    <id>9</id>
    <points>0:0,0.5:0,1:0,0:0.5,0.5:0.5,0:1,0.5:1,1:1</points>
    <polygons>0,1,4,3/3,4,6,5/1,2,7,6</polygons>
</layout>
```

> 其中：
> - 第二行 ``` numOfSlots="3" ``` 表示本拼图模板中有三张图片；
> - 第三行 ``` <id>9</id> ``` 表示当前模板在所有模板中的序号为9；
> - 第四行 ``` <points>0:0,0.5:0,1:0,0:0.5,0.5:0.5,0:1,0.5:1,1:1</points> ```  表示拼图模板中涉及到的点相对于整个拼图组件View的x，y坐标。“,”用于分割点，“:”用于分割x和y坐标。本模板中包含了7个点，分别为0(0,0)，1(0.5,0)，2(1,0)，3(0,0.5)，4(0.5,0.5)，5(0,1)，6(0.5,1)，(1,1)；
> - 第五行 ``` <polygons>0,1,4,3/3,4,6,5/1,2,7,6</polygons> ```  表示拼图中三张图片边界的环绕规则，“/”用于区分图片的边界，0, 1, 4, 3 表示第一张图片所用到的点，以左上角开始。本模板中包含三张图片，三张图片所用到的点环绕分别为0->1->4->3; 3->4->6->5; 1->2->7->6；
> - 简单的图示表示如下：

![Image of 6](https://github.com/newtonker/JigsawDemo/raw/master/screenshots/6.jpg)


#### 2. /utils/ParserHelper.java中定义了解析拼图模板的方式，解析后的拼图模板封装到了HashMap中，根据JigsawType可以得到对应的拼图模板列表。

```java
/**
 * 解析xml
 * @param is
 * @return
 */
private static HashMap<JigsawType, List<TemplateEntity>> parseXml(InputStream is)
{
    List<TemplateEntity> entityList = new ArrayList<>();
    try
    {
        TemplateEntity entity = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(is, "utf-8");
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
            case XmlPullParser.START_TAG:
                String tagName = parser.getName();
                if (null != tagName && tagName.equals("layout"))
                {
                    entity = new TemplateEntity();
                    int numOfSlots = Integer.parseInt(parser.getAttributeValue(null, "numOfSlots"));
                    entity.setNumOfSlots(numOfSlots);
                }

                if(null != tagName && tagName.equals("id") && null != entity)
                {
                    String id = parser.nextText();
                    entity.setId(Integer.parseInt(id));
                }

                if(null != tagName && tagName.equals("points") && null != entity)
                {
                    String points = parser.nextText();
                    entity.setPoints(points);
                }

                if(null != tagName && tagName.equals("polygons") && null != entity)
                {
                    String polygons = parser.nextText();
                    entity.setPolygons(polygons);
                }
                break;
            case XmlPullParser.END_TAG:
                if(parser.getName().equals("layout"))
                {
                    entityList.add(entity);
                }
                break;
            default:
                break;
            }
            eventType = parser.next();
        }
    }
    catch (XmlPullParserException | IOException e)
    {
        e.printStackTrace();
        return null;
    }

    // 对list解析，并放到SparseArray中
    HashMap<JigsawType, List<TemplateEntity>> hashMap = new HashMap<>();

    List<TemplateEntity> tempList0 = new ArrayList<>();
    List<TemplateEntity> tempList1 = new ArrayList<>();
    List<TemplateEntity> tempList2 = new ArrayList<>();
    List<TemplateEntity> tempList3 = new ArrayList<>();

    for(TemplateEntity temp : entityList)
    {
        switch(temp.getNumOfSlots())
        {
        case 1:
            tempList0.add(temp);
            break;
        case 2:
            tempList1.add(temp);
            break;
        case 3:
            tempList2.add(temp);
            break;
        case 4:
            tempList3.add(temp);
            break;
        default:
            break;
        }
    }

    // 在循环结束后将模版的集合按照键值对方式存放
    hashMap.put(JigsawType.ONE_PHOTO, tempList0);
    hashMap.put(JigsawType.TWO_PHOTO, tempList1);
    hashMap.put(JigsawType.THREE_PHOTO, tempList2);
    hashMap.put(JigsawType.FOUR_PHOTO, tempList3);

    return hashMap;
}
```

### 目前存在的问题

* 拼图中的每张图片都要用一个ImageView表示，所以暂时只支持矩形，不支持椭圆等形状的拼图；
* 拼图中的每张图片都要用一个ImageView表示，所以拼图的数量不能太多，目前所选图片的上线是4张；
* 拼图保存目前只能保存到view大小；


## License

    Copyright 2016 newtonker

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

