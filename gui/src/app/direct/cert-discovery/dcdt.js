var dcdtValidator = angular.module('ttt.direct.dcdtValidator', []);

dcdtValidator.controller('DCDTValidatorCtrl', ['$scope', 'DCDTValidatorFactory', '$state', 'ApiUrl','$http','CCDADocumentsFactory','$timeout', 'growl','$sce','$location', '$anchorScroll',
	function($scope, DCDTValidatorFactory, $state, ApiUrl,$http,CCDADocumentsFactory,$timeout ,growl,$sce,$location,$anchorScroll) {
	$scope.pageTitle= $state.current.data.pageTitle;
	$scope.year2015 = ($scope.pageTitle === "2015");
	$scope.year2014 = ($scope.pageTitle === "2014");
	$scope.emailDomain2014 = "dcdt30prod.sitenv.org";
	$scope.emailDomain2015 = "dcdt31prod.sitenv.org";
	$scope.disclaimerLink ="https://www.hhs.gov/disclaimer.html";

	$http.get('api/properties').then(function (response) {
		$scope.propDcdtDomain2014 = response.data.dcdt2014domain;
		$scope.propDcdtDomain2015 = response.data.dcdt2015domain;
		$scope.propDcdtProtocol2014 = response.data.dcdt2014Protocol;
		$scope.propDcdtProtocol2015 = response.data.dcdt2015Protocol;
	});

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
		$scope.alerts = [];
		$scope.discalerts = [];
        $scope.datatool = [
          {name:"Hosting allows a System Under Test (SUT) to verify that their certificates are hosted correctly, and discoverable by other Direct implementations.", hreflink:"panel_hosting",children:0},
          {name:"Discovery allows a SUT to verify that they can discover certificates in other Direct implementations by using them to send Direct messages.",  hreflink:"panel_discovery",children:1}
        ];

       $scope.directAddress ="";
       $scope.testcase="";
       $scope.discEmailAddr ="";
       $scope.discResultEmailAddr="";
       $scope.discoveryTestCase = [
    { code: "", name: "--No testcase selected--" },
    { code: "D1_DNS_AB_Valid", testcaseid:"1" , name: "D1 - Valid address-bound certificate discovery in DNS",
      Negative: "false",
      Optional: "false",
      Direct_address_2014: "d1@domain1.dcdt30prod.sitenv.org",
      Direct_address_2015: "d1@domain1.dcdt31prod.sitenv.org",
      Description: "This test case verifies that your system can query DNS for address-bound CERT records and discover a valid address-bound X.509 certificate for a Direct address.",
      RTM_Sections: "1, 3",
      RFC_4398:  "Section 2.1",
      Direct_SHT: "Direct Applicability Statement for Secure Health Transport: Sections 4.0 and 5.3",
      Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
      Target_Certificate: [{"name": "D1_valA",
                            "Valid": "true",
                            "Description": "Valid address-bound certificate for the Direct address in a DNS CERT record.",
                            "Binding_Type": "ADDRESS",
                            "Locaton": [{"Type": "DNS",
                                 "Mail_Address": "d1@domain1.dcdt30prod.sitenv.org"
                             }]
                         }],
      Background_Certificate: [{"name": "D1_invB",
          "Valid": "false",
          "Description": "Invalid domain-bound certificate for the Direct address in a DNS CERT record.",
          "Binding_Type": "DOMAIN",
          "Locaton": [{"Type": "LDAP",
               "Mail_Address": "domain1.dcdt30prod.sitenv.org"
           }]
       },
       {"name": "D1_invC",
           "Valid": "false",
           "Description": "Invalid address-bound certificate for the Direct address in an LDAP server with an associated SRV record.",
           "Binding_Type": "ADDRESS",
           "Locaton": [{"Type": "LDAP",
                "Mail_Address": "d1@domain1.dcdt30prod.sitenv.org",
                "Host":"0.0.0.0",
                "Port":"10389"
            }]
        },
        {"name": "D1_invD",
            "Valid": "false",
            "Description": "Invalid domain-bound certificate for the Direct address in an LDAP server with an associated SRV record.",
            "Binding_Type": "DOMAIN",
            "Locaton": [{"Type": "LDAP",
                 "Mail_Address": "domain1.dcdt30prod.sitenv.org",
                     "Host":"0.0.0.0",
                     "Port":"10389"            }]
         }
         ]
     },
     { code: "D2_DNS_DB_Valid", testcaseid:"2",  name: "D2 - Valid domain-bound certificate discovery in DNS",
         Negative: "false",
         Optional: "false",
         Direct_address_2014: "d2@domain1.dcdt30prod.sitenv.org",
         Direct_address_2015: "d2@domain1.dcdt31prod.sitenv.org",
         Description: "This test case verifies that your system can query DNS for domain-bound CERT records and discover a valid domain-bound X.509 certificate for a Direct address.",
         RTM_Sections: "1, 3",
         RFC_4398:  "Section 2.1",
         Direct_SHT: "Direct Applicability Statement for Secure Health Transport: Sections 4.0 and 5.3",
         Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
         Target_Certificate: [{"name": "D2_valB",
                               "Valid": "true",
                               "Description": "Valid domain-bound certificate in a DNS CERT record containing the Direct address in the rfc822Name of the SubjectAlternativeName extension.",
                               "Binding_Type": "DOMAIN",
                               "Locaton": [{"Type": "DNS",
                                    "Mail_Address": "domain1.dcdt30prod.sitenv.org"
                                }]
                            }],
         Background_Certificate: [
          {"name": "D2_invC",
              "Valid": "false",
              "Description": "Invalid address-bound certificate for the Direct address in an LDAP server with an associated SRV record.",
              "Binding_Type": "ADDRESS",
              "Locaton": [{"Type": "LDAP",
                   "Mail_Address": "d2@domain1.dcdt30prod.sitenv.org",
                   "Host":"0.0.0.0",
                   "Port":"10389"
               }]
           },
           {"name": "D1_invD",
               "Valid": "false",
               "Description": "Invalid domain-bound certificate for the Direct address in an LDAP server with an associated SRV record.",
               "Binding_Type": "DOMAIN",
               "Locaton": [{"Type": "LDAP",
                    "Mail_Address": "domain1.dcdt30prod.sitenv.org",
                        "Host":"0.0.0.0",
                        "Port":"10389"            }]
            }
            ]
        },
        { code: "D3_LDAP_AB_Valid",  testcaseid:"3", name: "D3 - Valid address-bound certificate discovery in LDAP",
            Negative: "false",
            Optional: "false",
            Direct_address_2014: "d3@domain2.dcdt30prod.sitenv.org",
            Direct_address_2015: "d3@domain2.dcdt31prod.sitenv.org",
            Description: "This test case verifies that your system can query DNS for SRV records and discover a valid address-bound X.509 certificate for a Direct address in the associated LDAP server.",
            RTM_Sections: "2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22",
            RFC_2798:  "Section 9.1.2",
            Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
            Target_Certificate: [{"name": "D3_valC",
                                  "Valid": "true",
                                  "Description": "Valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson schema. The associated SRV record has Priority = 0.",
                                  "Binding_Type": "ADDRESS",
                                  "Locaton": [{"Type": "LDAP",
                                       "Mail_Address": "d3@domain2.dcdt30prod.sitenv.org",
                                       "Host":"0.0.0.0",
                                       "Port":"10389"
                                  }]
                               }],
            Background_Certificate: [{"name": "D3_invD",
                "Valid": "false",
                "Description": "Invalid domain-bound certificate for the Direct address in an LDAP server with an associated SRV record",
                "Binding_Type": "DOMAIN",
                "Locaton": [{"Type": "LDAP",
                     "Mail_Address": "domain2.dcdt30prod.sitenv.org",
                     "Host":"0.0.0.0",
                     "Port":"10389"
                }]
             }
               ]
           },
           { code: "D4_LDAP_DB_Valid", testcaseid:"4", name: "D4 - Valid domain-bound certificate discovery in LDAP",
               Negative: "false",
               Optional: "false",
               Direct_address_2014: "d4@domain2.dcdt30prod.sitenv.org",
               Direct_address_2015: "d4@domain2.dcdt31prod.sitenv.org",
               Description: "This test case verifies that your system can query DNS for SRV records and discover a valid domain-bound X.509 certificate for a Direct address in the associated LDAP server.",
               RTM_Sections: "2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22",
               RFC_2798:  "Section 9.1.2",
               Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
               Target_Certificate: [{"name": "D4_valD",
                                     "Valid": "true",
                                     "Description": "Valid domain-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson schema. The associated SRV record has Priority = 0.",
                                     "Binding_Type": "DOMAIN",
                                     "Locaton": [{"Type": "LDAP",
                                          "Mail_Address": "domain2.dcdt30prod.sitenv.org",
                                          "Host":"0.0.0.0",
                                          "Port":"10389"
                                      }]
                                  }],
               Background_Certificate: [
                  ]
              },
              { code: "D5_DNS_AB_Invalid",  testcaseid:"5", name: "D5 - Invalid address-bound certificate discovery in DNS",
                  Negative: "false",
                  Optional: "false",
                  Direct_address_2014: "d5@domain1.dcdt30prod.sitenv.org",
                  Direct_address_2015: "d5@domain1.dcdt31prod.sitenv.org",
                  Description: "This test case verifies that your system can query DNS for address-bound CERT records and finds, but does not select the associated invalid address-bound X.509 certificate.",
                  RTM_Sections: "1, 3",
                  Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                  Target_Certificate: [{"name": "D5_invA",
                                        "Valid": "false",
                                        "Description": "An invalid address-bound certificate for the Direct address in a DNS CERT record.",
                                        "Binding_Type": "ADDRESS",
                                        "Locaton": [{"Type": "DNS",
                                             "Mail_Address": "d5@domain1.dcdt30prod.sitenv.org"
                                         }]
                                     }],
                  Background_Certificate: [
                     ]
                 },
                 { code: "D6_DNS_DB_Invalid",  testcaseid:"6", name: "D6 - Invalid domain-bound certificate discovery in DNS",
                     Negative: "true",
                     Optional: "false",
                     Direct_address_2014: "d6@domain4.dcdt30prod.sitenv.org",
                     Direct_address_2015: "d6@domain4.dcdt31prod.sitenv.org",
                     Description: "This test case verifies that your system can query DNS for domain-bound CERT records and finds, but does not select the associated invalid domain-bound X.509 certificate.",
                     RTM_Sections: "1, 3",
                     Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                     Target_Certificate: [{"name": "D6_invB",
                                           "Valid": "false",
                                           "Description": "An invalid domain-bound certificate for the Direct address in a DNS CERT record.",
                                           "Binding_Type": "DOMAIN",
                                           "Locaton": [{"Type": "DNS",
                                                "Mail_Address": "domain4.dcdt30prod.sitenv.org"
                                            }]
                                        }],
                     Background_Certificate: [
                        ]
                    },
                { code: "D7_LDAP_AB_Invalid",  testcaseid:"7", name: "D7 - Invalid address-bound certificate discovery in LDAP",
                    Negative: "true",
                    Optional: "false",
                    Direct_address_2014: "d7@domain2.dcdt30prod.sitenv.org",
                    Direct_address_2015: "d7@domain2.dcdt31prod.sitenv.org",
                    Description: "This test case verifies that your system can query DNS for SRV records and finds, but does not select the invalid address-bound X.509 certificate in the associated LDAP server.",
                    RTM_Sections: "3, 22",
                    Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                    Target_Certificate: [{"name": "D7_invC",
                                          "Valid": "true",
                                          "Description": "Invalid address-bound certificate for the Direct address in an LDAP server with an associated SRV record.",
                                          "Binding_Type": "ADDRESS",
                                          "Locaton": [{"Type": "LDAP",
                                               "Mail_Address": "d7@domain2.dcdt30prod.sitenv.org",
                                               "Host":"0.0.0.0",
                                               "Port":"10389"
                                           }]
                                       }],
                    Background_Certificate: [
                       ]
                   },
                   { code: "D8_LDAP_DB_Invalid", testcaseid:"8", name: "D8 - Invalid domain-bound certificate discovery in LDAP",
                       Negative: "true",
                       Optional: "false",
                       Direct_address_2014: "d8@domain5.dcdt30prod.sitenv.org",
                       Direct_address_2015: "d8@domain5.dcdt31prod.sitenv.org",
                       Description: "This test case verifies that your system can query DNS for SRV records and finds, but does not select the invalid domain-bound X.509 certificate in the associated LDAP server.",
                       RTM_Sections: " 3, 22",
                       Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                       Target_Certificate: [{"name": "D8_invD",
                                             "Valid": "false",
                                             "Description": " Invalid domain-bound certificate for the Direct address in an LDAP server with an associated SRV record.",
                                             "Binding_Type": "DOMAIN",
                                             "Locaton": [{"Type": "LDAP",
                                                  "Mail_Address": "domain5.dcdt30prod.sitenv.org",
                                                  "Host":"0.0.0.0",
                                                  "Port":"12389"
                                              }]
                                          }],
                       Background_Certificate: [
                          ]
                      },
                      { code: "D9_DNS_AB_SelectValid",  testcaseid:"9", name: "D9 - Select valid address-bound certificate over invalid certificate in DNS",
                          Negative: "false",
                          Optional: "false",
                          Direct_address_2014: "d9@domain1.dcdt30prod.sitenv.org",
                          Direct_address_2015: "d9@domain1.dcdt31prod.sitenv.org",
                          Description: "This test case verifies that your system can query DNS for address-bound CERT records and select the valid address-bound X.509 certificate instead of the invalid address-bound X.509 certificate.",
                          RTM_Sections: "1, 3",
                          Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
                          Target_Certificate: [{"name": "D9_valA",
                                                "Valid": "true",
                                                "Description": "Valid address-bound certificate in a DNS CERT record containing the Direct address in the rfc822Name of the SubjectAlternativeName extension.",
                                                "Binding_Type": "ADDRESS",
                                                "Locaton": [{"Type": "DNS",
                                                     "Mail_Address": "d9@domain1.dcdt30prod.sitenv.org"
                                                 }]
                                             },
                          {"name": "D9_invA",
                              "Valid": "false",
                              "Description": "Invalid address-bound certificate for the Direct address in a DNS CERT record.",
                              "Binding_Type": "ADDRESS",
                              "Locaton": [{"Type": "DNS",
                                   "Mail_Address": "d9@domain1.dcdt30prod.sitenv.org"
                               }]
                           }],
                          Background_Certificate: [
                             ]
                         },
                         { code: "D10_LDAP_AB_UnavailableLDAPServer",  testcaseid:"10", name: "D10 - Certificate discovery in LDAP with one unavailable LDAP server",
                             Negative: "false",
                             Optional: "false",
                             Direct_address_2014: "d10@domain3.dcdt30prod.sitenv.org",
                             Direct_address_2015: "d10@domain3.dcdt31prod.sitenv.org",
                             Description: "This test case verifies that your system can query DNS for SRV records and attempts to connect to an LDAP server based on the priority value specified in the SRV records until a successful connection is made. Your system should first attempt to connect to an LDAP server associated with an SRV record containing the lowest priority value (highest priority). Since this LDAP server is unavailable, your system should then attempt to connect to the LDAP server associated with an SRV record containing the second lowest priority value (second highest priority) and discover the valid address-bound X.509 certificate in the available LDAP server.",
                             RTM_Sections: " 15, 18",
                             RFC_2782:  "Page 3, Priority Section",
                             Direct_SHT: "Direct Applicability Statement for Secure Health Transport: Sections 4.0 and 5.3",
                             Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
                             Target_Certificate: [{"name": "D10_valE",
                                                   "Valid": "true",
                                                   "Description": "Valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson schema. The associated SRV record has Priority = 1.",
                                                   "Binding_Type": "ADDRESS",
                                                   "Locaton": [{"Type": "LDAP",
                                                        "Mail_Address": "d10@domain3.dcdt30prod.sitenv.org",
                                                        "Host":"0.0.0.0",
                                                        "Port":"11389"
                                                    }]
                                                }],
                             Background_Certificate: [
                                ]
                            },
                            { code: "D11_DNS_NB_NoDNSCertsorSRV",  testcaseid:"11", name: "D11 - No certificates discovered in DNS CERT records and no SRV records",
                                Negative: "true",
                                Optional: "false",
                                Direct_address_2014: "d11@domain6.dcdt30prod.sitenv.org",
                                Direct_address_2015: "d11@domain6.dcdt31prod.sitenv.org",
                                Description: "This test case verifies that your system does not find any certificates when querying DNS for CERT records and does not find any SRV records in DNS.",
                                RTM_Sections: "1, 3, 18",
                                Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                                Target_Certificate: [],
                                Background_Certificate: [
                                   ]
                               },
                               { code: "D12_LDAP_NB_UnavailableLDAPServer",  testcaseid:"12", name: "D12 - No certificates found in DNS CERT records and no available LDAP servers",
                                   Negative: "true",
                                   Optional: "false",
                                   Direct_address_2014: "d12@domain7.dcdt30prod.sitenv.org",
                                   Direct_address_2015: "d12@domain7.dcdt31prod.sitenv.org",
                                   Description: "This test case verifies that your system can query DNS for SRV records and attempts to connect to an LDAP server associated with the only SRV record that should be found. Since this LDAP server is unavailable or does not exist and no additional SRV records should have been found, your system should not discover any X.509 certificates in either DNS CERT records or LDAP servers.",
                                   RTM_Sections: "1, 3, 18",
                                   Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                                   Target_Certificate: [],
                                   Background_Certificate: [
                                      ]
                                  },
                                  { code: "D13_LDAP_NB_NoCerts",  testcaseid:"13", name: "D13 - No certificates discovered in DNS CERT records or LDAP servers",
                                      Negative: "true",
                                      Optional: "false",
                                      Direct_address_2014: "d13@domain8.dcdt30prod.sitenv.org",
                                      Direct_address_2015: "d13@domain8.dcdt31prod.sitenv.org",
                                      Description: "This test case verifies that your system does not discover any certificates in DNS CERT records or LDAP servers when no certificates should be found.",
                                      RTM_Sections: "1, 3, 18",
                                      Instructions: "Verify that your system did NOT send an email because it could not find a certificate for the Direct address. To pass this test case, you must NOT receive an email in response.",
                                      Target_Certificate: [],
                                      Background_Certificate: [
                                         ]
                                     },
                                     { code: "D14_DNS_AB_TCPLargeCert",  testcaseid:"14", name: "D14 - Discovery of certificate larger than 512 bytes in DNS",
                                         Negative: "false",
                                         Optional: "false",
                                         Direct_address_2014: "d14@domain1.dcdt30prod.sitenv.org",
                                         Direct_address_2015: "d14@domain1.dcdt31prod.sitenv.org",
                                         Description: "This test case verifies that your system can query DNS for address-bound CERT records and discover a valid address-bound X.509 certificate that is larger than 512 bytes using a TCP connection.",
                                         RTM_Sections: "1, 3, 4",
                                         RFC_1035:  "Section 4.2",
                                         RFC_4298:  "Section 4",
                                         Direct_SHT: "Direct Applicability Statement for Secure Health Transport: Section 5.4",
                                         Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
                                         Target_Certificate: [{"name": "D14_valA",
                                                               "Valid": "true",
                                                               "Description": "Valid address-bound certificate that is larger than 512 bytes in a DNS CERT record containing the Direct address in the rfc822Name of the SubjectAlternativeName extension.",
                                                               "Binding_Type": "ADDRESS",
                                                               "Locaton": [{"Type": "DNS",
                                                                    "Mail_Address": "d14@domain1.dcdt30prod.sitenv.org"
                                                                }]
                                                            }],
                                         Background_Certificate: [
                                            ]
                                        },
                                    { code: "D15_LDAP_AB_SRVPriority",  testcaseid:"15", name: "D15 - Certificate discovery in LDAP based on SRV priority value",
                                        Negative: "false",
                                        Optional: "false",
                                        Direct_address_2014: "d15@domain2.dcdt30prod.sitenv.org",
                                        Direct_address_2015: "d15@domain2.dcdt31prod.sitenv.org",
                                        Description: "This test case verifies that your system can query DNS for SRV records and discover a valid address-bound X.509 certificate in the LDAP server associated with an SRV record containing the lowest priority value (highest priority).",
                                        RTM_Sections: "15, 18",
                                        RFC_2782:  "Page 3, Priority Section",
                                        Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
                                        Target_Certificate: [{"name": "D15_valC",
                                                              "Valid": "true",
                                                              "Description": "Valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson schema. The associated SRV record has Priority = 0.",
                                                              "Binding_Type": "ADDRESS",
                                                              "Locaton": [{"Type": "LDAP",
                                                                   "Mail_Address": "d15@domain2.dcdt30prod.sitenv.org",
                                                                   "Host":"0.0.0.0",
                                                                   "Port":"10389"
                                                               }]
                                                           }],
                                        Background_Certificate: [{"name": "D15_invE",
                                            "Valid": "false",
                                            "Description": "Invalid address-bound certificate for the Direct address in an LDAP server. The associated SRV record has Priority = 1.",
                                            "Binding_Type": "ADDRESS",
                                            "Locaton": [{"Type": "LDAP",
                                                 "Mail_Address": "d15@domain2.dcdt30prod.sitenv.org",
                                                     "Host":"0.0.0.0",
                                                     "Port":"11389"
                                            }]
                                         }
                                           ]
                                       },
                                       { code: "D16_LDAP_AB_SRVWeight",  testcaseid:"16", name: "D16 - Certificate discovery in LDAP based on SRV weight value",
                                           Negative: "false",
                                           Optional: "false",
                                           Direct_address_2014: "d16@domain5.dcdt30prod.sitenv.org",
                                           Direct_address_2015: "d16@domain5.dcdt31prod.sitenv.org",
                                           Description: "This test case verifies that your system can query DNS for SRV records and discover a valid address-bound X.509 certificate in the LDAP server associated with an SRV record containing the lowest priority value (highest priority) and the highest weight value when SRV records with the same priority value exist.",
                                           RTM_Sections: "16, 18",
                                           RFC_2782:  "Page 3, Weight Section",
                                           Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
                                           Target_Certificate: [{"name": "D16_valC",
                                                                 "Valid": "true",
                                                                 "Description": "Valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson schema. The associated SRV record has Priority = 0 and Weight = 100.",
                                                                 "Binding_Type": "ADDRESS",
                                                                 "Locaton": [{"Type": "LDAP",
                                                                      "Mail_Address": "d16@domain5.dcdt30prod.sitenv.org",
                                                                      "Host":"0.0.0.0",
                                                                      "Port":"12389"
                                                                  }]
                                                              }],
                                           Background_Certificate: [{"name": "D16_valE",
                                               "Valid": "true",
                                               "Description": "Valid address-bound certificate in an LDAP server with the appropriate mail attribute and InetOrgPerson schema. The associated SRV record has Priority = 0 and Weight = 0.",
                                               "Binding_Type": "ADDRESS",
                                               "Locaton": [{"Type": "LDAP",
                                                "Mail_Address": "d16@domain5.dcdt30prod.sitenv.org",
                                                "Host":"0.0.0.0",
                                                "Port":"10389"
                                                }]
                                            }
                                              ]
                                          },
                                          { code: "D17_DNS_AB_CRLRevocation",  testcaseid:"17", name: "D17 - CRL-based revocation checking for address-bound certificate discovery in DNS",
                                              Negative: "false",
                                              Optional: "false",
                                              Direct_address_2014: "d17@domain9.dcdt30prod.sitenv.org",
                                              Direct_address_2015: "d17@domain9.dcdt31prod.sitenv.org",
                                              Description: "This test case verifies that your system can query DNS for address-bound CERT records and discover a valid X.509 certificate whose CRL-based revocation status indicates that it has not been revoked.",
                                              RTM_Sections: "1, 3",
                                              RFC_4398:  "Section 2.1",
                                              Direct_SHT: "Direct Applicability Statement for Secure Health Transport: Sections 4.0 and 5.3",
                                              Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
                                              Target_Certificate: [{"name": "D17_valA",
                                                                    "Valid": "true",
                                                                    "Description": "Valid, non-revoked address-bound certificate for the Direct address in a DNS CERT record.",
                                                                    "Binding_Type": "ADDRESS",
                                                                    "Locaton": [{"Type": "DNS",
                                                                         "Mail_Address": "d17@domain9.dcdt30prod.sitenv.org"
                                                                     }]
                                                                 }],
                                              Background_Certificate: [{"name": "D17_invB",
                                                  "Valid": "false",
                                                  "Description": "An invalid, revoked address-bound certificate for the Direct address in a DNS CERT record.",
                                                  "Binding_Type": "ADDRESS",
                                                  "Locaton": [{"Type": "DNS",
                                                       "Mail_Address": "d17@domain9.dcdt30prod.sitenv.org"
                                                   }]
                                               },
                                               {"name": "D17_invC",
                                                   "Valid": "false",
                                                   "Description": " An invalid, revoked address-bound certificate for the Direct address in a DNS CERT record.",
                                                   "Binding_Type": "ADDRESS",
                                                   "Locaton": [{"Type": "DNS",
                                                        "Mail_Address": "d17@domain9.dcdt30prod.sitenv.org"
                                                    }]
                                                },
                                                {"name": "D17_invD",
                                                    "Valid": "false",
                                                    "Description": "Invalid domain-bound certificate for the Direct address in a DNS CERT record.",
                                                    "Binding_Type": "DOMAIN",
                                                    "Locaton": [{"Type": "DNS",
                                                         "Mail_Address": "domain9.dcdt30prod.sitenv.org"
                                                     }]
                                                 }
                                                 ]
                                             },           { code: "D18_DNS_AB_AIAIntermediateIssuer",  testcaseid:"18", name: "D18 - AIA-based intermediate issuer certificate retrieval for address-bound certificate discovery in DNS",
            Negative: "false",
            Optional: "false",
            Direct_address_2014: "d18@domain10.dcdt30prod.sitenv.org",
            Direct_address_2015: "d18@domain10.dcdt31prod.sitenv.org",
            Description: "This test case verifies that your system can query DNS for an address-bound CERT record and discover a valid X.509 certificate whose path to the trusted root CA certificate must be retrieved via Authority Information Access (AIA) X.509v3 extension caIssuers HTTP URIs.",
            RTM_Sections: "1, 3",
            RFC_5280:  "Section 4.2.2.1",
            Direct_SHT: "Direct Applicability Statement for Secure Health Transport: Section 4.2.2",
            Instructions: "You should have received an email indicating the test case results for your system. Examine the results to see if your system passed the test case. If you do not receive a message for the test case, then you should assume that the test case failed.",
            Target_Certificate: [{"name": "D18_valA",
                                  "Valid": "true",
                                  "Description": "Valid address-bound certificate issued by an intermediate CA for the Direct address in a DNS CERT record.",
                                  "Binding_Type": "ADDRESS",
                                  "Locaton": [{"Type": "DNS",
                                       "Mail_Address": "d18@domain10.dcdt30prod.sitenv.org"
                                   }]
                               }],
            Background_Certificate: [{"name": "D18_invB",
                "Valid": "true",
                "Description": "Invalid domain-bound certificate for the Direct address in a DNS CERT record.",
                "Binding_Type": "DOMAIN",
                "Locaton": [{"Type": "DNS",
                     "Mail_Address": "domain10.dcdt30prod.sitenv.org"
                 }]
             }
               ]
           }];



$scope.processes= [
    { code: "", name: "--No testcase selected--" },
    { code: "H1_DNS_AB_Normal", name: "H1 - Normal address-bound certificate search in DNS",
Binding_Type: "ADDRESS",
Location_Type: "DNS",
Negative: "false",
Optional: "false",
Description: "This test case verifies that your system's DNS can host and return the expected address-bound X.509 certificate.",
RTM_Sections: "1, 3",
RFC_4398:  "Section 2.1",
Direct_SHT: "Section 5.3",
Instructions: "Enter a Direct address corresponding to an address-bound X.509 certificate that is hosted by your system's DNS and then click Submit. DCDT will attempt to discover the certificate and display the result on the screen."
},
{ code: "H2_DNS_DB_Normal", name: "H2 - Normal domain-bound certificate search in DNS",
Binding_Type: "DOMAIN",
Location_Type: "DNS",
Negative: "false",
Optional: "false",
Description: "This test case verifies that your system's DNS can host and return the expected domain-bound X.509 certificate.",
RTM_Sections: "1, 3",
RFC_4398:  "Section 2.1",
Direct_SHT: "Section 5.3",
Instructions: "Enter a Direct address corresponding to a domain-bound X.509 certificate that is hosted by your system's DNS and then click Submit. DCDT will attempt to discover the certificate and display the result on the screen."
},
{ code: "H3_LDAP_AB_Normal", name: "H3 - Normal address-bound certificate search in LDAP",
Binding_Type: "ADDRESS",
Location_Type: "LDAP",
Negative: "false",
Optional: "false",
Description: "This test case verifies that your system's LDAP server can host and return the expected address-bound X.509 certificate.",
RTM_Sections: "2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22",
RFC_2798:  "Section 9.1.2",
Instructions: "Enter a Direct address corresponding to an address-bound X.509 certificate that is hosted by your system's LDAP server and then click Submit. DCDT will attempt to discover the certificate and display the result on the screen."
},
{ code: "H4_LDAP_DB_Normal", name: "H4 - Normal domain-bound certificate search in LDAP",
Binding_Type: "DOMAIN",
Location_Type: "LDAP",
Negative: "false",
Optional: "false",
Description: "This test case verifies that your system's LDAP server can host and return the expected domain-bound X.509 certificate.",
RTM_Sections: "2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22",
RFC_2798:  "Section 9.1.2",
Instructions: "Enter a Direct address corresponding to a domain-bound X.509 certificate that is hosted by your system's LDAP server and then click Submit. DCDT will attempt to discover the certificate and display the result on the screen."
}];


$scope.selectedItem = $scope.processes[0];
$scope.discorySelectedItem = $scope.discoveryTestCase[0];
$scope.hostingResultsStack=[];
$scope.renderHtml = function (htmlCode) {
    return $sce.trustAsHtml(htmlCode);
};

$scope.checkEmpty = function(testObject){
	if(testObject || testObject === null){
		return "";
	}
	return "None";
};
   $scope.onSelectionChange= function(selectedItem,testcase) {
     $scope.testcase = selectedItem.code;
     if ($scope.testcase !==""){
         if (testcase === "process"){
            $scope.alerts = [];
            $scope.testCaseType = {'Hosting':true};
            $scope.dcdtResult = angular.extend(selectedItem, $scope.testCaseType);
        }else{
            $scope.discalerts = [];
            $scope.testCaseType = {'Discovery':true,'year2014':$scope.year2014,'year2015':$scope.year2015};
            var replaceJsonTest = JSON.stringify(selectedItem);
            var jsonReplacedObjTest = replaceJsonTest.split($scope.emailDomain2014).join($scope.propDcdtDomain2014);
            var jsonReplacedObj1 = jsonReplacedObjTest.split($scope.emailDomain2015).join($scope.propDcdtDomain2015);
            selectedItem = JSON.parse(jsonReplacedObj1);

            if ($scope.year2015){
                var replaceJson = JSON.stringify(selectedItem);
                var jsonReplacedObj = replaceJson.split($scope.emailDomain2014).join($scope.emailDomain2015);
                $scope.dcdtDiscoveryResult = JSON.parse(jsonReplacedObj);
            }else{
                $scope.dcdtDiscoveryResult = selectedItem;
            }
            $scope.dcdtDiscoveryResult = angular.extend($scope.dcdtDiscoveryResult, $scope.testCaseType);
       }
    }else{
        $scope.dcdtResult = null;
        $scope.dcdtDiscoveryResult=null;
    }
        $scope.testCaseId = selectedItem.testcaseid;
    };

$scope.gotodiv = function(anchor) {
    $location.hash(anchor);
   // call $anchorScroll()
    $anchorScroll();
};

    $scope.showhidediv = function(dataobj) {
       var tmpobj = dataobj;
       dataobj.expandResult = !dataobj.expandResult;
       return dataobj.expandResult;
    };
	$scope.closeAlert = function() {
		$scope.alerts = [];
		$timeout.cancel($scope.timeout);
	};
	$scope.closeDiscAlert = function() {
		$scope.discalerts = [];
		$timeout.cancel($scope.timeout);
	};
	function showAlert(type, msg) {
		$scope.alerts = [];
		$scope.alerts.push({
			type: type,
			msg: msg
		});
		$scope.timeout = $timeout($scope.closeAlert, 60000);
	}
	function showDiscAlert(type, msg) {
		$scope.discalerts = [];
		$scope.discalerts.push({
			type: type,
			msg: msg
		});
		$scope.timeout = $timeout($scope.closeDiscAlert, 60000);
	}

        $scope.restdata = function() {
             $scope.dcdtResult = null;
             $scope.selectedItem = $scope.processes[0];
             $scope.directAddress ="";
             $scope.testcase ="";
             $scope.alerts = [];
             $scope.hostingResultError =[];
        };

     $scope.resetDiscData = function() {
         $scope.discResultEmailAddr = "";
         $scope.discEmailAddr = "";
         $scope.discoveryReport  =[];
         $scope.discalerts = [];
     };

    $scope.ignoreTestcase = function(testcaseid) {
       return (testcaseid.testcaseid !== "17" &&
         testcaseid.testcaseid !== "18" );
    };

    $scope.discValidate = function() {
        if (!$scope.discEmailAddr || $scope.discEmailAddr === "") {
           showDiscAlert('danger', 'Direct Address must be an email');
        }else if (!$scope.discResultEmailAddr || $scope.discResultEmailAddr === "") {
           showDiscAlert('danger', 'Result Address must be an email');
        }else{
        $scope.discalerts = [];
   $scope.discValidateRequest = {
           "@type": "discoveryTestcaseMailMapping",
           "directAddr": $scope.discEmailAddr,
           "resultsAddr": $scope.discResultEmailAddr,
           "year": $scope.pageTitle,
           "hostingcase":"NO"
           };
           DCDTValidatorFactory.save($scope.discValidateRequest, function(data) {
              console.log(" $scope.response dcdt::::"+ angular.toJson(data,true));
             // $scope.hostingResults = angular.extend($scope.hostingResults, data);
              $scope.discoveryReport = data;
            }, function(data) {
               $scope.laddaLoading = false;
               throw {
                   code: data.data.code,
                   url: data.data.url,
                   message: data.data.message
               };
           });
}
};
      $scope.validate = function() {
      if (!$scope.directAddress || $scope.directAddress === "") {
         showAlert('danger', 'Direct Address must be an email');
      }else if ($scope.testcase === "") {
          showAlert('danger', 'Please Select a Hosting Testcase');
      }else{
            $scope.alerts = [];
            $scope.validator = {
                    "@type": "hostingTestcaseSubmission",
                    "directAddr": $scope.directAddress,
                    "testcase": $scope.testcase,
                    "year": $scope.pageTitle,
                    "hostingcase":"YES"
                   };
                    DCDTValidatorFactory.save($scope.validator, function(data) {
                       console.log(" $scope.response hostingTestcaseSubmission dcdt::::"+ angular.toJson(data,true));
                       if (data.status !=='error'){
                           angular.forEach($scope.hostingResult, function(test) {
                               angular.forEach($scope.hostingResult[$scope.hostingResult.indexOf(test)].itemloop, function(itemloopobj) {
                                   itemloopobj.testcase.expandResult =false;
                               });
                            });
                       }
                         $scope.hostingResult = data;
                         $scope.hostingResultError = data;
                         $scope.hostingResultsStack.push({'itemloop' : [data]});
                         $scope.hostingResult = $scope.hostingResultsStack;
                   }, function(data) {
                        $scope.laddaLoading = false;
                        throw {
                            code: data.data.code,
                            url: data.data.url,
                            message: data.data.message
                        };
                    });
         }
        };
        $scope.apiUrl = ApiUrl.get();

    }
]);
