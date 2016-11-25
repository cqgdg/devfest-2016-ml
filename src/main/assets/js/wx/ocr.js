var app = angular.module('app', ['ng.weui']);

app.controller('BodyCtrl', function ($scope, $http, WuToast, WuDialog) {

    $http.get(Context + '/api/wx/session').success(function (user) {
        $scope.user = user;
    });

    $http.get(Context + '/api/wx/jsapi').success(function (jsapi) {
        wx.ready(function () {
            $scope.ready = true;
            $scope.$digest();
        });
        jsapi.debug = false;
        jsapi.appId = jsapi.appid;
        jsapi.nonceStr = jsapi.noncestr;
        jsapi.jsApiList = ['chooseImage', 'uploadImage'];
        console.log(jsapi);
        wx.config(jsapi);
    });

    $scope.upload = function () {
        var config = {
            count: 1,
            sizeType: ['compressed'],
            sourceType: ['album', 'camera'],
            success: function (resp) {
                var config = {
                    localId: resp.localIds[0],
                    success: function (res) {
                        var toast = WuToast.loading({message: '处理中...'});
                        var url = Context + '/api/wx/ocr?mediaId=' + res.serverId;
                        $http.get(url).success(function (data) {
                            $scope.result = data;
                        }).error(function () {
                            WuDialog.alert({title: '服务器错误了，请重试！'})
                        }).finally(function () {
                            toast.close();
                        });
                    },
                    fail: function () {
                        WuDialog.alert({title: '上传图片错误了，请重试！'});
                    }
                };
                wx.uploadImage(config);
            }
        };
        wx.chooseImage(config);
    };

});
