var app = angular.module('app', ['ng.weui']);

app.controller('BodyCtrl', function ($scope, $http, WuToast) {

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
            sourceType: ['camera'],
            success: function (res) {
                var toast = WuToast.loading({message: '处理中...'});
                var config = {
                    localId: res.localIds,
                    isShowProgressTips: 1,
                    success: function (res) {
                        var url = Context + '/api/wx/face?mediaId=' + res.serverId;
                        $http.get(url).success(function (data) {
                            // TODO
                        }).finally(function () {
                            toast.close();
                        });
                    },
                    fail: function () {
                        toast.close();
                    }
                };
                wx.uploadImage(config);
            },
            fail: function () {

            },
            complete: function () {

            },
            cancel: function () {
                // 取消没有礼品哦
            }
        };
        wx.chooseImage(config);
    };

});
