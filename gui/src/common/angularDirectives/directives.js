/* Directives */


var tttDirective = angular.module('ttt.directives', []);

tttDirective.directive('directReport', function() {
    return {
        restrict: "E",
        replace: true,
        scope: {
            data: '=',
            selectNode: '&'
        },
        template: '<ul><member ng-repeat="member in data" member="member" select-node="selectNode()"></member></ul>'
    };
});

tttDirective.directive('member', function($compile) {
    return {
        restrict: "E",
        replace: true,
        scope: {
            member: '=',
            selectNode: '&'
        },
        template: '<li><a href="" class="report-{{member.status | statusFilter}}" ng-click="selectNode()(member)"><span><i class="fa {{member.status | statusIconFilter}}"></i> Part: {{member.contentType | contentTypeFilter}}</span></a></li>',
        link: function(scope, element, attrs) {
            if (angular.isArray(scope.member.children)) {
                element.append('<direct-report data="member.children" select-node="selectNode()"></direct-report>');
                $compile(element.contents())(scope);
            }
        }
    };
});

tttDirective.directive('checkOrCross', function() {
    return {
        restrict: 'A',
        scope: {
            val: '@'
        },
        template: '<i class="fa fa-{{fa}}" style="color: {{color}};"></i>',
        link: function(scope, element, attrs) {
            scope.$watch('val', function(value) {
                if (scope.val === "true") {
                    scope.color = "green";
                    scope.fa = "check";
                } else {
                    scope.color = "red";
                    scope.fa = "times";
                }
            });
        }
    };
});

tttDirective.directive('numberOnly', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            ngModel: '='
        },
        link: function(scope) {
            scope.$watch('ngModel', function(newValue, oldValue) {
                var arr = String(newValue).split("");
                if (arr.length === 0) {
                    return;
                }
                if (arr.length === 1 && (arr[0] == '-' || arr[0] === '.')) {
                    return;
                }
                if (arr.length === 2 && newValue === '-.') {
                    return;
                }
                if (isNaN(newValue)) {
                    scope.ngModel = oldValue;
                }
            });
        }
    };
});

tttDirective.directive('smtpTests', function() {
    return {
        restrict: "E",
        scope: false,
        template: '<ul class="event-list"><smtp-single-test ng-repeat="test in testBench"></smtp-single-test></ul>'
    };
});

tttDirective.directive('smtpSingleTest', function() {
    return {
        restrict: "E",
        replace: true,
        scope: false,
        link: function(scope, element, attrs) {
            scope.fieldInput = {
                "testCaseNumber": scope.test.id,
                "sutSmtpAddress": "",
                "sutSmtpPort": 0,
                "tttSmtpPort": 25,
                "sutEmailAddress": "",
                "tttEmailAddress": "",
                "useTLS": false,
                "sutCommandTimeoutInSeconds": 0,
                "sutUserName": "",
                "sutPassword": "",
                "tttUserName": "",
                "tttPassword": "",
                "tttSmtpAddress": "",
                "startTlsPort": 0
            };
        },
        templateUrl: 'templates/smtpTemplates/SingleTestCaseTemplate.tpl.html'

    };
});

tttDirective.directive('xdrSenderSingleTest', function() {
    return {
        restrict: "E",
        replace: true,
        scope: false,
        templateUrl: 'templates/xdrTemplates/xdrSenderTestCaseTemplate.tpl.html'

    };
});

tttDirective.directive('xdrReceiverSingleTest', function() {
    return {
        restrict: "E",
        replace: true,
        scope: false,
        templateUrl: 'templates/xdrTemplates/xdrSenderTestCaseTemplate.tpl.html'

    };
});

tttDirective.directive('backToTop', function() {
    return {
        restrict: "E",
        template: '<a id="back-to-top" href="#" class="btn btn-primary btn-lg back-to-top" role="button" tooltips title="Click to return on the top page" tooltip-side="left"><span class="glyphicon glyphicon-chevron-up"></span></a>',
        link: function(scope, element, attrs) {
            $(window).scroll(function() {
                if ($(this).scrollTop() > 50) {
                    $('#back-to-top').fadeIn();
                } else {
                    $('#back-to-top').fadeOut();
                }
            });
            // scroll body to 0px on click
            $('#back-to-top').click(function() {
                $('body,html').animate({
                    scrollTop: 0
                }, 300);
                return false;
            });
        }
    };
});

tttDirective.directive('editInPlace', function() {
    return {
        restrict: 'E',
        scope: {
            value: '='
        },
        template: '<span ng-click="edit()" ng-bind="value"></span><input ng-model="value"></input>',
        link: function($scope, element, attrs) {
            // Let's get a reference to the input element, as we'll want to reference it.
            var inputElement = angular.element(element.children()[1]);

            // This directive should have a set class so we can style it.
            element.addClass('edit-in-place');

            // Initially, we're not editing.
            $scope.editing = false;

            // ng-click handler to activate edit-in-place
            $scope.edit = function() {
                $scope.editing = true;

                // We control display through a class on the directive itself. See the CSS.
                element.addClass('active');

                // And we must focus the element.
                // `angular.element()` provides a chainable array, like jQuery so to access a native DOM function,
                // we have to reference the first element in the array.
                inputElement[0].focus();
            };

            // When we leave the input, we're done editing.
            inputElement.prop('onblur', function() {
                if ($scope.value === "") {
                    $scope.value = "Profile_0";
                }
                $scope.editing = false;
                element.removeClass('active');
            });
        }
    };
});

tttDirective.directive("fadeAnimation", function() {
    return {
        restrict: "C",
        link: function(scope, element) {

            var w = $(element);
            scope.getWindowDimensions = function() {
                return {
                    'h': w.height(),
                    'w': w.width()
                };
            };

            scope.$watch(scope.getWindowDimensions, function(newValue, oldValue) {
                var height = $(element).height();
                var parentHeight = $(element).parent().height();
                // If this element's height is greater than that of the parent,
                // update the parents's height. Always pick the largest one as this
                // will mean no flickering.
                $(element).parent().height(height + 20);

            }, true);

            element.bind('animateFade', function() {
                scope.$apply();
            });
        }
    };
});

tttDirective.directive('prism', [function() {
    return {
        restrict: 'A',
        link: function($scope, element, attrs) {
            element.ready(function() {
                Prism.highlightElement(element[0]);
            });
        }
    };
}]);

tttDirective.directive('releaseNotes', function() {
    return {
        restrict: 'E',
        template: '<div ng-repeat="version in versions" ng-show="$first"><div ng-repeat="(key, value) in version"><h3 style="margin-bottom:0px;">{{key}} - <small>{{value.date | date}}</small></h3><ul ng-repeat="note in value.notes"><li>{{note.title}}</li><ul ng-repeat="line in note.lines"><li>{{line}}</li></ul></ul></div></div>',
        controller: 'ReleaseNotesCtrl'
    };
});

tttDirective.directive('attachmentDownload', function($compile) {
    return {
        restrict: 'E',
        scope: {
            getUrlData: '&getData',
            filename: '@'
        },
        replace: true,
        link: function(scope, elm, attrs) {
            var url = URL.createObjectURL(scope.getUrlData());
            elm.append($compile(
                '<a  class="btn btn-inverse btn-lg pull-right" download="' + attrs.filename + '"' +
                'href="' + url + '">' +
                '<i class="fa fa-cloud-download"></i> Download</a>'
            )(scope));
        }
    };
});

tttDirective.directive('ccdaR1Report', function() {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        replace: true,
        templateUrl: 'templates/ccdaTemplates/ccdaR1ReportTemplate.tpl.html',
        controller: ('CcdaR2WidgetCtrl', ['$scope','$location', '$anchorScroll', function($scope,$location,$anchorScroll) {
                $scope.tabs = [{active:true},{active:false},{active:false}];
                $scope.gotoLink = function(item) {
                   if (item == "C-CDA MDHT Conformance Error" ||
                       item == "C-CDA MDHT Conformance Warning" ||
                       item == "C-CDA MDHT Conformance Info"){
                            $scope.tabs[0].active =true;
                   }else if(item == "ONC 2015 S&CC Vocabulary Validation Conformance Error" ||
                       item == "ONC 2015 S&CC Vocabulary Validation Conformance Warning" ||
                       item == "ONC 2015 S&CC Vocabulary Validation Conformance Info"){
                           $scope.tabs[1].active =true;
                   }else{
                           $scope.tabs[2].active =true;
                   }
                  // set the location.hash to the id of
                  // the element you wish to scroll to.
                  $location.hash(item);
                 // call anchorScroll()
                 $anchorScroll();
                };
             }])
    };
});

tttDirective.directive('dcdtReport', function() {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        replace: true,
        templateUrl: 'templates/dcdtTemplates/dcdtReportTemplate.tpl.html'
    };
});

tttDirective.directive('hostingReport', function() {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        replace: true,
        templateUrl: 'templates/dcdtTemplates/hostingReportTemplate.tpl.html'
    };
});

tttDirective.directive('ccdaR2Report', function() {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        replace: true,
        templateUrl: 'templates/ccdaTemplates/ccdaR2ReportTemplate.tpl.html',
                controller: ('CcdaR2WidgetCtrl', ['$scope','$location', '$anchorScroll', function($scope,$location,$anchorScroll) {
                $scope.tabs = [{active:true},{active:false},{active:false}];
                $scope.gotoLink = function(item) {
                   if (item == "C-CDA MDHT Conformance Error" ||
                       item == "C-CDA MDHT Conformance Warning" ||
                       item == "C-CDA MDHT Conformance Info"){
                            $scope.tabs[0].active =true;
                   }else if(item == "ONC 2015 S&CC Vocabulary Validation Conformance Error" ||
                       item == "ONC 2015 S&CC Vocabulary Validation Conformance Warning" ||
                       item == "ONC 2015 S&CC Vocabulary Validation Conformance Info"){
                           $scope.tabs[1].active =true;
                   }else{
                           $scope.tabs[2].active =true;
                   }
                  // set the location.hash to the id of
                  // the element you wish to scroll to.
                  $location.hash(item);
                 // call anchorScroll()
                 $anchorScroll();
                };
             }])
   };
});

tttDirective.directive('ccdaWidget', ['$uibModal', function($uibModal) {
    return {
        restrict: 'E',
        scope: {
            ccdaDocument: '=ngModel'
        },
        replace: true,
        template: '<div><button class="btn btn-default" ng-click="openCcdaModal()" style="margin-bottom: 10px;"">Select document...</button><strong>{{ccdaDocument.name}}</strong></div>',
        controller: ('CcdaWidgetCtrl', ['$scope', '$uibModal', function($scope, $uibModal) {
            $scope.openCcdaModal = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'templates/ccdaTemplates/ccdaWidgetTemplate.tpl.html',
                    controller: ('CCDADocumentPicker', ['$scope', '$uibModalInstance', 'CCDADocumentsFactory',
                        function($scope, $uibModalInstance, CCDADocumentsFactory) {
                            $scope.modalTitle = "Select C-CDA Document Type";
                            $scope.buttonSuccess = "Ok";
                            $scope.sutRole = "sender";
                            $scope.ccdaData = {};

                            CCDADocumentsFactory.get(function(data) {
                                $scope.ccdaDocuments = data;
                                if (data !== null) {
                                    $scope.sutRole = Object.keys(data)[0];
                                    $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                                }
                            }, function(error) {
                                console.log(error);
                            });

                            $scope.displayType = function(type, allowedType) {
                                if (allowedType !== undefined) {
                                    if (type.toLowerCase().indexOf(allowedType) > -1) {
                                        return false;
                                    } else {
                                        return true;
                                    }
                                }
                                return false;
                            };

                            $scope.switchDocType = function(type) {
                                $scope.sutRole = type;
                                $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                            };

                            $scope.save = function() {
                                $uibModalInstance.close($scope.ccdaDocument);
                            };


                            $scope.close = function() {
                                $uibModalInstance.dismiss("Ok");
                            };
                        }
                    ])
                });

                modalInstance.result.then(function(selectedItem) {
                    $scope.ccdaDocument = selectedItem;
                }, function() {
                    // console.log('Modal dismissed at: ' + new Date());
                });
            };
        }])
    };
}]);


tttDirective.directive('ccdaWidgetreceiver', ['$uibModal', function($uibModal) {
    return {
        restrict: 'E',
        scope: {
            ccdaDocument: '=ngModel'
        },
        replace: true,
        template: '<div><button class="btn btn-default" ng-click="openCcdaModal()" style="margin-bottom: 10px;"">Select document...</button><strong>{{ccdaDocument.name}}</strong></div>',
        controller: ('CcdaWidgetCtrl', ['$scope', '$uibModal', function($scope, $uibModal) {
            $scope.openCcdaModal = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'templates/ccdaTemplates/ccdaWidgetTemplate.tpl.html',
                    controller: ('CCDADocumentPicker', ['$scope', '$uibModalInstance', 'CCDADocumentsFactory',
                        function($scope, $uibModalInstance, CCDADocumentsFactory) {
                            $scope.modalTitle = "Select C-CDA Document Type";
                            $scope.buttonSuccess = "Ok";
                            $scope.sutRole = "receiver";
                            $scope.ccdaData = {};

                            CCDADocumentsFactory.get(function(data) {
                                $scope.ccdaDocuments = data;
                                if (data !== null) {
                                   $scope.sutRole = Object.keys(data)[1];
                                    $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                                }
                            }, function(error) {
                                console.log(error);
                            });

                            $scope.displayType = function(type, allowedType) {
                                if (allowedType !== undefined) {
                                    if (type.toLowerCase().indexOf(allowedType) > -1) {
                                        return false;
                                    } else {
                                        return true;
                                    }
                                }
                                return false;
                            };

                            $scope.switchDocType = function(type) {
                                $scope.sutRole = type;
                                $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                            };

                            $scope.save = function() {
                                $uibModalInstance.close($scope.ccdaDocument);
                            };


                            $scope.close = function() {
                                $uibModalInstance.dismiss("Ok");
                            };
                        }
                    ])
                });

                modalInstance.result.then(function(selectedItem) {
                    $scope.ccdaDocument = selectedItem;
                }, function() {
                    // console.log('Modal dismissed at: ' + new Date());
                });
            };
        }])
    };
}]);

tttDirective.directive('ccdaWidgetxdr', ['$uibModal', function($uibModal) {
    return {
        restrict: 'E',
        scope: {
            ccdaDocument: '=ngModel'
        },
        replace: true,
        template: '<div><button class="btn btn-default" ng-click="openCcdaModal()" style="margin-bottom: 10px;"">Select document...</button><strong>{{ccdaDocument.name}}</strong></div>',
        controller: ('CcdaWidgetCtrl', ['$scope', '$uibModal', function($scope, $uibModal) {
            $scope.openCcdaModal = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'templates/ccdaTemplates/ccdaWidgetTemplate.tpl.html',
                    controller: ('CCDADocumentPicker', ['$scope', '$uibModalInstance', 'CCDAXdrDocumentsFactory',
                        function($scope, $uibModalInstance, CCDAXdrDocumentsFactory) {
                            $scope.modalTitle = "Select C-CDA Document Type";
                            $scope.buttonSuccess = "Ok";
                            $scope.sutRole = "sender";
                            $scope.ccdaData = {};

                            CCDAXdrDocumentsFactory.get(function(data) {
                                $scope.ccdaDocuments = data;
                                if (data !== null) {
                                    $scope.sutRole = Object.keys(data)[0];
                                    $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                                }
                            }, function(error) {
                                console.log(error);
                            });

                            $scope.displayType = function(type, allowedType) {
                                if (allowedType !== undefined) {
                                    if (type.toLowerCase().indexOf(allowedType) > -1) {
                                        return false;
                                    } else {
                                        return true;
                                    }
                                }
                                return false;
                            };

                            $scope.switchDocType = function(type) {
                                $scope.sutRole = type;
                                $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                            };

                            $scope.save = function() {
                                $uibModalInstance.close($scope.ccdaDocument);
                            };


                            $scope.close = function() {
                                $uibModalInstance.dismiss("Ok");
                            };
                        }
                    ])
                });

                modalInstance.result.then(function(selectedItem) {
                    $scope.ccdaDocument = selectedItem;
                }, function() {
                    // console.log('Modal dismissed at: ' + new Date());
                });
            };
        }])
    };
}]);


tttDirective.directive('ccdaWidgetreceiverxdr', ['$uibModal', function($uibModal) {
    return {
        restrict: 'E',
        scope: {
            ccdaDocument: '=ngModel'
        },
        replace: true,
        template: '<div><button class="btn btn-default" ng-click="openCcdaModal()" style="margin-bottom: 10px;"">Select document...</button><strong>{{ccdaDocument.name}}</strong></div>',
        controller: ('CcdaWidgetCtrl', ['$scope', '$uibModal', function($scope, $uibModal) {
            $scope.openCcdaModal = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: 'templates/ccdaTemplates/ccdaWidgetTemplate.tpl.html',
                    controller: ('CCDADocumentPicker', ['$scope', '$uibModalInstance', 'CCDAXdrDocumentsFactory',
                        function($scope, $uibModalInstance, CCDAXdrDocumentsFactory) {
                            $scope.modalTitle = "Select C-CDA Document Type";
                            $scope.buttonSuccess = "Ok";
                            $scope.sutRole = "receiver";
                            $scope.ccdaData = {};
                            CCDAXdrDocumentsFactory.get(function(data) {
                                $scope.ccdaDocuments = data;
                                if (data !== null) {
                                   $scope.sutRole = Object.keys(data)[1];
                                    $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                                }
                            }, function(error) {
                                console.log(error);
                            });

                            $scope.displayType = function(type, allowedType) {
                                if (allowedType !== undefined) {
                                    if (type.toLowerCase().indexOf(allowedType) > -1) {
                                        return false;
                                    } else {
                                        return true;
                                    }
                                }
                                return false;
                            };

                            $scope.switchDocType = function(type) {
                                $scope.sutRole = type;
                                $scope.ccdaData = $scope.ccdaDocuments[$scope.sutRole];
                            };

                            $scope.save = function() {
                                $uibModalInstance.close($scope.ccdaDocument);
                            };


                            $scope.close = function() {
                                $uibModalInstance.dismiss("Ok");
                            };
                        }
                    ])
                });

                modalInstance.result.then(function(selectedItem) {
                    $scope.ccdaDocument = selectedItem;
                }, function() {
                    // console.log('Modal dismissed at: ' + new Date());
                });
            };
        }])
    };
}]);
