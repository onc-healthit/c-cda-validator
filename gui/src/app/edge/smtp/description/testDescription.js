var smtpDescription = angular.module('ttt.edge.smtp.description', []);

smtpDescription.controller('SmtpDescriptionCtrl', ['$scope', '$stateParams',  'SMTPTestCasesDescription',
	function($scope, $stateParams, SMTPTestCasesDescription) {
		$scope.test_id = parseInt($stateParams.id, 10);

		$scope.fieldInput = {};

		SMTPTestCasesDescription.getTestCasesDescription(function(response) {
			var result = response.data;
			angular.forEach(result.tests, function(test) {
				if (test.id === $scope.test_id) {
					$scope.specificTest = test;
				}
			});

		});

		$scope.$watch('specificTest.status', function(value) {
			$scope.laddaLoading = false;
			if (value === 'loading') {
				$scope.laddaLoading = true;
			} else if (value === 'success') {
				$scope.resultDisplay = true;
			} else if (value === 'fail') {
				$scope.resultDisplay = false;
			}
		});
	}
]);
