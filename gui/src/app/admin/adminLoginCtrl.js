/*Admin Login Controller */

var loginCtrl = angular.module('ttt.adminLoginCtrl', []);

loginCtrl.controller('AdminLoginCtrl', ['$scope', '$timeout', 'AdminLogin', 'growl', '$uibModalInstance', '$state',
	function($scope, $timeout, AdminLogin, growl, $uibModalInstance, $state) {
		$scope.alerts = [];

		$scope.modalTitle = "AdminLogin";
		$scope.buttonSuccess = "Login";

		$scope.repeatPassword = {
			password: ""
		};

		$scope.adminlogin = {
			username: "",
			password: ""
		};
		$scope.modalType = "login";
		$scope.newUser = {
			username: "",
			password: ""
		};

		$scope.cancel = function() {
			$uibModalInstance.dismiss("Cancel");
		};

		$scope.close = function(result) {
			$uibModalInstance.close(result);
		};

		$scope.save = function() {
			AdminLogin.adminlogin($scope.adminlogin).
			then(function(data, status, headers, config) {
				$scope.userInfo = {
					"username": $scope.adminlogin.username,
					"logged": true
				};
				growl.success("Successfully logged in", {});
				$scope.close($scope.userInfo);
			}, function(data, status, headers, config) {
				showAlert('danger', 'Wrong username or password !');
			});
		};


		$scope.closeAlert = function() {
			$scope.alerts = [];
			$timeout.cancel($scope.timeout);
		};

		function showAlert(type, msg) {
			$scope.alerts = [];
			$scope.alerts.push({
				type: type,
				msg: msg
			});
			$scope.timeout = $timeout($scope.closeAlert, 60000);
		}

		$scope.hitEnter = function(evt) {
			if (angular.equals(evt.keyCode, 13) && !(angular.equals($scope.adminlogin.username, null) || angular.equals($scope.adminlogin.username, ''))) {
				$scope.save();
			}
		}; // end hitEnter

	}
]);
