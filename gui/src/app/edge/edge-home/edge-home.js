var edgeHome = angular.module('ttt.edge.home', []);

edgeHome.controller('EdgeHomeCtrl', ['$scope', 'PropertiesFactory', 'growl',
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
        }, {
            name: "processedonly5-plain",
            purpose: "Provides SMTP processed MDN's when messages received. (No Dispatched MDN)"
        }, {
            name: "processeddispatched6-plain",
            purpose: "Provides both Processed and Dispatched SMTP MDN's when messages received."
        }, {
            name: "noaddressfailure9-plain",
            purpose: "Non-existent address. (Need to send Failure MDN)"
        }];

        $scope.badMdnAddresses = [{
            name: "white_space_mdn",
            purpose: "MDN Report field containing white spaces."
        }, {
            name: "extra_line_break_mdn",
            purpose: "MDN Report field with line breaks."
        }, {
            name: "extra_space_disposition",
            purpose: "Extra Space in Disposition Report Field."
        }, {
            name: "missing_disposition",
            purpose: "Missing Disposition Notification To Header."
        }, {
            name: "null_sender",
            purpose: "MDN with Null Envelope Sender."
        }, {
            name: "different_sender",
            purpose: "MDN’s containing different Sender and From addresses."
        }, {
            name: "different_msgid",
            purpose: "Message ID differences between the outer envelope and inner message for wrapped messages."
        }, {
            name: "white_space_822",
            purpose: "RFC822 headers containing white spaces."
        }, {
            name: "different_cases_822",
            purpose: "RFC822 headers constructed using different cases."
        }, {
            name: "dsn",
            purpose: "Non-existent address. (Need to send Failure MDN)"
        }];

        $scope.properties = PropertiesFactory.get(function(data) {
            angular.forEach($scope.mdnAddresses, function(mdn) {
                mdn.name += '@' + data.domainName;
            });
            angular.forEach($scope.badMdnAddresses, function(mdn) {
                mdn.name += '@' + data.domainName;
            });

        });

        $scope.displayGrowl = function(text) {
            growl.success(text);
        };

    }

]);
