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
        jsapi.jsApiList = ['startRecord', 'stopRecord', 'uploadVoice'];
        wx.config(jsapi);
    });

    $scope.record = function () {
        if (!$scope.recording) {
            wx.startRecord();
            $scope.recording = true;
            return;
        }

        var config = ({
            success: function (res) {
                var localId = res.localId;

                var config = ({
                    localId: localId, // 需要上传的音频的本地ID，由stopRecord接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        var toast = WuToast.loading({message: '处理中...'});
                        var url = Context + '/api/wx/voice?mediaId=' + res.serverId;
                        $http.get(url).success(function (data) {
                            $scope.result = data;
                        }).error(function () {
                            WuDialog.alert({title: '服务器错误了，请重试！'})
                        }).finally(function () {
                            toast.close();
                        });
                    }
                });
                wx.uploadVoice(config)
            }
        });
        wx.stopRecord(config);
        $scope.recording = false;
    };

});
