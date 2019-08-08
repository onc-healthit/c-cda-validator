var ccdaValidator = angular.module('ttt.direct.ccdar2Validator', []);

ccdaValidator.controller('CCDAR2ValidatorCtrl', ['$scope', 'CCDAR2ValidatorFactory', '$state', 'ApiUrl', 'CCDAR21Documents','$filter', 'CCDADocumentsFactory','$location','$anchorScroll',
    function($scope, CCDAR2ValidatorFactory, $state, ApiUrl, CCDAR21Documents, $filter,CCDADocumentsFactory,$location,$anchorScroll) {

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


        $scope.fileInfoCdaIg = {
            "flowChunkNumber": "",
            "flowChunkSize": "",
            "flowCurrentChunkSize": "",
            "flowTotalSize": "",
            "flowIdentifier": "",
            "flowFilename": "",
            "flowRelativePath": "",
            "flowTotalChunks": ""
        };
        $scope.sutRole = "sender";

        $scope.sutSenderRole ="Sender SUT Test Data";

		$scope.objective = [];

		$scope.filename = [];

        $scope.ccdaData = {};
        CCDAR21Documents.getCcdaDocuments(function(data) {
            $scope.ccdaDataSender = data.data.sender;
            // $scope.ccdaData = $scope.ccdaDataSender;
            $scope.ccdaDataReceiver = data.data.receiver;
        });

        $scope.ccdaSenderData = {};
        CCDADocumentsFactory.get(function(data) {
          $scope.ccdaDocuments = data;
            if (data !== null) {
                $scope.ccdaSenderData = $scope.ccdaDocuments[Object.keys(data)[0]];
            }
        });
        CCDADocumentsFactory.get(function(data) {
          $scope.ccdaDocuments = data;
            if (data !== null) {
                $scope.sutRole = Object.keys(data)[0];
                 $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                $scope.ccdaSelectData = $scope.ccdaData.dirs;
            }
        }, function(error) {
            console.log(error);
        });

        $scope.switchDocType = function(type) {
            $scope.sutRole = type;
            $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
            $scope.ccdaSelectData = $scope.ccdaData.dirs;
			$scope.ccdaDocument = "";
			$scope.filename.selected ="";
			$scope.objective.selected = "";
        };

        $scope.getCdaDocType = function() {
            $scope.ccdaDataCdaIg = $scope.ccdaDocuments[$scope.sutSenderRole];
        };

		$scope.changed = function(item) {
			$scope.type = item;
			$scope.ccdaFileNames = item.files;
			$scope.ccdaDocument = "";
			$scope.filename.selected ="";
		};

		$scope.setFileName = function(item) {
			$scope.ccdaDocument = item;
		};

        $scope.apiUrl = ApiUrl.get();

        $scope.validator = {
            "messageFilePath": "",
            "validationObjective": "",
            "referenceFileName": "",
            "messageFileCdaIg": "",
            "messageFile": ""
        };

        $scope.successMessage = function(message) {
			$scope.uploadCcda ="true";
            $scope.fileInfo = angular.fromJson(message);
            $scope.validator.messageFile = $scope.fileInfo.flowRelativePath;
        };

        $scope.resetMessage = function() {
            $scope.uploadCcda =undefined;
             $scope.validator.messageFile ="";
        };

        $scope.successMessageCdaIg = function(messageCdaIg) {
			$scope.uploadCdaIg ="true";
            $scope.fileInfoCdaIg = angular.fromJson(messageCdaIg);
            $scope.validator.messageFileCdaIg = $scope.fileInfoCdaIg.flowRelativePath;
        };

        $scope.resetMessageCdaIg = function() {
            $scope.uploadCdaIg =undefined;
            $scope.validator.messageFileCdaIg ="";
        };


$scope.gotodiv = function(anchor) {
    $location.hash(anchor);
   // call $anchorScroll()
    $anchorScroll();
};
        $scope.validateCdaIg = function() {
            $scope.laddaLoadingCdaIg = true;
            $scope.getCdaDocType();
            $scope.ccdaDocumentCdaIg =$filter('filter')($scope.ccdaDataCdaIg.dirs,  {"name": "CDA_IG_Plus_Vocab"})[0];
            $scope.ccdaDocumentCdaIg =$filter('filter')($scope.ccdaDocumentCdaIg.files,  {"name": "Readme.txt"})[0];
			if ($scope.uploadCdaIg!==undefined){
				$scope.validator.messageFilePath = $scope.validator.messageFileCdaIg;
				if ($scope.ccdaDocumentCdaIg.name && $scope.ccdaDocumentCdaIg.path) {
                    $scope.validator.validationObjective = $scope.ccdaDocumentCdaIg.path[$scope.ccdaDocumentCdaIg.path.length - 1];
                    $scope.validator.referenceFileName = $scope.ccdaDocumentCdaIg.name;
                    CCDAR2ValidatorFactory.save($scope.validator, function(data) {
                        $scope.laddaLoadingCdaIg = false;
                        $scope.ccdaappendfilename =    {ccdafilenaame : $scope.validator.referenceFileName};
                        $scope.ccdaResult = angular.extend(data, $scope.ccdaappendfilename);
                        $scope.gotodiv("ccdaValdReport");
                    }, function(data) {
                        $scope.laddaLoadingCdaIg = false;
                        throw {
                            code: data.data.code,
                            url: data.data.url,
                            message: data.data.message
                        };
                    });
                }
			}else{
                    $scope.laddaLoadingCdaIg = false;
                    throw {
                        code: "No code",
                        url: "",
                        message: "No C-CDA attachment uploaded "
                    };
                }
        };

        $scope.validate = function() {
            $scope.laddaLoading = true;
			if ($scope.uploadCcda !==undefined ){
				$scope.validator.messageFilePath = $scope.validator.messageFile;
				if ($scope.ccdaDocument) {
					if ($scope.ccdaDocument.name && $scope.ccdaDocument.path) {
						$scope.validator.validationObjective = $scope.ccdaDocument.path[$scope.ccdaDocument.path.length - 1];
						$scope.validator.referenceFileName = $scope.ccdaDocument.name;
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
						$scope.laddaLoading = false;
						throw {
							code: "No code",
							url: "",
							message: "You need to select a C-CDA document name"
						};
					}
				} else {
					$scope.laddaLoading = false;
					throw {
						code: "No code",
						url: "",
						message: "You need to select a C-CDA document type"
					};
				}
			}else{
                    $scope.laddaLoading = false;
                    throw {
                        code: "No code",
                        url: "",
                        message: "No C-CDA attachment uploaded "
                    };
                }
        };

        $scope.$watch('sutRole', function(newV, oldV) {

        });

    }
]);
