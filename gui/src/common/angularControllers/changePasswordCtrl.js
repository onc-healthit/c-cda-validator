var changePasswordCtrl = angular.module('ttt.changePassword', []);

changePasswordCtrl.controller('ChangePasswordCtrl', ['$scope', 'ChangePassword', 'growl',
    function($scope, ChangePassword, growl) {

        $scope.changePassword = function() {
            if ($scope.newPassword !== $scope.confirmPassword) {
                throw {
                    code: "No Code",
                    url: "ChangePassword",
                    message: "New password does not match the confirm password"
                };
            } else {
                $scope.laddaLoading = true;

                var passToSend = {
                    "oldPassword": $scope.oldPassword,
                    "newPassword": $scope.newPassword
                };

                if ($scope.oldPassword === null || $scope.oldPassword === undefined) {
                    passToSend.oldPassword = "";
                } else if ($scope.newPassword === null || $scope.newPassword === undefined) {
                    passToSend.newPassword = "";
                }
                ChangePassword.save(passToSend, function(data) {
                    $scope.laddaLoading = false;
                    growl.success("Password successfully changed!");
                }, function(data) {
                    $scope.laddaLoading = false;
                    throw {
                        code: data.data.code,
                        url: data.data.url,
                        message: data.data.message
                    };
                });
            }
        };
    }
]);
