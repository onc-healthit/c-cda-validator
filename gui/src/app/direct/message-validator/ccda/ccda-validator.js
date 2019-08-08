var ccdaValidator = angular.module('ttt.direct.ccdaValidator', []);

ccdaValidator.controller('CCDAValidatorCtrl', ['$scope', 'CCDAR2ValidatorFactory', '$state', 'ApiUrl','$location','$anchorScroll',
	function($scope, CCDAR2ValidatorFactory, $state, ApiUrl,$location,$anchorScroll) {

		$scope.fileInfo = {
			"flowChunkNumber": "",
			"flowChunkSize": "",
			"flowCurrentChunkSize": "",
			"flowTotalSize": "",
			"flowIdentifier": "",
			"flowFilename": "",
			"flowRelativePath": "",
			"flowTotalChunks": ""
		};

		$scope.selectedItem = [];

		$scope.ccdaTypes = [{
			"selected": true,
			"desc": "Non-specific C-CDA",
			"code": "NonSpecificCCDA"

		}, {
			"selected": false,
			"desc": "Clinical Office Visit Summary - ONC 2014 Edition 170.314(e)(2) - Clinical Summary",
			"code": "ClinicalOfficeVisitSummary"
		}, {
			"selected": false,
			"desc": "Transitions Of Care Ambulatory Summary - ONC 2014 Edition 170.314(b)(2) Transition of Care/Referral Summary - For Ambulatory Care",
			"code": "TransitionsOfCareAmbulatorySummary"
		}, {
			"selected": false,
			"desc": "Transitions Of Care Ambulatory Summary - ONC 2014 Edition 170.314(b)(7) Data Portability - For Ambulatory Care",
			"code": "TransitionsOfCareAmbulatorySummary"
		}, {
			"selected": false,
			"desc": "Transitions Of Care Ambulatory Summary - ONC 2014 Edition 170.314(b)(1) Transition of Care Receive - For Ambulatory Care",
			"code": "TransitionsOfCareAmbulatorySummary"
		}, {
			"selected": false,
			"desc": "Transitions Of Care Inpatient Summary - ONC 2014 Edition 170.314(b)(2) Transition of Care/Referral Summary - For Inpatient Care",
			"code": "TransitionsOfCareInpatientSummary"
		}, {
			"selected": false,
			"desc": "Transitions Of Care Inpatient Summary - ONC 2014 Edition 170.314(b)(7) Data Portability - For Inpatient Care",
			"code": "TransitionsOfCareInpatientSummary"
		}, {
			"selected": false,
			"desc": "Transitions Of Care Inpatient Summary - ONC 2014 Edition 170.314(b)(1) Transition of Care Receive - For Inpatient Care",
			"code": "TransitionsOfCareInpatientSummary"
		}, {
			"selected": false,
			"desc": "VDT Ambulatory Summary - ONC 2014 Edition 170.314 (e)(1) Ambulatory Summary",
			"code": "VDTAmbulatorySummary"
		}, {
			"selected": false,
			"desc": "VDT Inpatient Summary - ONC 2014 Edition 170.314 (e)(1) Inpatient Summary",
			"code": "DTInpatientSummary"
		}];

		$scope.changed = function(item) {
			console.log(angular.toJson(item, true));
			$scope.type = item;
		};

		$scope.resetselection = function() {
			$scope.type = "";
			$scope.selectedItem.selected= "";
		};
		$scope.apiUrl = ApiUrl.get();

		$scope.validator = {
			"messageFilePath": "",
			"ccdaType": "",
            "validationObjective": "",
            "referenceFileName": "noscenariofile"
		};

		$scope.successMessage = function(message) {
			$scope.fileInfo = angular.fromJson(message);
			$scope.validator.messageFilePath = $scope.fileInfo.flowRelativePath;
		};

		$scope.resetMessage = function() {
			$scope.fileInfo = {};
			$scope.validator.messageFilePath = "";
		};

$scope.gotodiv = function(anchor) {
    $location.hash(anchor);
   // call $anchorScroll()
    $anchorScroll();
};

		$scope.validate = function() {
			console.log(angular.toJson($scope.type, true));
			if ($scope.type) {
				$scope.laddaLoading = true;
				$scope.validator.ccdaType = $scope.type.code;
                $scope.validator.validationObjective = $scope.type.code;
                $scope.validator.referenceFileName = $scope.fileInfo.flowFilename;
				CCDAR2ValidatorFactory.save($scope.validator, function(data) {
					$scope.laddaLoading = false;
                    $scope.ccdaappendfilename =    {ccdafilenaame : $scope.validator.referenceFileName};
					$scope.ccdaResult = angular.extend(data, $scope.ccdaappendfilename);
					$scope.gotodiv("ccdaValdReport");
				}, function(data) {
					$scope.laddaLoading = false;
					throw {
						code: data.data.code,
						url: data.data.url,
						message: data.data.message
					};
				});
			} else {
				throw {
					code: "0x0045",
					url: "C-CDA validator",
					message: "You have to select a C-CDA type"
				};
			}
		};

	}
]);
