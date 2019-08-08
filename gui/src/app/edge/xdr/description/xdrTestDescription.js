var smtpDescription = angular.module('ttt.edge.xdr.description', []);

smtpDescription.controller('XdrDescriptionCtrl', ['$scope', '$stateParams',  'XDRTestCasesDescription','ReplaceDomain','PropFactory',
	function($scope, $stateParams, XDRTestCasesDescription,ReplaceDomain,PropFactory) {
		$scope.test_id = $stateParams.id;
		$scope.testObj =  $stateParams.testObj;
		$scope.testObjDesc = $stateParams.testObj['Purpose/Description'];


		PropFactory.get(function(result) {
			$scope.ettDomain = result.data;
		});

		$scope.testObjDesc = ReplaceDomain.getReplacedDomain($scope.testObjDesc,$scope.ettDomain);
		$scope.testObj = ReplaceDomain.getReplacedDomain($scope.testObj,$scope.ettDomain);

		if ($scope.testObjDesc){
			$scope.testObjDesc =  $stateParams.testObj['Purpose/Description'].replace(/[\r\n]+/g, '</p><p>');
		}
		$scope.fieldInput = {};

		XDRTestCasesDescription.getTestCasesDescription(function(response) {
			var result = response.data;

			angular.forEach(result, function(test) {
            test = ReplaceDomain.getReplacedDomain(test,$scope.ettDomain);
				if (test.ID === $scope.test_id) {
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
