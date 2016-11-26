var app = angular.module('app', []);
app.controller('BodyCtrl', function ($scope, $http, $timeout) {
    var load = function () {
        $http.get(Context + '/api/faces').success(function (faces) {
            $scope.faces = faces;
        }).finally(function () {
            if (!$scope.stop) {
                $timeout(load, 1000);
            }
        });
    };
    $timeout(load, 0);
});
