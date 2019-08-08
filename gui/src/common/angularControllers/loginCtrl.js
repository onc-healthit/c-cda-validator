/* Login Controller */

var loginCtrl = angular.module('ttt.loginCtrl', []);

loginCtrl.controller('LoginCtrl', ['$scope', '$timeout', 'CreateUser', 'Login', 'growl', '$uibModalInstance', '$state', 'ForgotPassword',
	function($scope, $timeout, CreateUser, Login, growl, $uibModalInstance, $state, ForgotPassword) {

		$scope.alerts = [];

		$scope.modalTitle = "Login";
		$scope.buttonSuccess = "Login";

		$scope.repeatPassword = {
			password: ""
		};

		$scope.login = {
			username: "",
			password: ""
		};
		$scope.modalType = "login";
		$scope.newUser = {
			username: "",
			password: ""
		};

		$scope.forgot = {};

		$scope.usernameForgot = "";

		$scope.cancel = function() {
			$uibModalInstance.dismiss("Cancel");
		};

		$scope.close = function(result) {
			$uibModalInstance.close(result);
		};

		$scope.save = function() {
			Login.login($scope.login).
			then(function(data, status, headers, config) {
				$scope.userInfo = {
					"username": $scope.login.username,
					"logged": true
				};
				growl.success("Successfully logged in", {});
				$state.go($state.current, {}, {
					reload: true
				});
				$scope.close($scope.userInfo);
			}, function(data, status, headers, config) {
				showAlert('danger', 'Wrong username or password !');
			});
		};

		$scope.signUpPage = function() {
			$scope.modalTitle = "Sign Up";
			$scope.buttonSuccess = "Sign Up";
			$scope.modalType = "signUp";
			$scope.save = function() {
				if ($scope.newUser.password != $scope.repeatPassword.password) {
					showAlert('danger', 'Passwords don\'t match');
				} else if (!$scope.newUser.username || $scope.newUser.username === "") {
					showAlert('danger', 'Username must be an email');
				} else if ($scope.newUser.password === "") {
					showAlert('danger', 'Please enter Password');
				} else {
					CreateUser.createUser($scope.newUser,

						function(data) {
							Login.login($scope.newUser).
							success(function(data, status, headers, config) {
								$scope.isSigningUp = false;
								growl.success("Successfully registered");

								// Reload page to show login
								$state.go($state.current, {}, {
									reload: true
								});
								$scope.close("Ok");
							}).
							error(function(data, status, headers, config) {
								showAlert('danger', 'Registration has encounter a problem!');
							});
						},
						function(data) {
							showAlert('danger', data.data.message);
						}
					);
				}
			};
		};

		$scope.forgotPage = function() {
			$scope.modalTitle = "Forgot Password";
			$scope.buttonSuccess = "Send";
			$scope.modalType = "forgotPass";
			$scope.save = function() {
				if (!$scope.forgot.username || $scope.forgot.username === "") {
					showAlert('danger', 'Username must be an email');
				} else {
					ForgotPassword.save($scope.forgot.username, function(data) {
						growl.success("Email sent");
						$scope.close("Ok");
					}, function(data) {
						showAlert('danger', data.data.message);
					});
				}
			};
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
			if (angular.equals(evt.keyCode, 13) && !(angular.equals($scope.login.username, null) || angular.equals($scope.login.username, ''))) {
				$scope.save();
			}
		}; // end hitEnter

	}
]);
