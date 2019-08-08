var edgeSmtp = angular.module('ttt.hisp.smtp', []);

edgeSmtp.config(['$stateProvider',
    function($stateProvider) {
        $stateProvider.state('hisp.smtp.main', {
                url: '',
                views: {
                    "smtp": {
                        templateUrl: 'hisp/smtp/smtpMain.tpl.html'
                    }
                }
            })
            .state('hisp.smtp.description', {
                url: '/description/:id',
                views: {
                    "smtp": {
                        templateUrl: 'hisp/smtp/description/testDescription.tpl.html'
                    }
                }
            })
            .state('hisp.smtp.logs', {
                url: '/logs',
                views: {
                    "smtp": {
                        templateUrl: 'edge/smtp/logs/testLog.tpl.html'
                    }
                },
				data: {
					sutEge: true
				}
            });
    }
]);

edgeSmtp.controller('HispSmtpCtrl', ['$scope', 'LogInfo', 'SMTPTestCasesDescription', 'SMTPTestCases', '$q', '$timeout', '$window', 'SMTPProfileFactory',
'SMTPLogFactory', 'growl','$location','$anchorScroll',
    function($scope, LogInfo, SMTPTestCasesDescription, SMTPTestCases, $q, $timeout, $window, SMTPProfileFactory,
    SMTPLogFactory, growl,$location,$anchorScroll) {

        $scope.senderTests = [];
        $scope.receiverTests = [];

        // Check type Edge or Hisp
        $scope.isEdge = $state.current.data.sutEge;
        if($scope.isEdge) {
            $scope.testSystem = "edge";
        } else {
            $scope.testSystem = "hisp";
        }
        $scope.edgeProtocol = $state.current.data.protocol;

        SMTPTestCasesDescription.getTestCasesDescription(function(response) {
            var result = response.data;

            $scope.testingMode = result.testingMode;
            angular.forEach(result.tests, function(test) {
                test.status = 'na';
                test.testResult = [{
                    "criteriaMet": "NA"
                }]; // Check the protocol first
                if (test.protocol === $scope.edgeProtocol) {
                    // Check the system edge or hisp
                    if ((test.sutEdge && $scope.isEdge) || (test.sutHisp && !$scope.isEdge)) {
                        if (test.sutRole.toLowerCase().indexOf('sender') >= 0) {
                            $scope.senderTests.push(test);
                        } else if (test.sutRole.toLowerCase().indexOf('receiver') >= 0) {
                            $scope.receiverTests.push(test);
                        }
                    }
                }
            });

        });

        $scope.refreshProfile = function(current) {
            SMTPProfileFactory.query(function(data) {
                if (data.length > 0) {
                    $scope.currentProfile = current || data[0];
                } else {
                    $scope.currentProfile = {};
                    $scope.currentProfile.profileName = "Profile_0";
                }
                $scope.profileList = data;
            });
        };

        $scope.refreshProfile();

        $scope.switchProfile = function(profile) {
            $scope.currentProfile = profile;
        };

        $scope.$watch('transactionType', function() {
            if ($scope.transactionType === 'sender') {
                $scope.testBench = $scope.senderTests;
            } else if($scope.transactionType === 'receiver'){
				$scope.testBench = $scope.receiverTests;
			} else {
                $scope.testBench =  [];
            }
        });

        $scope.scrollTop = function() {
            $window.scrollTo(0, 0);
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

        $scope.displayLog = function(test) {
            $scope.logToDisplay = test;
        };

        $scope.reset = function() {
            $scope.currentProfile.sutSMTPAddress = "";
            $scope.currentProfile.sutEmailAddress = "";
            $scope.currentProfile.sutUsername = "";
            $scope.currentProfile.sutPassword = "";
            $scope.currentProfile.profileName = "Profile_" + $scope.profileList.length;
        };

        $scope.saveProfile = function(profile) {
            SMTPProfileFactory.save(profile, function() {
                growl.success("Profile saved", {});
                $scope.refreshProfile(profile);
            }, function(data) {
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };

        $scope.removeProfile = function(profile_name) {
            SMTPProfileFactory.removeProf({
                'profile': profile_name
            }, function() {
                growl.success("Profile deleted", {});
                $scope.refreshProfile();
            }, function(data) {
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };

        $scope.startTest = function(test, fieldInput) {
            test.status = "loading";

            // Get profile info
            $scope.inputForTest = {
                "testCaseNumber": test.id,
                "sutSmtpAddress": $scope.currentProfile.sutSMTPAddress,
                "sutSmtpPort": $scope.sutSmtpPort,
                "tttSmtpPort": $scope.tttSmtpPort,
                "sutEmailAddress": $scope.currentProfile.sutEmailAddress,
                "tttEmailAddress": $scope.tttEmailAddress,
                "useTLS": true,
                "sutCommandTimeoutInSeconds": fieldInput.sutCommandTimeoutInSeconds,
                "sutUserName": $scope.currentProfile.sutUsername,
                "sutPassword": $scope.currentProfile.sutPassword,
                "tttUserName": "",
                "tttPassword": "",
                "tttSmtpAddress": $scope.tttSmtpAddress,
                "startTlsPort": 0
            };

            SMTPTestCases.startTest($scope.inputForTest, function(data) {
                // Set log result
                test.testResult = data;

                // See if the test passed or failed
                test.status = 'success';
                angular.forEach(data, function(res) {
                    if (res.criteriaMet === false) {
                        test.status = 'fail';
                    }
                });

                // Save log result to database
                // Get login first
                $scope.userInfo = LogInfo.getUsername();
                $scope.userInfo.$promise.then(function(logData) {
                    if (logData.logged) {
                        angular.forEach(test.testResult, function(smtpRes) {

                            // Transform testRequestResponse
                            var testResp = "";
                            angular.forEach(smtpRes.testRequestResponses, function(resValue, resKey) {
                                testResp += resKey;
                            });

                            var log = {
                                "testCaseNumber": test.name,
                                "criteriaMet": smtpRes.criteriaMet,
                                "testRequestsResponse": testResp,
                                "attachments": []
                            };

                            // Save the log to the profile
                            SMTPLogFactory.save({
                                'profile': $scope.currentProfile.profileName
                            }, log, function(saveResult) {

                            }, function(saveData) {
                                test.status = 'fail';
                                throw {
                                    code: saveData.data.code,
                                    url: saveData.data.url,
                                    message: saveData.data.message
                                };
                            });

                        });
                    }
                });


            }, function(data) {
                test.status = 'fail';
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };
    }
]);
