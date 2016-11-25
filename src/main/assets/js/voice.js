var app = angular.module('app', []);
app.controller('BodyCtrl', function ($scope, $http, $timeout) {
    var load = function () {
        $http.get(Context + '/api/voices').success(function (voices) {
            $scope.voices = voices;
        }).finally(function () {
            $timeout(load, 1000);
        });
    };
    $timeout(load, 0);
});
