# 内容描述 #

- sdk 集成包目录
- app DEMO工程

# 集成准备 #

拷贝sdk/libs下文件到工程的libs目录下。


注意：如果工程只支持arm平台，不要拷贝x86目录。

# 代码集成 #

## AndroidManifest.xml ##

	<application> 
	</application>

在此节点下增加

### 唤醒接收器申明 ###

		<!--
			app wakeup receiver
			class in APP
        -->
        <receiver android:name="com.netease.app.CosineReceiver" >
        </receiver>

替换com.netease.app.CosineReceiver为app的接收器


### 直接拷贝 ###

		<!--
			cosine core service
			class in SDK
			must deploy @:cosine process			
        -->
        <service
            android:name="com.netease.cosine.core.CosineService"
            android:process=":cosine" >
        </service>
        
        <!--
			cosine wakeup receiver
			class in SDK
			had better deploy @:cosine process
			must exported
        -->
        <receiver
            android:name="com.netease.cosine.target.CosineReceiver"
            android:exported="true"
            android:process=":cosine" >
        </receiver>

        <!-- cosine target SDK integrated -->
        <meta-data
            android:name="com.netease.cosine.target"
            android:value="" />

### 拷贝并按需要修改唤醒间隔 ###

        <!-- cosine target wakeup interval in seconds -->
        <meta-data
            android:name="com.netease.cosine.target.interval"
            android:value="300" />

### 拷贝并按需要修改唤醒接收器 ###

        <!-- app wakeup receiver -->
        <meta-data
            android:name="com.netease.cosine.target.receiver"
            android:value="com.netease.app.CosineReceiver" />	

## 启动代码 ##

				Intent service = CosineIntent.start(context);
				
				try {
					context.startService(service);
				} catch (Throwable tr) {
					tr.printStackTrace();
				}