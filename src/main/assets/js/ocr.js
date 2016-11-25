var app = angular.module('app', []);
app.controller('BodyCtrl', function ($scope, $http, $timeout) {
    var load = function () {
        $http.get(Context + '/api/ocrs').success(function (ocrs) {
            $scope.ocrs = ocrs;
        }).finally(function () {
            $timeout(load, 1000);
        });
    };
    $timeout(load, 0);
});
