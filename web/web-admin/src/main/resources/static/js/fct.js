
var JQCheck = {
    load: function () {
        //给checkbox提供全选
        $(".checkbox").click(function () {
            var checkall = true;
            $(".checkbox").each(function () {
                if (!$(this).is(':checked')) {
                    checkall = false;
                }
            });
            if (checkall) {
                $(".checkall").attr("checked", "checked");
            } else {
                $(".checkall").removeAttr("checked");
            }
        });
        $(".checkall").click(function () {
            if ($(this).is(":checked")) {
                $(".checkbox").attr("checked", "checked");
            } else {
                $(".checkbox").removeAttr("checked");
            }
        });
    },
    count: function () {
        return $(".checkbox:checked").length;
    },
    valid: function () {
        var bool = false;
        $(".checkbox").each(function () {
            if ($(this).is(':checked')) {
                bool = true;
                return;
            }
        });
        return bool;
    }
};

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

    if (options.type == 'GET' || options.type == 'LOAD') {
        $.ajax({
            url: options.url ? options.url : "",
            type: 'GET',
            cache: false,
            timeout: 3000000,
            error: function () { if (options.containerid) { $('#' + options.containerid).html('数据加载失败'); } else { /*alert('数据加载失败，可能是网络连接问题或者服务器错误。'); */ } },
            success: options.callback,
            complete: function () { if (html != "") { $(element).html(html); } }
        });
    } else if (options.type == 'POST') {
        if (!options.url) {
            options.url = $("#" + options.form).attr("action");
        }
        var data;
        if ('object' == typeof (options.form)) {
            data = options.form;
        } else {
            data = options.form ? $('#' + options.form).serialize() : "";
        }
        $.ajax({
            url: options.url,
            type: 'POST',
            //contentType:"application/x-www-form-urlencoded; charset=UTF-8",
            data: data,
            cache: false,
            timeout: 3000000,
            error: function () { /*alert('数据加载失败，可能是网络连接问题或者服务器错误。'); */ },
            success: options.callback,
            complete: function () { $(element).removeClass("disabled"); if (html != "") { $(element).html(html); } }
        });
    }
};
//重装jquery ajax 方法
var JQAjax = {
    get: function (element, options) {
        Ajax(element, {
            type: "GET",
            url: options.url,
            wait: options.wait,
            callback: options.callback
        });
    },
    load: function (element, url,fun) {
        var cid = element.attr("data-load");
        Ajax(element, {
            type: "LOAD",
            url: url,
            containerid: cid,
            callback: function (data) {
                $('#' + cid).html(data);
            }
        })
    },
    post: function (element, options) {
        if (options.confirm) {
            if (!confirm('你确定要执行该操作？')) {
                return;
            }
            //JQbox.confrim("你确定要执行该操作？");
        }
        $(".error").each(function () {
            $(this).hide();
        });
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
                        case "func":
                            eval(data.func);
                            break;
                        case "remind":
                            var close = "<button class='close' id='close_remind' type='button'>×</button>";
                            $('#valid_remind').html(close + data.message).show();
                            $('#close_remind').click(function () { $("#valid_remind").hide()});
                            break;
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
                        case "error":
                            var err = data.dic;
                            for (var o in data.dic) {
                                $('#e_' + o).html(err[o]).show();
                            }
                            break;
                    }
                }
            }
        });
    }
};

var JQDialog = {
    creat: function (action,options) {
        options = options || {};

        JQAjax.get(null, {
            url: options.url,
            callback: function (t) {
                //页面层
                layer.open({
                    type: 1,
                    skin: 'layui-layer-rim', //加上边框
                    area: [options.width + 'px', options.height + "px"], //宽高
                    shift: 2,
                    title: options.title,
                    content: t
                });
            }
        });
        //$(".ui-dialog-titlebar-close").click(options.callback);
    }
};

///重写jquery dialog弹出提醒
var JQbox = {
    alert: function (message) {
        layer.msg(message);
    },
    reload: function (message) {
        $(".layui-layer").remove();
        layer.msg(message,{
            time: 2000 //2秒关闭（如果不配置，默认是3秒）
        },function () {
            location.reload();
        });
    },
    jump: function (message, url) {
        $(".layui-layer").remove();
        layer.msg(message,{
            time: 2000 //2秒关闭（如果不配置，默认是3秒）
        },function () {
            location.href = url;
        });
    },
    close: function (message, callback) {
        layer.open({
            type: 1,
            shade: false,
            title: false, //不显示标题
            content: message, //捕获的元素
            cancel: function(index){
                layer.close(index);
            }
        });
    },
    open: function (options) {
        options = options || {},
        JQDialog.creat('open',{
            title: options.title || "提示",
            url: options.url,
            width: options.width || 500,
            height: options.height || 'auto'
        });
    },
    confirm: function (ele,options) {
        //询问框
        layer.confirm(options.msg, {
            btn: ['确定', '取消'], //按钮
            title:'提示'
        }, function () {
            layer.close();
            JQAjax.post(ele, {
                url: options.url,
            });
        }, function () {
            layer.close();
            //cancelcall.call(this);
        });
    },
    prompt:function(ele,options){
        layer.prompt({
            title: options.title,
            formType: options.type || 0 //prompt风格，支持0-2
        }, function (text) {
            JQAjax.post(ele, {
                url: options.url + encodeURI(encodeURI(text)),
            });
        });
    }
};


  var dropZoneUploads = {
      init: function (ele,options) {
          var input = options.input;
          var url = options.url;
          var path = options.path;
          var max = options.max || 1;
          var default_img = options.default_img || '';
          var imgs = [];
          Dropzone.autoDiscover = false;
          var tem_str = '<div class=\"dz-preview sortable_img dz-file-preview\">\n  ' +
              '<div class="preview-img">\n    ' +
              '<img data-dz-thumbnail />\n  ' +
              '<div data-dz-name></div>\n    ' +
              '</div>\n  ' +
              '<a name class="fork-remove" data-dz-remove />' +
              '<div class=\"dz-success-mark\"><span>✔</span></div>\n  ' +
              '<div class=\"dz-error-mark\"><span>✘</span></div>\n ' +
              ' <div class=\"dz-error-message\"><span data-dz-errormessage></span></div>\n' +
              '</div>';
          var imgwidth = "@200w.jpg";
          $(ele).dropzone({
              url:url,
              autoProcessQueue:true,
              parallelUploads:100,
              maxFiles:max,
              init:function(){
                  var myDropzone=this;
                  //如果为单个图片的展现，1.jpg,2.jpg
                  if(!default_img || default_img==''){
                      default_img =[];
                      var inputValue = $("#"+input).val();
                      if(inputValue!="") {
                          imgs = inputValue.split(",");
                          for (var i = 0; i < imgs.length; i++) {
                              var json = {img_url: "" + path + imgs[i] + "", img_name: ""};
                              default_img.push(json);
                          }
                      }
                  }
                  for(var i=0;i<default_img.length;i++){
                      //{img_url:,img_name:}
                      if(default_img[i].img_url !=''){
                          var myurl = default_img[i].img_url+imgwidth;
                          myDropzone.emit("addedfile", default_img[i]);
                          myDropzone.emit("thumbnail", default_img[i], myurl);

                          //$(myDropzone).children('.fork-remove').attr("data-url",myurl);
                      }
                  }

                  myDropzone.on("maxfilesexceeded", function(file) {
                      if(parseInt(myDropzone.options.maxFiles) == 1){
                          myDropzone.removeAllFiles();
                          myDropzone.addFile(file);
                      }else {
                          myDropzone.removeFile(file);
                      }
                  });

              },
              success: function (file, response, e) {
                    //var res = JSON.parse(response);
                    if(response.data) {
                        imgs.push(response.data.url);
                        $("#"+input).val(imgs.join());
                        // If the image is already a thumbnail:
                        this.emit('thumbnail', file, path+response.data.url+imgwidth);

                        $(file.previewTemplate).children('.fork-remove').attr("data-url",response.data.url);
                    }
              },
              error: function(file, errorMessage, xhr) {
                  $(file.previewTemplate).children('.dz-error-mark').css('opacity', '1');
              },
              removedfile: function (file) {
                  var del_url = $(file.previewTemplate).children('.fork-remove').attr("data-url");
                  var arrData = $("#"+input).val().split(',');
                  for (var i = 0 ; i< arrData.length; i ++) {
                      if(arrData[i] == del_url){
                          arrData.splice(i,1);
                      }
                  }
                  $("#"+input).val(arrData.join());
                  $(file.previewTemplate).remove();

              },
              previewTemplate: tem_str
          });

      }
  };

    var webUploads = function (ele,opt) {

        var defaults = {
            url: '',
            input: '',
            max: 1,
            default_img: '',
            path:'http://fct-nick.img-cn-shanghai.aliyuncs.com',

        };
        var options = $.extend({}, defaults, opt);

        dropZoneUploads.init(ele, options);

        var sortable = new Sortable(ele[0], {
          animation: 150,
          ghostClass: "sortable-ghost",
          handle: '.dz-preview'
        });
    };

//图片上传
var editorUpload = function(file, editor, $editable){

    var filename = false;
    try{
        filename = file['name'];
    } catch(e){
        filename = false;
    }
    if(!filename){
        $(".note-alarm").remove();
    }

    //以上防止在图片在编辑器内拖拽引发第二次上传导致的提示错误
    data = new FormData();
    data.append("file", file);
    data.append("key",filename); //唯一性参数

    $.ajax({
        data: data,
        type: "POST",
        url: "/upload/image?action=editor",
        cache: false,
        contentType: false,
        processData: false,
        success: function(result) {
            if(result.code !=200){
                JQbox.alert("上传失败");
            }
            editor.insertImage($editable, result.data.url);
        },
        error:function(){
            JQbox.alert("上传失败");
            return;
        }
    });
}

