(function (root, factory) {
	root.JsBridge = factory(root);
}(typeof window == "undefined" ? this : window, function (win) {
	if (!win.document) { return {};}

	var doc = win.document,
	title = doc.title,
	ua = navigator.userAgent.toLowerCase(),
	platform = navigator.platform.toLowerCase(),
	isMacorWin = !(!platform.match("mac") && !platform.match("win")),
	isandroid = -1 != ua.indexOf("android"),
	isphoneorpad = -1 != ua.indexOf("iphone") || -1 != ua.indexOf("ipad"),
	JsBridge = {
		usable: false,
		init: function (bridge) {
			return this;
		},

		callJavaAsync: function(methodName, params, cb) {
			if (!window._JSNativeBridge) {
				//JS not be injected success
				cb({
					status: "-1",
					msg: "window._JSNativeBridge is undefined"
				}, {});
				return;
			}

			try {
                window._JSNativeBridge._doSendRequest(methodName, params, cb);
			} catch (e) {
				cb({status: "-1", msg: e},{});
			}
		},

		callJavaSync: function(methodName, params) {
            if (!window._JSNativeBridge) {
				return "error:JS not be injected success";
			}

            var result;
			try {
                result = window._JSNativeBridge._doSendRequestSync(methodName, params);
			} catch (e) {
				result = "error:" + e;
			}

			return result;
		},

        notification: function(params) {
            return this.callJavaSync("notification",{"msg": params});
        },

        picture: function(params,cb){
           	this.callJavaAsync("picture",{},cb);
        },
	};

	if (window._JSNativeBridge) {
		JsBridge.init(window._JSNativeBridge);
	} else {
		document.addEventListener(
			'JsBridgeInit',
			function(event) {
				JsBridge.init(event.bridge);
		    }
		);
	}

	return JsBridge;
}));
