/* Header Controller */

var headerCtrl = angular.module('ttt.headerCtrl', []);

headerCtrl.controller('HeaderCtrl', ['$scope', '$http', '$location', 'CreateUser', '$uibModal', 'LogInfo', 'growl', 'Idle', 'Keepalive', 'Logout', '$state',
    function($scope, $http, $location, CreateUser, $uibModal, LogInfo, growl, Idle, Keepalive, Logout, $state) {

        $scope.$state = $state;

        $scope.isActive = function(viewLocation) {
            return viewLocation === $location.path();
        };

        $scope.userInfo = LogInfo.getUsername();

        $scope.loginPopup = function() {

            var modalInstance = $uibModal.open({
                templateUrl: "templates/login.tpl.html",
                controller: "LoginCtrl",
                resolve: {
                    login: function() {
                        return $scope.login;
                    }
                }
            });

            modalInstance.result.then(function(result) {
                $scope.userInfo = LogInfo.getUsername();
            }, function() {
                $scope.userInfo = LogInfo.getUsername();
            });

        };

        $scope.logout = function() {
            Logout.logout()
                .then(function(data, status, headers, config) {
                    $scope.userInfo.username = "";
                    $scope.userInfo.logged = false;
                    growl.success("Successfully logged out");
                });
        };

        $scope.$on('IdleTimeout', function() {
            if ($scope.userInfo.logged) {
                var modalInstance = $uibModal.open({
                    templateUrl: "templates/session-timed-out.tpl.html",
                    controller: "LoginCtrl"
                });
                $scope.userInfo = LogInfo.getUsername();
            }
        });
    }
]);
