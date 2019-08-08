var directSend = angular.module('ttt.direct.send', []);

directSend.controller('DirectSendCtrl', ['$scope', 'SettingsFactory', 'SendDirect', 'growl', 'LogInfo', 'DirectEmailAddress', 'ApiUrl',
    function($scope, SettingsFactory, SendDirect, growl, LogInfo, DirectEmailAddress, ApiUrl) {

        $scope.apiUrl = ApiUrl.get();

        $scope.message = {};
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

        // Get Direct address list for connected user
        // Get username
        $scope.userInfo = LogInfo.getUsername();
        // Asynchronous call so do that when the back end answered with $promise
        $scope.userInfo.$promise.then(function(response) {
            // Get list of direct emails
            if ($scope.userInfo.logged) {
                $scope.directList = DirectEmailAddress.query();
            } else {
                $scope.directList = [];
            }
        });

        // In order to disable the send button when request is pending
        $scope.requestPending = false;
        $scope.sample = {};
        $scope.isWrapped = true;
        $scope.certType = 'GOOD';

        $scope.success = function(message) {
            $scope.fileInfo = angular.fromJson(message);
            $scope.message.CertFilePath = $scope.fileInfo.flowRelativePath;
        };

        $scope.reset = function() {
            $scope.fileInfo = {};
            $scope.message.CertFilePath = undefined;
        };

        $scope.successCcda = function(message) {
            $scope.fileInfo = angular.fromJson(message);
            $scope.message.ownCcdaPath = $scope.fileInfo.flowRelativePath;
        };

        $scope.resetCcda = function() {
            $scope.ownCcda = {};
        };

        $scope.clear = function() {
            $scope.sample.selected = undefined;
        };

        $scope.toggleWrapped = function(bool) {
            $scope.isWrapped = bool;
        };


        $scope.toggleCertType = function(type) {
            if (type === 'INVALID_DIGEST') {
                $scope.certType = '';
                $scope.invalidDigest = true;
            } else {
                $scope.invalidDigest = false;
                $scope.certType = type;
            }
        };

        $scope.send = function() {
            $scope.laddaLoading = true;

            if ($scope.sample.selected === undefined) {
                $scope.message.attachmentFile = "CCDA_Ambulatory.xml";
            } else {
                $scope.message.attachmentFile = $scope.sample.selected.name;
            }

            $scope.fromAddress = $scope.message.fromAddress + '@' + $scope.properties.domainName;
            if ($scope.message.textMessage === null || $scope.message.textMessage === undefined || $scope.message.textMessage === "") {
                $scope.message.textMessage = "Test Message";
            }
            if ($scope.message.subject === null || $scope.message.subject === undefined || $scope.message.subject === "") {
                $scope.message.subject = "Test Message";
            }

            $scope.msgToSend = {
                "textMessage": $scope.message.textMessage,
                "subject": $scope.message.subject,
                "fromAddress": $scope.fromAddress,
                "toAddress": $scope.message.toAddress,
                "attachmentFile": $scope.message.attachmentFile,
                "ownCcdaAttachment": $scope.message.ownCcdaPath,
                "signingCert": $scope.certType,
                "signingCertPassword": "",
                "encryptionCert": $scope.message.CertFilePath,
                "wrapped": $scope.isWrapped,
                "invalidDigest": $scope.invalidDigest || false,
                "digestAlgo": $scope.digestAlgo || 'sha1'
            };

            SendDirect.save($scope.msgToSend, function(data) {
                $scope.laddaLoading = false;
                if (data.result) {
                    growl.success("Message successfully sent!");
                } else {
                    throw {
                        code: '0x0004',
                        message: "Cannot send the message"
                    };
                }
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
