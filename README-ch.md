# VirtualView

[English Document](README.md)

在 Tangram 体系里，页面结构可以通过配置动态更新，然而业务组件是通过 Native 代码实现的，无法动态更新。VirtualView 就是为了解决业务组件的动态更新而生的，它提供了一系列基础 UI 组件和布局组件能力，通过 XML 来搭建业务组件，并将 XML 模板编译成二进制数据，然后主体框架解析二进制数据并渲染出视图。当 XML 模板数据能动态下发的时候，客户端上的业务组件视图也就能动态更新了。

编写 XML 模板的方式、序列化成二进制数据的协议，VirtualView 都很大程度上吸取了 Android 原生开发的开发方式和原理，但增加了数据绑定、表达式相关的能力，可以更好的与 Tangram 体系结合。

在通过 XML 模板构建组件的基础之上，VirtualView 引入了虚拟化的概念，它支持将 XML 里描述的布局嵌套层次和视图节点扁平化、虚拟化，从而减少了最终渲染出来的实体组件的层次，提升绘制效率。

XML 模板实现组件的动态性，虚拟化的技术提升组件的渲染性能，这两点就是使用 VirtualView 技术的主要意义所在。

更多详细信息参考文档：TODO

## 主要功能

+ 一份模板，两端支持。
+ 提供基础的原子组件与容器组件，支持加入自定义组件。
+ 支持一种虚拟化实现组件的协议，在模板里混合使用虚拟组件和实体组件。
+ 支持在模板里编写数据绑定的表达式。
+ 支持在模板里写事件触发的逻辑表达式。
+ 提供配套的开发工具，辅助模板开发工具。

## 接入教程

### 单独使用

版本请参考 mvn repository 上的最新版本（目前最新版本是 1.0.0），最新的 aar 都会发布到 jcenter 和 MavenCentral 上，确保配置了这两个仓库源，然后引入aar依赖：

``` gradle
compile ('com.alibaba.android:virtualview:1.0.0@aar') {
	transitive = true
}
```

或者maven:
pom.XML
``` XML
<dependency>
  <groupId>com.alibaba.android</groupId>
  <artifactId>virtualview</artifactId>
  <version>1.0.0</version>
  <type>aar</type>
</dependency>
```

构建一个 `VafContext` 对象，

```
VafContext vafContext = new VafContext(mContext.getApplicationContext());
```
初始化一下图片加载器，如果只是运行 Demo 程序，使用内置的基础图片组件 NImage 和 VImage，那么需要初始化一下图片加载器，在真实的产品里，一般不需要这一步，因为往往需要使用方注册使用自己的图片组件；

```
VafContext.loadImageLoader(mContext.getApplicationContext());
```

初始化 `ViewManager` 对象，

```
ViewManager viewManager = vafContext.getViewManager();
viewManager.init(mContext.getApplicationContext());
```

加载模板数据，利用 VirtualView Tools 编译出二进制文件，在初始化的时候加载，有两张方式，一种是直接加载二进制字节数组（推荐）：

```
viewManager.loadBinBufferSync(TMALLCOMPONENT1.BIN);
viewManager.loadBinBufferSync(TMALLCOMPONENT2.BIN);
......
```

另一种是通过二进制文件路径加载：

```
viewManager.loadBinFileSync(TMALLCOMPONENT1_PATH);
viewManager.loadBinFileSync(TMALLCOMPONENT2_PATH);
......
```

如果开发了自定义的基础组件，注册自定义组件的构造器：(开发自定义组件的说明参考：TODO)

```
viewManager.getViewFactory().registerBuilder(BizCommon.TM_PRICE_TEXTVIEW, new TMPriceView.Builder());
viewManager.getViewFactory().registerBuilder(BizCommon.TM_TOTAL_CONTAINER, new TotalContainer.Builder());
```

注册事件处理器，比如常用的点击、曝光处理：(更多事件处理信息的说明参考：TODO）

```
vafContext.getEventManager().register(EventManager.TYPE_Click, new IEventProcessor() {

    @Override
    public boolean process(EventData data) {
        //handle here
        return true;
    }
});
vafContext.getEventManager().register(EventManager.TYPE_Exposure, new IEventProcessor() {

    @Override
    public boolean process(EventData data) {
        //handle here
        return true;
    }
});
```

通过组件名参数 name 生成组件实例，

```
View container = vafContext.getContainerService().getContainer(name, true);
mLinearLayout.addView(container);
```

如果组件模板里写了数据绑定的表达式，那么需要给组件绑定真实的数据，

```
IContainer iContainer = (IContainer)container;
JSONObject json = getJSONDataFromAsset(data);
if (json != null) {
    iContainer.getVirtualView().setVData(json);
}
```

当然在使用之前，先编写组件模板，在 Demo 程序里已经提供了大量的例子，结合例子和教程学习如何写一个模板并编译出来。TODO

### 在 Tangram 中使用

在 Tangram 里使用 VirtualView 的时候，大致流程如上述所示，只不过很多步骤已经内置到 Tangram 的初始化里了，外部只需要注册业务组件类型、加载模板数据、提供事件处理器。

注册 VirtualView 版本的 Tangram 组件，只需要提供组件类型名称即可，

```
Tangram.Builder builder = Tangram.newBuilder(activity);
builder.registerVirtualView("tmallcomponent1");
builder.registerVirtualView("tmallcomponent2");
```

在 TangramEngine 构建出来之后加载模板数据，

```
tangramEngine.setVirtualViewTemplate(TMALLCOMPONENT1.BIN);
tangramEngine.setVirtualViewTemplate(TMALLCOMPONENT2.BIN);
...
```

同样的有必要的话需要注册自定义基础组件的构造器，

```
ViewManager viewManager = tangramEngine.getService(ViewManager.class);
viewManager.getViewFactory().registerBuilder(BizCommon.TM_PRICE_TEXTVIEW, new TMPriceView.Builder());
...
```

注册事件处理器，

```
VafContext vafContext = tangramEngine.getService(VafContext.class);
vafContext.getEventManager().register(EventManager.TYPE_Click, new IEventProcessor() {

    @Override
    public boolean process(EventData data) {
        //handle here
        return true;
    }
});
vafContext.getEventManager().register(EventManager.TYPE_Exposure, new IEventProcessor() {

    @Override
    public boolean process(EventData data) {
        //handle here
        return true;
    }
});
```

更多关于在 Tangram 中使用 VirtualView 的信息，参考文档。TODO


# 贡献代码

在提 Issue 或者 PR 之前，建议先阅读 [Contributing Guide](CONTRIBUTING.md)。按照规范提建议。

# 开源许可证

VirtualView 遵循 MIT 开源许可证协议。
