var smtpDescription = angular.module('ttt.hisp.smtp.description', []);

smtpDescription.controller('SmtpDescriptionCtrl', ['$scope', 'ReplaceDomain','PropFactory','$stateParams',  'SMTPTestCasesDescription',
	function($scope,ReplaceDomain,PropFactory, $stateParams, SMTPTestCasesDescription) {
		$scope.test_id = parseInt($stateParams.id, 10);

		$scope.fieldInput = {};

		PropFactory.get(function(result) {
			$scope.ettDomain = result.data;
		});

		SMTPTestCasesDescription.getTestCasesDescription(function(response) {
			var result = response.data;

			angular.forEach(result.tests, function(test) {
				test = ReplaceDomain.getReplacedDomain(test,$scope.ettDomain);
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
