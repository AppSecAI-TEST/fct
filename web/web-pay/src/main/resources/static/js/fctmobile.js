

var Ajax = function (element, options) {
    options = options || {};
    var html = "";
    if (options.wait) {
        if ($(element).hasClass("disabled")) {
            return;
        }
        html = $(element).html();
        $(element).addClass("disabled");
        $(element).html(options.waitinfo || html + "...");
    }

    if (!options.url) {
        options.url = $("#" + options.form).attr("action");
    }
    var data;
    if ('object' == typeof (options.form)) {
        data = options.form;
    } else {
        data = options.form ? $('#' + options.form).serialize() : "";
    }

    var obj = new XMLHttpRequest();
    obj.open("POST", url, true);
    bj.setRequestHeader("Content-type", "application/x-www-form-urlencoded"); // 发送信息至服务器时内容编码类型
    obj.onreadystatechange = function () {
        if (obj.readyState == 4 && (obj.status == 200 || obj.status == 304)) {  // 304未修改
            fn.call(this, obj.responseText);
        }
    };
    obj.send(data);

    $.ajax({
        url: options.url,
        type: 'POST',
        data: data,
        cache: false,
        timeout: 3000000,
        error: function () { /*alert('数据加载失败，可能是网络连接问题或者服务器错误。'); */ },
        success: options.callback,
        complete: function () { $(element).removeClass("disabled"); if (html != "") { $(element).html(html); } }
    });
};
//重装jquery ajax 方法
var JQAjax = {
    post: function (element, options) {
        Ajax(element, {
            type: "POST",
            wait: options.wait,
            url: options.url,
            form: options.form,
            waitinfo: options.waitinfo,
            callback: function (result) {
                //var data = eval('(' + result + ')');
                var data = result;
                if (data.method) {
                    switch (data.method) {
                        case "alert":
                            JQbox.alert(data.message);
                            break;
                        case "goto":
                            if (data.message) {
                                JQbox.jump(data.message, data.url);
                            } else {
                                location.href = data.url;
                            }
                            break;
                        case "reload":
                            if (data.message) {
                                JQbox.reload(data.message);
                            } else {
                                location.reload();
                            }
                            break;
                    }
                }
            }
        });
    }
};


///重写jquery dialog弹出提醒
var JQbox = {
    alert: function (message) {
        layer.open({
            content: message,
            skin: 'msg',
            time: 2 //2秒后自动关闭
        });
    },
    reload: function (message) {
        layer.open({
            content: message,
            skin: 'msg',
            time: 2, //2秒后自动关闭
            success:location.reload()
        });
    },
    jump: function (message, url) {
        layer.open({
            content: message,
            skin: 'msg',
            time: 2, //2秒后自动关闭
            success:location.href = url
        });
    }
};


