# Attention. This project is not maintained any more !!!

# VirtualView

[中文文档](README-ch.md)

## Projects of Tangram

### Android

+ [Tangram-Android](https://github.com/alibaba/Tangram-Android)
+ [Virtualview-Android](https://github.com/alibaba/Virtualview-Android)
+ [vlayout](https://github.com/alibaba/vlayout)
+ [UltraViewPager](https://github.com/alibaba/UltraViewPager)

### iOS

+ [Tangram-iOS](https://github.com/alibaba/Tangram-iOS)
+ [Virtualview-iOS](https://github.com/alibaba/VirtualView-iOS)
+ [LazyScrollView](https://github.com/alibaba/lazyscrollview)

### Server

+ [TAC](https://github.com/alibaba/tac)

In the system of Android, the structure of a page can be modified by config dynamically while the business components is written in native code which can only be updated with App release. This library, VirtualView, is born to solve this problem. It provides a serial of basic component and layout component like that in Android and builds business component via XML, then compiles the XML file into binary data and deploys it to App with VirutalView sdk to render the view. When the binary data comes from XML deploys dynamically, the business component on released App can be updated dynamically.

The way to write XML template and serialize to binary is borrowed from Android's native development. But in VirtualView it is simplified and enhanced with simple EL expression and logic expression, make it more suitable to Android system.

And just as its name implies, this library introduces a concept of virtual view, which allows the basic component and layout defined in XML to directly render on the canvas of container with component's size and position calculated. This makes the final view in Android system is flatten and render performance is imporved.

XML template to build business component dynamically and virtual view to improve performance, these are two most important reason to use VirtualView.

![](https://github.com/alibaba/VirtualView-iOS/raw/master/README/feature.png)

For more information, see [this](http://tangram.pingguohe.net/docs/virtualview/about-virtualview).

## Features

+ Write template once, run it on two platform. (VirtualView for iOS will be released later)
+ Provide basic widget and layout, support adding custom widget.
+ Support virtualized view, render UI mixed with virtual view and native view.
+ Support simple EL in XML template.
+ Support simple basic logical expression in XML template.
+ Ship with tools to help developing.

## Real-time compile tools
[I'm docs](https://github.com/alibaba/virtualview_tools/tree/master/compiler-tools/RealtimePreview)

![](https://raw.githubusercontent.com/alibaba/virtualview_tools/master/compiler-tools/RealtimePreview/screenshot.gif)

## User Guide

### Use It Independently


### Import Library

Please find the latest version in [release notes](https://github.com/alibaba/Virtualview-Android/releases). The newest version has been upload to jcenter and MavenCentral, make sure you have added at least one of these repositories. As follow:

For gradle:
``` gradle
compile ('com.alibaba.android:virtualview:1.0.5@aar') {
	transitive = true
}
```

Or in maven:
pom.xml
``` xml
<dependency>
  <groupId>com.alibaba.android</groupId>
  <artifactId>virtualview</artifactId>
  <version>1.0.5</version>
  <type>aar</type>
</dependency>
```

Init a `VafContext` instance,

```
VafContext vafContext = new VafContext(mContext.getApplicationContext());
```

Init an image loader.(Optional) If you run Demo App and use NImage and VImage, this work has to been done. In product App, you are suggested to use your own image component to replace such build-in ones because a product level App always have custom image library.

```
VafContext.loadImageLoader(mContext.getApplicationContext());
```

Init `ViewManager` instance,

```
ViewManager viewManager = vafContext.getViewManager();
viewManager.init(mContext.getApplicationContext());
```

Load template binary data compiled by VirtualView Tools. There are to ways to load, one way is load byte-array directly(suggested):

```
viewManager.loadBinBufferSync(TMALLCOMPONENT1.BIN);
viewManager.loadBinBufferSync(TMALLCOMPONENT2.BIN);
......
```

the other way is load from file：

```
viewManager.loadBinFileSync(TMALLCOMPONENT1_PATH);
viewManager.loadBinFileSync(TMALLCOMPONENT2_PATH);
......
```

If you have developed custom basic components, register their builders(Guide to develop custom basic components [here](http://tangram.pingguohe.net/docs/android/add-a-custom-element)):

```
viewManager.getViewFactory().registerBuilder(BizCommon.TM_PRICE_TEXTVIEW, new TMPriceView.Builder());
viewManager.getViewFactory().registerBuilder(BizCommon.TM_TOTAL_CONTAINER, new TotalContainer.Builder());
```

Register event handler for click or exposure: (Information for event handler [here](http://tangram.pingguohe.net/docs/android/event-handler))

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

Build final view instance by name and add it to existing layout,

```
View container = vafContext.getContainerService().getContainer(name, true);
mLinearLayout.addView(container);
```

Set data to view to bind it to views,

```
IContainer iContainer = (IContainer)container;
JSONObject json = getJSONDataFromAsset(data);
if (json != null) {
    iContainer.getVirtualView().setVData(json);
}
```

Of course before running your own components, you should learn to write XML template for your components. In Demo App there is a few templates to show how to do it. Try to learn to write one with the help of samples and guide.

### Use It in Tangram

Use VirtualView in Tangram is just the same as using it independently while a lot of work has been done in Tangram, users only need to register components and template data to Tangram, provide event handler.

Register Tangram component developed by VirtualView with component's type,

```
Tangram.Builder builder = Tangram.newBuilder(activity);
builder.registerVirtualView("tmallcomponent1");
builder.registerVirtualView("tmallcomponent2");
```

After TangramEngine is built, load each component's binary data,

```
tangramEngine.setVirtualViewTemplate(TMALLCOMPONENT1.BIN);
tangramEngine.setVirtualViewTemplate(TMALLCOMPONENT2.BIN);
...
```

Register custom basic component builder if necessary,

```
ViewManager viewManager = tangramEngine.getService(ViewManager.class);
viewManager.getViewFactory().registerBuilder(BizCommon.TM_PRICE_TEXTVIEW, new TMPriceView.Builder());
...
```

Register event handler,

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

More about running VirtualView in Tangram can be found [here](http://tangram.pingguohe.net/docs/android/use-virtualview-in-tangram).

# Contributing

Before you open an issue or create a pull request, please read [Contributing Guide](CONTRIBUTING.md) first.

# LICENSE

VirtualView is available under the MIT license.

