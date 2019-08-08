var surveilh1 = angular.module('ttt.surveillance.surveilh1', []);

surveilh1.config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.state('surveillance.surveilh1.main', {
				url: '',
				views: {
					"surveilh1": {
						templateUrl: 'edge/smtp/smtpMain.tpl.html'
					}
				}
			})
			.state('surveillance.surveilh1.description', {
				url: '/description/:id',
				views: {
					"surveilh1": {
						templateUrl: 'edge/smtp/description/testDescription.tpl.html'
					}
				}
			})
			.state('surveillance.surveilh1.logs', {
				url: '/logs',
				views: {
					"surveilh1": {
						templateUrl: 'edge/smtp/logs/testLog.tpl.html'
					}
				}
			}
			);
	}
]);

surveilh1.controller('Surveilh1Ctrl', ['$scope', '$stateParams','LogInfo','growl','SMTPLogFactory','ApiUrl','SMTPTestCasesDescription','CriteriaDescription','SMTPTestCases','XDRTestCasesTemplate','XDRTestCases','XDRRunTestCases','SMTPProfileFactory','SettingsFactory', 'PropertiesFactory',  '$timeout','$window','CCDADocumentsFactory', 'DirectRICertFactory','DirectCertsLinkFactory','$filter','$state','ReplaceDomain','PropFactory','$location','$anchorScroll','XDRCheckStatus',
	function($scope, $stateParams, LogInfo,growl,SMTPLogFactory, ApiUrl,SMTPTestCasesDescription,CriteriaDescription,SMTPTestCases,XDRTestCasesTemplate,XDRTestCases,XDRRunTestCases,SMTPProfileFactory,SettingsFactory, PropertiesFactory, $timeout,$window,CCDADocumentsFactory, DirectRICertFactory,DirectCertsLinkFactory,$filter, $state,ReplaceDomain,PropFactory,$location,$anchorScroll,XDRCheckStatus) {
         $scope.paramCri =  $stateParams.paramCri;
         $scope.pageTitle= $state.current.data.pageTitle;
		$scope.filterCrit = $state.current.data.filterCrit;
		$scope.uploadOption = false;

		$scope.properties = PropertiesFactory.get(function(data) {
		// Smtp
		$scope.smtpTests = [];
		$scope.xdrTests = [];
		$scope.criterFilterObj = [];
		$scope.criteriaSelection  = null;
		$scope.isXdrTest = false;
		$scope.testSystem = "certification";
		$scope.viewObj = "certh1";
		$scope.edgeProtocol = "certh1";
		$scope.backToCriteria = 0;
		$scope.backToOption = 0;
		$scope.optionchange = 0;
		if ($scope.filterCrit == "h1"){
			$scope.edgeProtocol = "certh1";
			$scope.viewObj = "certh1";
		}
		if ($scope.filterCrit == "h2"){
			$scope.edgeProtocol = "certh2";
			$scope.viewObj = "certh2";
		}
		if ($scope.filterCrit == "b1"){
			$scope.edgeProtocol = "certb1";
			$scope.viewObj = "certb1";
		}

		PropFactory.get(function(result) {
			$scope.ettDomain = result.data;
		});

		XDRTestCasesTemplate.getTestCasesDescription(function(response) {
			var result = response.data;
			angular.forEach(result, function(test) {
				if (!test.status) {
					test.status = 'na';
				} else if(test.status === 'configure') {
					$scope.configureXdr(test);
				}
                test = ReplaceDomain.getReplacedDomain(test,$scope.ettDomain);
				$scope.xdrTests.push(test);
			});
		});

		$scope.configureXdr = function(test) {
			test.status = "loading";
			var properties = {};
			XDRTestCases.configure({
				id: test.id
			}, function(data) {
				if(data.content) {
					test.endpoint = data.content.value.endpoint;
					test.endpointTLS = data.content.value.endpointTLS;
				}
				test.status = 'na';
			}, function(data) {
				test.status = 'error';
				if (data.data) {
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				}
			});
		};
				$scope.apiUrl = ApiUrl.get();


		$scope.successUpload = function(message) {
			$scope.fileInfo = angular.fromJson(message);
			var validExts = new Array(".der", ".pem");
			var fileExt = $scope.fileInfo.flowFilename.substring($scope.fileInfo.flowFilename.lastIndexOf('.'));
			if (validExts.indexOf(fileExt) >= 0) {
				var certFilePath = $scope.fileInfo.flowRelativePath;
				DirectRICertFactory.save(certFilePath, function(data) {
					if (data.criteriaMet == "FALSE"){
						growl.error("Failed to Upload Certificate", {});
					}else{
						growl.success("Certificate Uploaded", {});
					}
				}, function(data) {
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				});
			}else{
				growl.error("Invalid file selected, valid files are "+validExts.toString()+" types.", {});
			}
		};

		CriteriaDescription.getCriteriaOptions(function(response) {
			var result = response.data;
            $scope.criteriaSelection = result;
			$scope.firstCriteriaSelection = $filter('filter')($scope.firstCriteriaSelection,  {"testList": 'h2'});

			$scope.filterObj = $filter('filter')($scope.criteriaSelection,  {"testList": $scope.filterCrit});

            $scope.backToCriteria = 0;

            if ($scope.paramCri !=null){
                $scope.backToCriteria = $scope.paramCri.backToCriteria;
                $scope.backToOption  = $scope.paramCri.backToOption;
            }

            $scope.criterFilterObj = $scope.filterObj;
            $scope.firstSelectedItem = $scope.firstCriteriaSelection[$scope.backToOption];
            $scope.onOptionChange($scope.firstSelectedItem);
            $scope.selectedItem = $scope.filterObj[$scope.backToCriteria];
		});

		SMTPTestCasesDescription.getTestCasesDescription(function(response) {
			var result = response.data;
			$scope.testingMode = result.testingMode;
			angular.forEach(response.data.tests, function(test) {
				test.status = 'na';
				test.testResult = [{
					"criteriaMet": "NA"
				}];
                test = ReplaceDomain.getReplacedDomain(test,$scope.ettDomain);
				$scope.smtpTests.push(test);
			});
		});

           $scope.firstCriteriaSelection= [
            {  name: "All", testList:['h2'],selectOption:'ALL'},
            {  name: "Setup", testList:['h2'],selectOption:'A'},
            {  name: "Send", testList:['h2'],selectOption:'B'},
            {  name: "Send - Delivery Notification for Direct", testList:['h2'],selectOption:'9'},
            {  name: "Send using Direct+XDM", testList:['h2'],selectOption:'2'},
            {  name: "Send conversion XDR", testList:['h2'],selectOption:'3'},
            {  name: "Send using Edge Protocol",testList:['h2'],selectOption:'4'},
            {  name: "Receive", testList:['h2'],selectOption:'5'},
            {  name: "Receive - Delivery Notification in Direct",testList:['h2'],selectOption:'10'},
            {  name: "Receive using Direct+XDM",testList:['h2'],selectOption:'6'},
            {  name: "Receive conversion XDR",testList:['h2'],selectOption:'7'},
            {  name: "Receive using Edge Protocol",testList:['h2'],selectOption:'8'}
            ];

           $scope.secondCriteriaSelection= [
            {  name: "Direct", testList:['h2'],selectOption:'ALL'},
            {  name: "Edge", testList:['h2'],selectOption:'1'}
            ];
            });

            $scope.onOptionChange= function(selectedItem) {
                 $scope.optionchange = $scope.firstCriteriaSelection.indexOf( selectedItem );
                 $scope.testBench =  [];
                 $scope.filterObj = $filter('filter')($scope.criterFilterObj, {selectOption: selectedItem.selectOption});

                 if (selectedItem.selectOption === "ALL"){
                    $scope.filterObj = $scope.criterFilterObj;
                 }
                 $scope.selectedItem = $scope.filterObj[0];
             };


            $scope.onCategoryChange= function(selectedItem) {
                 $scope.selectedCrit = $scope.filterObj.indexOf( selectedItem );
                 $scope.testBench =  [];
                 $scope.isXdrTest = false;
                /* if (selectedItem.criteria === "'h1-1'"){
                        $scope.uploadOption = true;
                 }*/
                 //console.log("selectedItem :::"+angular.toJson(selectedItem, true));
                 $scope.uploadOption = selectedItem.uploadOption;
                 $scope.isXdrTest = selectedItem.xdrTest;
                 $scope.redirectLink = selectedItem.redirect;
                 $scope.openInNewWindow = "";
                 if ($scope.isXdrTest){
                     $scope.testchange = $filter('filter')($scope.xdrTests, {criteria: selectedItem.criteria});
                 }else{
                     $scope.testchange = $filter('filter')($scope.smtpTests, {criteria: selectedItem.criteria});
                 }
                 $scope.testBench = $scope.testchange;
                 //console.log("$scope.testBench :::"+angular.toJson($scope.testBench, true));
               if (selectedItem.redirect){
                    $scope.openInNewWindow  = selectedItem.redirect.newWindow;
                    if ($scope.openInNewWindow){
                           window.open(selectedItem.redirect.hrefvalue, '_blank');
                     }else{
                            $state.go(selectedItem.redirect.hrefvalue, {paramsObj:{"prevPage":selectedItem.redirect.hreflabel,"goBackTo":selectedItem.redirect.hrefback,"backToCriteria":$scope.selectedCrit,"backToOption":$scope.optionchange}});
                     }
               }
             };



		$scope.scrollTop = function() {
			$window.scrollTo(0, 0);
		};
		$scope.scrollToId = function(testcaseid) {
			$state.go($scope.testSystem + '.' + $scope.edgeProtocol + '.main');
				$timeout(function() {
					$location.hash("test_" + testcaseid.name);
					$anchorScroll();
				}, 0);
		};

		$scope.displayLog = function(test) {
			$scope.logToDisplay = test;
		};

		$scope.resetTest = function(test) {
			if (!test.testResult.$resolved) {
				test.testResult.cancel();
			}
			test.status = 'na';
		};


		$scope.refreshProfile = function(current) {
			SMTPProfileFactory.query(function(data) {
				if (data.length > 0) {
					$scope.currentProfile = current || data[0];
                    if ($scope.currentProfile.useTLS === null){
                        $scope.currentProfile.useTLS = true;
                    }
				} else {
					$scope.currentProfile = {};
					$scope.currentProfile.profileName = "Default Profile";
					$scope.currentProfile.useTLS = true;
				}
				$scope.profileList = data;
			});
		};

		$scope.refreshProfile();

		$scope.switchProfile = function(profile) {
			$scope.currentProfile = profile;
		};

		$scope.resetProfile = function() {
			$scope.currentProfile.sutSMTPAddress = "";
			$scope.currentProfile.sutEmailAddress = "";
			$scope.currentProfile.sutUsername = "";
			$scope.currentProfile.sutPassword = "";
			$scope.currentProfile.useTLS = true;
			$scope.currentProfile.profileName = "Default Profile " + $scope.profileList.length;
			$scope.refreshProfile($scope.currentProfile);
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
		$scope.validateManual = function(test, validation) {
			test.status = validation;
			$scope.logTestData(test);
		};

		$scope.startTest = function(test, fieldInput) {
			// Get CCDA R2 validation objectives if exists
			var ccdaReferenceFilename = "";
			var ccdaValidationObjective = "";
			var fileLink = "";
			if (test.ccdaFileRequired && (!fieldInput.ccdaDocument)){
				throw {
					code: "Error",
					url: "",
					message: "Please select C-CDA Document Type"
               };
			}
			if (fieldInput.ccdaDocument) {
				ccdaReferenceFilename = fieldInput.ccdaDocument.name || "";
				fileLink = fieldInput.ccdaDocument.link || "";
				ccdaValidationObjective = fieldInput.ccdaDocument.path[fieldInput.ccdaDocument.path.length - 1] || "";
			}

			var previousTR = null;
            if (!angular.isUndefined(test.testResult)){
			if (test.testResult.length > 0) {
				if (test.testResult[0].criteriaMet !== "NA") {
					previousTR = test.testResult[0];
				}
			}
             }

			// Get profile info
			$scope.inputForTest = {
				"testCaseNumber": test.id,
				"sutSmtpAddress": $scope.currentProfile.sutSMTPAddress,
				"sutSmtpPort": $scope.sutSmtpPort,
				"tttSmtpPort": $scope.tttSmtpPort,
				"sutEmailAddress": $scope.currentProfile.sutEmailAddress,
				"tttEmailAddress": $scope.tttEmailAddress,
				"useTLS": $scope.currentProfile.useTLS,
				"sutCommandTimeoutInSeconds": fieldInput.sutCommandTimeoutInSeconds,
				"sutUserName": $scope.currentProfile.sutUsername,
				"sutPassword": $scope.currentProfile.sutPassword,
				"tttUserName": "",
				"tttPassword": "",
				"tttSmtpAddress": $scope.tttSmtpAddress,
				"startTlsPort": 0,
				"status": test.status,
				"attachmentType": fieldInput.attachmentType,
				"ccdaReferenceFilename": ccdaReferenceFilename,
				"ccdaValidationObjective": ccdaValidationObjective,
				"ccdaFileLink": fileLink,
				"previousResult": previousTR || null
			};

			// Set status to loading for loading UI
			test.status = "loading";

			test.testResult = SMTPTestCases.startTest($scope.inputForTest, function(data) {
				// Set log result
				test.testResult = data;

				// See if the test passed, failed, pending or manual
				test.status = 'success';
				angular.forEach(data, function(res) {
					if (res.criteriaMet === 'FALSE') {
						test.status = 'fail';
					} else if (res.criteriaMet === 'MANUAL') {
						test.status = 'manual';
					} else if (res.criteriaMet === 'STEP2') {
						test.status = 'fetching';
					} else if (res.criteriaMet === 'RETRY') {
						test.status = 'retry';
					}
				});
				$scope.logTestData(test);

			}, function(data) {
				test.status = 'fail';
				if (data.data) {
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				}
			});
		};

	$scope.logTestData = function(test) {
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

						if (test.status === 'fail' || test.status === 'success') {
							var isCriteriaMet = false;
							if (test.status === 'success') {
								isCriteriaMet = true;
							}
							var log = {
								"testCaseNumber": test.name,
								"criteriaMet": isCriteriaMet,
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
						}

					});
				}
			});
		};

		$scope.getBlob = function(filename, attachment) {
			var contentType = 'text/plain';
			if (filename.indexOf('.xml') > -1) {
				contentType = 'application/xml';
			}
			return new Blob([attachment], {
				type: contentType
			});
		};

		$scope.validateXdr = function(test, validation) {
			test.status = validation;
		};

		$scope.reset = function(test) {
			console.log("inside reset for xdr......");
			if (!test.results.$resolved) {
				test.results.cancel();
			}
			test.status = 'na';
		};

		$scope.runXdr = function(test) {
			test.status = "loading";
			var properties = {};
			angular.forEach(test.inputs, function(property) {
				properties[property.key] = test[property.key];
			});
			test.results = XDRRunTestCases.run({
				id: test.id
			}, properties, function(data) {
				test.results = data;
				if (data.content !== null && data.content !== undefined) {
					if (data.content.criteriaMet.toLowerCase() === 'pending') {
						test.status = "pending";
						if(data.content) {
							test.endpoint = data.content.value.endpoint;
							test.endpointTLS = data.content.value.endpointTLS;
						}
					} else if (data.content.criteriaMet.toLowerCase() === 'manual') {
						test.status = "manual";
					} else if (data.content.criteriaMet.toLowerCase() === 'passed') {
						test.status = "success";
					} else {
						test.status = "error";
					}
				} else {
					if (data.status.toLowerCase() === 'passed') {
						test.status = "success";
					} else {
						test.status = "error";
						throw {
							code: '0x0020',
							url: 'xdr',
							message: data.message
						};
					}
				}
			}, function(data) {
				test.status = 'error';
				if (data.data) {
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				}
			});
		};

		$scope.checkXdrStatus = function(test) {
			test.status = "loading";
			test.results = XDRCheckStatus.checkStatus({
				id: test.id
			}, function(data) {
				test.results = data;
				if (data.content.criteriaMet.toLowerCase() === "passed") {
					test.status = "success";
				} else if (data.content.criteriaMet.toLowerCase() === "failed") {
					test.status = "error";
				} else if (data.content.criteriaMet.toLowerCase() === "pending") {
					test.status = "pending";
				} else if (data.content.criteriaMet.toLowerCase() === "manual") {
					test.status = "manual";
				} else {
					test.status = "error";
				}
			}, function(data) {
				test.status = "error";
				if (data.data) {
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				}
			});
		};

	} // end of main function

]);
