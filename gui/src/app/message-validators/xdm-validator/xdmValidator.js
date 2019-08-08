var xdmValidator = angular.module('ttt.direct.xdmValidator', []);

ccdaValidator.controller('XDMValidatorCtrl', ['$scope', 'XDMValidatorFactory', '$state', 'ApiUrl',
    function($scope, XDMValidatorFactory, $state, ApiUrl) {

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
            "messageFilePath": ""
        };

        $scope.successMessage = function(message) {
            $scope.fileInfo = angular.fromJson(message);
            $scope.validator.messageFilePath = $scope.fileInfo.flowRelativePath;
        };

        $scope.resetMessage = function() {
            $scope.fileInfo = {};
            $scope.validator.messageFilePath = "";
        };

        $scope.validate = function() {
            $scope.laddaLoading = true;
            XDMValidatorFactory.save($scope.validator, function(data) {
                $scope.laddaLoading = false;
                $scope.xdmResult = data;
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
