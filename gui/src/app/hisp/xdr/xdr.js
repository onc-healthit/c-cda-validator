var edgeXdr = angular.module('ttt.hisp.xdr', []);

edgeXdr.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('hisp.xdr.main', {
            url: '',
            views: {
                "xdr": {
                    templateUrl: 'hisp/xdr/xdrMain.tpl.html'
                }
            }
        })
        .state('hisp.xdr.xdrdescription', {
            url: '/description',
			params: {
				id: null,
				testObj: null
			},
            views: {
                "xdr": {
                    templateUrl: 'edge/xdr/description/xdrTestDescription.tpl.html'
                }
            }
        })
        .state('hisp.xdr.xdrlogs', {
            url: '/logs',
            views: {
                "xdr": {
                    templateUrl: 'edge/xdr/logs/xdrTestLog.tpl.html'
                }
            }
        });

    }
]);

edgeXdr.controller('HispXdrCtrl', ['$scope', 'XDRTestCasesDescription', 'growl', '$q', '$timeout', 'XDRTestCases', 'XDRCheckStatus','$location','$anchorScroll',
    function($scope, XDRTestCasesDescription, growl, $q, $timeout, XDRTestCases, XDRCheckStatus,$location,$anchorScroll) {

        $scope.senderTests = [];
        $scope.receiverTests = [];

        // Check type Edge or Hisp
        $scope.isEdge = $state.current.data.sutEge;
        if ($scope.isEdge) {
            $scope.testSystem = "edge";
        } else {
             $scope.testSystem = "hisp";
        }
        $scope.edgeProtocol =  $state.current.data.protocol;

        XDRTestCasesDescription.getTestCasesDescription(function(response) {
            var result = response.data;

            angular.forEach(result, function(test) {
                test.status = 'na';
                if (test['SUT: Sender/ Receiver'].toLowerCase().indexOf('sender') >= 0) {
                    $scope.senderTests.push(test);
                } else if (test['SUT: Sender/ Receiver'].toLowerCase().indexOf('receiver') >= 0) {
                    $scope.receiverTests.push(test);
                }
            });

        });

        $scope.displayGrowl = function(text) {
            growl.success(text);
        };

        $scope.scrollToId = function(testcaseid) {
             $state.go($scope.testSystem + '.' + $scope.edgeProtocol + '.main');
              // We need to wait for the animation to finish
               $timeout(function() {
               // set the location.hash to the id of
               // the element you wish to scroll to.
                      $location.hash("test_" + testcaseid.name);
                      // call anchorScroll()
                       $anchorScroll();
                }, 0);
        };

        $scope.$watch('transactionType', function() {
            if ($scope.transactionType === 'sender') {
                $scope.testBench = $scope.senderTests;
            }else if($scope.transactionType === 'receiver'){
				$scope.testBench = $scope.receiverTests;
			}else {
                $scope.testBench =  [];
            }
        });

        $scope.runXdr = function(test) {
            test.status = "loading";
            XDRTestCases.save({
                id: test.ID
            }, {
                endpoint: test.endpoint
            }, function(data) {
                test.results = data;
                if (data.status.toLowerCase() === 'error') {
                    test.status = "error";
                } else if ($scope.transactionType === 'sender') {
                    test.status = "pending";
                } else {
                    test.status = "success";
                }
            }, function(data) {
                test.status = 'error';
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };

        $scope.checkXdrStatus = function(test) {
            test.status = "loading";
            XDRCheckStatus.get({}, {
                id: test.ID
            }, function(data) {
                test.results = data;
                if (data.content === "PASSED") {
                    test.status = "success";
                } else if (data.content === "FAILED") {
                    test.status = "error";
                } else if (data.content === "PENDING") {
                    test.status = "pending";
                }
            }, function(data) {
                test.status = "error";
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };

    }
]);
