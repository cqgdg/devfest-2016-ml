var app = angular.module('app', []);

app.controller('BodyCtrl', function ($scope, $http) {
    $http.get(Context + "/api/wx/session").success(function (user) {
        $scope.user = user;
    });
});
