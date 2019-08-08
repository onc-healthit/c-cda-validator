var directReport = angular.module('ttt.direct.report', []);

directReport.controller('DirectReportCtrl', ['$scope', '$stateParams', 'ValidationReportFactory', 'ReportMessageContentFactory', '$sce', 'ApiUrl', 'CCDAReportFactory',
    function($scope, $stateParams, ValidationReportFactory, ReportMessageContentFactory, $sce, ApiUrl, CCDAReportFactory) {
        $scope.message_id = $stateParams.message_id;

        $scope.detailedHeader = [
            "Name", "Status", "DTS", "Found", "Expected", "RFC"
        ];

        $scope.apiUrl = ApiUrl.get();

        $scope.tree_data = [];

        $scope.globalErrors = [];

        if ($scope.message_id != null) {
            $scope.report = ValidationReportFactory.get({
                    "messageId": $scope.message_id
                }, function(data) {
                    // Success
                    $scope.tree_data = [$scope.report];
                    $scope.changeDetail($scope.report);



                    // Get Message Content
                    $scope.partsRawContent = ReportMessageContentFactory.query({
                        "messageId": $scope.message_id
                    }, function(data) {
                        // Get CCDA Report
                        $scope.ccdaReports = CCDAReportFactory.query({
                            "messageId": $scope.message_id
                        }, function(data) {
                            // Success
                            $scope.ccdaValidationType = [];
                            // Check if it is CCDA R2 validation
                            $scope.ccdaValidationType.push("CCDA Validation R1.1");
                               for (var i = 0; i < data.length; i++) {
                                  $scope.ccdaValidationType.push("CCDA Validation R1.1");
                                  if (data[i].ccdaReport.ccdaRType === "r1") {
                                      $scope.ccdaValidationType[i] = "CCDA Validation R1.1";
                                   } else if (data[i].ccdaReport.ccdaRType  ==="r2"){
                                       $scope.ccdaValidationType[i] = "CCDA Validation R2.1";
                                   }else if (data[i].filename.startsWith("XDM_")) {
                                      $scope.ccdaValidationType[i] = "XDM Validation";
                                   }
                                }
                        }, function(data) {
                            // Error handling
                            throw {
                                code: data.data.code,
                                url: data.data.url,
                                message: data.data.message
                            };
                        });

                    });

                },
                function(data) {
                    throw {
                        code: data.data.code,
                        url: data.data.url,
                        message: data.data.message
                    };
                });

        }


        $scope.changeDetail = function(selected) {
            $scope.selectedPart = selected;
            angular.forEach(selected.details, function(detail) {
                if ((typeof detail.rfc) === "string") {
                    detail.rfc = $scope.getRfcNameAndLink(detail.rfc);
                }
                if(detail.name === 'Unexpected Error') {
                    $scope.globalErrors.push(detail);
                }
            });
        };

        $scope.getRfcNameAndLink = function(rfc) {
            var rfcNameAndLink = [];
            if (rfc === undefined) {
                return [];
            } else if (rfc === null) {
                return [];
            }
            if (rfc.indexOf(';') > -1) {
                var rfc_split = rfc.split(';');
                for (var i = 0; i < rfc_split.length; i += 2) {
                    rfcNameAndLink.push({
                        'name': rfc_split[i],
                        'link': rfc_split[i + 1]
                    });
                }
                return rfcNameAndLink;
            } else {
                return [{
                    name: rfc,
                    link: ""
                }];
            }
        };

        $scope.renderHtml = function(htmlCode) {
            return $sce.trustAsHtml(htmlCode);
        };

    }
]);
