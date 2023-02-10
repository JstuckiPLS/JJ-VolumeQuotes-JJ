angular.module('plsApp').controller('PricingProfileCtrl', ['$scope', '$routeParams', '$location', 'ProfileDetailsService',
    'ProfilesListToCopyService', 'GetUnsavedProfileCopyService', 'GetCustomerMarginProfileService', 'routes',
    function ($scope, $routeParams, $location, ProfileDetailsService, ProfilesListToCopyService,
              GetUnsavedProfileCopyService, GetCustomerMarginProfileService, routes) {
        'use strict';

        var val = $location.path();
        var regExp = new RegExp(/\/(-?\d+)\//);
        var profileNum = regExp.exec(val);
        var CARRIER_API = "CARRIER_API";

        $scope.hideTabs = profileNum[1] === '-1';

        $scope.hideForCarrierApi = function(type) {
            return $scope.profileDetails
                && $scope.profileDetails.profileDetails[0].carrierType === "CARRIER_API"
                && $scope.profileDetails.ltlPricingType === type;
        };

        $scope.customerMarginSetup = $routeParams.operationType === "cm";

        function getCopyFromData(profileDetailType) {
            var copyCriteria = {
                copyFromPricingDetailType: profileDetailType,
                profileId: $scope.profileDetails.id
            };

            ProfilesListToCopyService.get({}, copyCriteria, function (profiles) {
                $scope.copyProfiles = profiles;
            });
        }

        $scope.overrideProfile = "N";

        function loadTabAction(profileDetailType) {
            $scope.pricingType = profileDetailType ? profileDetailType : 'BUY_SELL';

            if (profileDetailType === 'BUY') {
                $scope.profileDetailType = 'buy';
                $scope.priceTabName = 'Buy Pricing';
            } else if (profileDetailType === 'SELL') {
                $scope.profileDetailType = 'sell';
                $scope.marginPanelName = 'Sell';
                $scope.priceTabName = 'Sell Pricing';
            } else {
                $scope.profileDetailType = 'pricing';
                $scope.marginPanelName = 'Margins:';
                $scope.priceTabName = 'Pricing';
            }

            getCopyFromData(profileDetailType);
        }

        var locationUpdate = function () {
            var val = _.keys($location.search())[0];

            if (val.search('buy') > -1) {
                loadTabAction("BUY");

                if (val === 'buy') {
                    $scope.$root.ignoreLocationChange();
                    $location.search({
                        'buy.details': true
                    });
                }
            } else if (val.search('sell') > -1) {
                loadTabAction("SELL");

                if (val === 'sell') {
                    $scope.$root.ignoreLocationChange();
                    $location.search({
                        'sell.details': true
                    });
                }
            } else if (val.search('pricing') > -1) {
                loadTabAction();

                if (val === 'pricing') {
                    $scope.$root.ignoreLocationChange();
                    if($scope.profileDetails.isLTLLCApi() && !$scope.profileDetails.isCSP()){
                        $location.search({
                            'pricing.freight-bill': true
                        });
                    } else {
                        $location.search({
                            'pricing.details': true
                        });
                    }
                }
            } else if (val.search('profile') > -1) {
                if (val === 'profile') {
                    $scope.$root.ignoreLocationChange();
                    $location.search({
                        'profile.details': true
                    });
                }
            }
        };

        var reload = function () {
            _.keys($location.search()).forEach(function (element) {
                var permutations = _.reduce(element.split('.'), function (memo, item) {
                    var prev;

                    if (_.isEmpty(memo)) {
                        memo.push(item);
                    } else {
                        prev = memo.pop();
                        memo.push(prev);
                        memo.push(prev + "." + item);
                    }

                    return memo;
                }, []);

                permutations.forEach(function (rule) {
                    if (_.has(routes, rule)) {
                        _.each(routes[rule], function (value, key) {
                            $scope[key] = value;
                        });
                    }
                });
            });

            locationUpdate();
        };

        $scope.$on('$routeUpdate', function () {
            if ($scope.profileDetails) {
                reload();
            }
        });

        $scope.$watch('$location.search()', function () {
            if ($scope.profileDetails) {
                reload();
            }
        });

        $scope.benchmarkCarrierOptions = [{
            id: 'ALL',
            label: 'All Carriers'
        }, {
            id: 'SPECIFIC',
            label: 'Specific Carrier'
        }];

        $scope.profileNoteLabel = "Carrier Note";

        var setProfileToScope = function (profile) {
            var pricingProfiles = angular.copy(profile);
            $scope.profileDetails = profile;

            if ($scope.profileDetails.ltlPricingType === "BUY_SELL") {
                //profileDetails.profileDetails[0] must contain buy details
                $scope.profileDetails.profileDetails[0] = pricingProfiles.profileDetails[0].pricingDetailType === "BUY" ?
                        pricingProfiles.profileDetails[0] : pricingProfiles.profileDetails[1];
                //profileDetails.profileDetails[1] must contain sell details
                $scope.profileDetails.profileDetails[1] = pricingProfiles.profileDetails[1].pricingDetailType === "SELL" ?
                        pricingProfiles.profileDetails[1] : pricingProfiles.profileDetails[0];
            }

            if ($routeParams.operationType === "addbm") {
                $scope.profileDetails.ltlPricingType = "BENCHMARK";
                $scope.profileDetails.profileDetails = [{}];
                $scope.profileDetails.profileDetails[0].mscale = "M10";
                $scope.profileDetails.shipperOrganization = {};
                $scope.profileDetails.shipperOrganization.id = $routeParams.pricingId;
            }

            $scope.profileDetails.isActive = (profile.status ? (profile.status === 'ACTIVE') : false);
            $scope.profileDetails.isBlocked = (profile.blocked ? (profile.blocked === 'Y') : false);
            $scope.profileDetails.isBlckdFrmBkng = profile.blockedFromBooking === 'YES';

            if ($scope.profileDetails.profileDetails) {
              if ($scope.profileDetails.profileDetails[0].pricingDetailType === "BUY") {
                 $scope.profileDetails.profileDetails.useBlanketAsBuy = profile.profileDetails[0].useBlanket ?
                    (profile.profileDetails[0].useBlanket === 'Y') : false;
                 $scope.profileDetails.profileDetails.useBlanketAsSell = profile.profileDetails[1].useBlanket ?
                    (profile.profileDetails[1].useBlanket === 'Y') : false;
              } else if ($scope.profileDetails.profileDetails[0].pricingDetailType === "SELL") {
                 $scope.profileDetails.profileDetails.useBlanketAsSell = profile.profileDetails[0].useBlanket ?
                    (profile.profileDetails[0].useBlanket === 'Y') : false;
                 $scope.profileDetails.profileDetails.useBlanketAsBuy = profile.profileDetails[1].useBlanket ?
                    (profile.profileDetails[1].useBlanket === 'Y') : false;
              }
            }

            if ($routeParams.operationType === "cpyBk" && $scope.profileDetails.ltlPricingType === "BLANKET") {
                $scope.profileDetails.ltlPricingType = "BLANKET_CSP";
            }

            if ($routeParams.operationType === "cm" && !profile.id) {
                $scope.profileDetails.ltlPricingType = "MARGIN";
                $scope.profileDetails.rateName = "Customer Margin - " + $routeParams.pricingId;
                $scope.profileDetails.profileDetails = [{}];
                $scope.profileDetails.profileDetails[0].mscale = "M10";
                $scope.profileDetails.profileDetails[0].carrierType = 'MANUAL';
                $scope.profileDetails.shipperOrganization = {};
                $scope.profileDetails.shipperOrganization.id = $routeParams.pricingId;
            }

            if ($scope.profileDetails.ltlPricingType === "BENCHMARK") {
                if ($scope.profileDetails.id !== null && $scope.profileDetails.id !== undefined) {
                    if ($scope.profileDetails.carrierOrganization === null
                            || $scope.profileDetails.carrierOrganization === undefined) {
                        $scope.benchmarkCarrierOption = "ALL";
                    } else {
                        $scope.benchmarkCarrierOption = "SPECIFIC";
                    }
                }

                if ($scope.profileDetails.id === null || $scope.profileDetails.id === undefined) {
                    if ($scope.profileDetails.carrierOrganization === null
                            || $scope.profileDetails.carrierOrganization === undefined) {
                        $scope.benchmarkCarrierOption = "ALL";
                    } else {
                        $scope.benchmarkCarrierOption = "SPECIFIC";
                    }
                }
                $scope.profileNoteLabel = "Note";
            }

            if ($scope.profileDetails.carrierOrganization) {
                $scope.profileDetails.carrierOrganization.name = profile.carrierOrganization.scac + ":" + profile.carrierOrganization.name;
            }

            if ($scope.profileDetails.actCarrierOrganization) {
                $scope.profileDetails.actCarrierOrganization.name = profile.actCarrierOrganization.scac + 
                                                                        ":" + profile.actCarrierOrganization.name;
            }
            $scope.profileDetails.isCSPAndCarrierApi = function() {
                return $scope.profileDetails.ltlPricingType === 'CSP' && $scope.profileDetails.profileDetails[0].carrierType === CARRIER_API;
            };
            $scope.profileDetails.isLTLLCApi = function() {
                return $scope.profileDetails.profileDetails[0].carrierType === "LTLLC";
            };
            $scope.profileDetails.isCSP = function() {
                return $scope.profileDetails.ltlPricingType === 'CSP';
            };
            reload();
        };

        var getProfileDetailId = function (value, profileDetails) {
            var object = _.find(profileDetails, function (item) {
                if (item.pricingDetailType === value) {
                    return item;
                }
            });

            return object.id;
        };

        var setProfileDetailIdToScope = function (profile) {
            var val = _.keys($location.search())[0];

            if (val.search('buy') > -1) {
                $scope.profileDetailId = getProfileDetailId('BUY', profile.profileDetails);
            } else if (val.search('sell') > -1) {
                $scope.profileDetailId = getProfileDetailId('SELL', profile.profileDetails);
            } else if (val.search('pricing') > -1) {
                $scope.profileDetailId = profile.profileDetails[0].id;
            }
        };

        $scope.loadProfile = function (operationType, profileId) {
            if (operationType === "addbm") {
                profileId = -1;
                $scope.hideTabs = true;
            }

            if (operationType === "cpy" || operationType === "cpyBk") {
                $scope.hideTabs = true;
                GetUnsavedProfileCopyService.get({
                    id: profileId
                }, function (profile) {
                    setProfileToScope(profile);
                    setProfileDetailIdToScope(profile);
                });
            } else if (operationType === "cm") {
                GetCustomerMarginProfileService.get({
                    id: profileId
                }, function (profile) {
                    $scope.hideTabs = !profile.id;
                    // Customer Margin
                    setProfileToScope(profile);
                    setProfileDetailIdToScope(profile);
                });
            } else {
                ProfileDetailsService.get({
                    id: profileId
                }, function (profile) {
                    // Benchmark
                    setProfileToScope(profile);
                    setProfileDetailIdToScope(profile);
                });
            }
        };

        $scope.loadProfile($routeParams.operationType, $routeParams.pricingId);
    }
]);
