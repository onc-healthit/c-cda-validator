var xdmValidator = angular.module('ttt.direct.xdrValidator', []);

ccdaValidator.controller('XDRValidatorCtrl', ['$scope', 'growl', 'XDRValidatorEndpoints', 'XDRValidatorRun', 'XDRValidatorStatus',
    function ($scope, growl, XDRValidatorEndpoints, XDRValidatorRun, XDRValidatorStatus) {

        $scope.xdrSamples = [{
            "name": "C32_Sample1_full_metadata",
            "value": "C32_FULL_1"
        }, {
            "name": "C32_Sample1_minimal_metadata",
            "value": "C32_MINIMAL_1"
        }, {
            "name": "C32_Sample2_full_metadata",
            "value": "C32_FULL_2"
        }, {
            "name": "C32_Sample2_minimal_metadata",
            "value": "C32_MINIMAL_2"
        }, {
            "name": "CCDA_Ambulatory_full_metadata",
            "value": "CCDA_AMBULATORY_FULL"
        }, {
            "name": "CCDA_Ambulatory_minimal_metadata",
            "value": "CCDA_AMBULATORY_MINIMAL"
        }, {
            "name": "CCDA_Inpatient_full_metadata",
            "value": "CCDA_INPATIENT_FULL"
        }, {
            "name": "CCDA_Inpatient_minimal_metadata",
            "value": "CCDA_INPATIENT_MINIMAL"
        }, {
            "name": "CCR_Sample1_full_metadata",
            "value": "CCR_FULL_1"
        }, {
            "name": "CCR_Sample1_minimal_metadata",
            "value": "CCR_MINIMAL_1"
        }, {
            "name": "CCR_Sample2_full_metadata",
            "value": "CCR_FULL_2"
        }, {
            "name": "CCR_Sample2_minimal_metadata",
            "value": "CCR_MINIMAL_2"
        }];

        $scope.sample = {};
        $scope.send = {};
        $scope.receive = {};
        $scope.receive.status = "na";

        $scope.properties = {};

        $scope.configure = function () {
            XDRValidatorEndpoints.get(function (data) {
                if (data.endpoint) {
                    $scope.receive.endpoint = data.endpoint;
                    $scope.receive.endpointTLS = data.endpointTLS;
                }
                $scope.receive.status = "success";
            }, function (data) {
                throw {
                    code: data.data.code,
                    url: data.data.url,
                    message: data.data.message
                };
            });
        };

        $scope.sendXdr = function () {
            $scope.laddaLoading = true;
            if ($scope.sample.selected) {
                $scope.properties.selected = $scope.sample.selected.value;
            }
            XDRValidatorRun.save($scope.properties, function (data) {
                $scope.laddaLoading = false;
                $scope.send.status = "loading";
                $scope.send.results = data;
                if (data.content !== null && data.content !== undefined) {
                    if (data.content.criteriaMet.toLowerCase() === 'pending') {
                        $scope.send.status = "pending";
                        if (data.content) {
                            $scope.send.endpoint = data.content.value.endpoint;
                            $scope.send.endpointTLS = data.content.value.endpointTLS;
                        }
                    } else if (data.content.criteriaMet.toLowerCase() === 'manual') {
                        $scope.send.status = "success";
                    } else if (data.content.criteriaMet.toLowerCase() === 'passed') {
                        $scope.send.status = "success";
                    } else {
                        $scope.send.status = "error";
                    }
                } else {
                    $scope.laddaLoading = false;
                    if (data.status.toLowerCase() === 'passed') {
                        $scope.send.status = "success";
                    } else {
                        $scope.send.status = "error";
                        throw {
                            code: '0x0020',
                            url: 'xdr',
                            message: data.message
                        };
                    }
                }
            }, function (data) {
                $scope.send.status = 'error';
                $scope.laddaLoading = false;
                if (data.data) {
                    throw {
                        code: data.data.code,
                        url: data.data.url,
                        message: data.data.message
                    };
                }
            });
        };

        $scope.checkXdrStatus = function () {
            $scope.receive.status = "loading";
            $scope.statusMessage = "";
            $scope.receive.results = XDRValidatorStatus.get(function (data) {
                $scope.receive.results = data;
                if (data.request) {
                    $scope.receive.status = "success";
                } else {
                    $scope.statusMessage = "No XDR received yet";
                }
            }, function (data) {
                $scope.receive.status = "error";
                if (data.data) {
                    throw {
                        code: data.data.code,
                        url: data.data.url,
                        message: data.data.message
                    };
                }
            });
        };

        $scope.displayGrowl = function (text) {
            growl.success(text);
        };

        $scope.resetReceive = function () {
            $scope.receive = {};
            $scope.statusMessage = "";
            $scope.receive.status = "na";
            $scope.receive.results = "";
        };

        $scope.resetSend = function () {
            $scope.send = {};
            $scope.statusMessage = "";
            $scope.send.status = "na";
            $scope.send.results = "";
        };
    }
]);