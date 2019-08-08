var edgeHome = angular.module('ttt.hisp.home', []);

edgeHome.controller('HispHomeCtrl', ['$scope', 'PropertiesFactory', 'growl',
    function($scope, PropertiesFactory, growl) {

        $scope.mdnAddresses = [{
            name: "processedonly5",
            purpose: "Provides regular processed MDN's when messages received. (No Dispatched MDN)"
        }, {
            name: "processeddispatched6",
            purpose: "Provides both Processed and Dispatched MDN's when messages received."
        }, {
            name: "processdelayeddispatch7",
            purpose: "Provides Processed when message received, dispatched after 1 hour 5 minutes after message received."
        }, {
            name: "nomdn8",
            purpose: "No MDNs are provided."
        }, {
            name: "noaddressfailure9",
            purpose: "Non-existent address. (Need to send Failure MDN)"
        }];

        $scope.properties = PropertiesFactory.get(function(data) {
            angular.forEach($scope.mdnAddresses, function(mdn) {
                mdn.name += '@' + data.domainName;
            });

        });

        $scope.displayGrowl = function(text) {
            growl.success(text);
        };

    }

]);
