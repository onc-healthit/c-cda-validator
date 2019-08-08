var directValidator = angular.module('ttt.direct.validator', []);

directValidator.controller('DirectValidatorCtrl', ['$scope', 'MessageValidatorFactory', '$state', 'ApiUrl',
    function($scope, MessageValidatorFactory, $state, ApiUrl) {

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

        $scope.apiUrl = ApiUrl.get();

        $scope.validator = {
            "messageFilePath": "",
            "certFilePath": "",
            "certPassword": ""
        };

        $scope.successMessage = function(message) {
            $scope.fileInfo = angular.fromJson(message);
            $scope.validator.messageFilePath = $scope.fileInfo.flowRelativePath;
        };

        $scope.successCert = function(message) {
            $scope.fileInfo = angular.fromJson(message);
            $scope.validator.certFilePath = $scope.fileInfo.flowRelativePath;
        };

        $scope.resetMessage = function() {
            $scope.fileInfo = {};
            $scope.validator.messageFilePath = "";
        };

        $scope.resetCert = function() {
            $scope.fileInfo = {};
            $scope.validator.certFilePath = "";
        };

		$scope.copyCcdaEmail = function(ccda, domain) {
			return ccda + "@" + domain;
		};

        $scope.validate = function() {
            $scope.laddaLoading = true;
            MessageValidatorFactory.save($scope.validator, function(data) {
                $scope.laddaLoading = false;
                $state.go('direct.report', {message_id: data.messageId});
            }, function(data) {
                $scope.laddaLoading = false;
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };

    }
]);
