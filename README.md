## :rocket: 编码不易，点个star！ ##

**iOS版：https://github.com/cmdbug/ZChat**

**本项目使用网易云信IM加上网上开源项目编写，
（附上使用的开源地址，如有侵权或遗漏马上修改）。**

### 使用以下开源项目并修改了部分内容 ###

 > https://github.com/Curzibn/BottomDialog<br/>
 > https://github.com/LuckSiege/PictureSelector<br/>
 > https://github.com/Jude95/SwipeBackHelper<br/>
 > https://github.com/CJT2325/CameraView<br/>
 > https://github.com/bingoogolapple/BGAQRCode-Android<br/>
 > https://github.com/Naoki2015/CircleDemo<br/>
 > https://github.com/koral--/android-gif-drawable/<br/>
 > https://github.com/Sunzxyong/Recovery<br/>
 > https://github.com/wenmingvs/LogReport<br/>
 > https://github.com/SumiMakito/AwesomeQRCode<br/>
 > https://github.com/CarGuo/GSYVideoPlayer<br/>

当前编辑版本与编译环境：

- Android Studio 3.1.2
- Gradle Version 4.4
- Android Plugin Version 3.1.2
- Android SDK Platform-Tools 27.0.3
- Android 7.1.1 API25
- 真机原生7.1.2

提供已有测试账号，请勿修改相关信息！<br/>
 > 账号：dawa<br/>
 > 密码：123456<br/>

为了减小体积只保留一个armeabi-v7a，如果部分手机出现错误请自行修改配置<bt/>

* :memo:说明:android studio 3.1.2可能修改了什么东西，暂时去掉ndk，不然提示CreateProcess error=2, 系统找不到指定的文件。(错误提示指向一个ndk文件)

* :bug:已知Bug:<br/>
   > 1. 部份低版本系统侧滑返回会显示透明桌面(未修复)<br/>
   > 2. 二维码扫描框未根据屏幕尺寸进行调整(未修复)<br/>
   > 3. ~~朋友圈小视频目前无法在6.0以上使用，会导致崩溃(修复完成)~~<br/>
   > 4. ~~修复低版本提示jar包错误(修复完成)~~<br/>

* :memo:日志:<br/>
   > 1. :art:更新高德地图 3DMap_5.7.0 Navi_5.6.0 Search_5.7.0 Location_3.7.0 20180109版本<br/>
        :art:更新聊天定位发送图片为实时图片，但接收图片为默认图片<br/>
   > 2. :art:更新朋友圈视频为IJKPlayer播放器，可以正常播放视频<br/>
   > 3. :sparkles:增加朋友圈音乐连接到百度随机播放歌曲<br/>
   > 4. :art:修改朋友圈链接使用jsoup爬虫简单分析网页再刷新内容<br/>
   > 5. :sparkles:升级IM到4.0，增加好友在线状态<br/>
   > 6. :sparkles:增加聊天表情雨<br/>
   > 7. :bug:修复设置里面的"网络通话设置","音视频通话网络探测"的bug<br/>
   > 8. :art:改进Tab小红点点击就消失的"bug"<br/>
   > 9. :art:改进Toolbar返回按钮与标题间隙<br/>
   > 10. :art:修改朋友圈发表界面。微信使用GridView，本项目使用RecyclerView与微信效果有点区别，主要在微信效果为覆盖，本项目为透传。<br/>
   > 11. :sparkles:增加类似公众号侧滑悬浮球效果，与微信有一点区别（还有点小bug）。同时在SwipeBackHelper中增加触摸回调实现位置判断来处理。<br/>
   > 12. :art:修改朋友圈图片浏览效果，具有点击放大与返回缩小效果。<br/>

:art: 截图<br/>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_133335.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_133340.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151150.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_133350.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_132948.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_101434.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_101618.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_101902.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_104011.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_154439.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_141033.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151113.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151211.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151222.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151249.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151457.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_151527.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_154807.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_154834.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_105031.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_105042.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_105052.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_174236.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075030.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075050.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075101.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075133.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075140.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075148.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075155.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_075301.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_133027.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_133039.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_161828.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_161908.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_130921.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_131055.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_152618.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_153120.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_164207.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_135538.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_171843.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_141604.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_141617.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_164339.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_162243.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_162520.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_162701.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_173849.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_161549.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_161555.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_161557.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_185352.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_185416.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_190422.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_190430.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_190452.png"/>
</div>
<div>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_190503.png"/>
<img width="270" height="450" src="https://github.com/cmdbug/TChat/blob/master/screenshots/Screenshot_190518.png"/>
</div>


