var accountInfoCtrl = angular.module('ttt.accountInfo', []);

accountInfoCtrl.controller('AccountInfoCtrl', ['$scope', 'DirectEmailAddress', 'ContactEmailAddress', 'growl', 'LogInfo', 'SMTPProfileFactory',
	function($scope, DirectEmailAddress, ContactEmailAddress, growl, LogInfo, SMTPProfileFactory) {
		// Get username
		$scope.userInfo = LogInfo.getUsername();
		// Asynchronous call so do that when the back end answered with $promise
		$scope.userInfo.$promise.then(function(response) {
			// Get list of direct emails
			if ($scope.userInfo.logged) {
				$scope.directList = DirectEmailAddress.query();
			}
		});

		$scope.smtpProfiles = SMTPProfileFactory.query();
	}
]);
