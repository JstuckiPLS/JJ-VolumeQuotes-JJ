angular.module('plsApp').controller('ProfileDetailsCtrl', ['$scope', '$route', '$routeParams', '$location', 'ProfileDictionaryService',
    'SaveCopiedProfileService', 'ProfileDetailsService', 'ProfileSelectedCarrierAPIService',
    function ($scope, $route, $routeParams, $location, ProfileDictionaryService, SaveCopiedProfileService,
              ProfileDetailsService, ProfileSelectedCarrierAPIService) {
        'use strict';

        $scope.smc3Carriers = [];

        $scope.benchmarkCarrierOptions = [
            {id: 'ALL', label: 'All Carriers'},
            {id: 'SPECIFIC', label: 'Specific Carrier'}
        ];

        $scope.profileNoteLabel = "Carrier Note";

        $scope.setSMC3Carrier = function () {
            if ($scope.profileDictionary && $scope.profileDictionary.smc3Carriers && !_.isUndefined($scope.profileDetails.profileDetails)) {
                if ($scope.profileDetails.profileDetails[0].carrierType === "SMC3" && $scope.profileDetails.carrierOrganization
                        && $scope.profileDetails.carrierOrganization.name) {
                    $scope.smc3Carriers.length = 0;
                    var carrierName = "";

                    $.each($scope.profileDictionary.smc3Carriers, function (key, value) {
                        carrierName = value.scac + ":" + value.name;

                        if ($scope.profileDetails.carrierOrganization.name === carrierName) {
                            $scope.smc3Carriers.push(value);
                        }
                    });
                }
            }
        };

        var setMileageTypes = function () {
            if (!_.isUndefined($scope.profileDetails.profileDetails)) {
                var i = 0;

                for (i = 0; i < $scope.profileDetails.profileDetails.length; i += 1) {
                    var j = 0;

                    if ($scope.profileDetails.profileDetails[i].mileageType !== null && $scope.profileDetails.profileDetails[i].mileageType !== '') {
                        for (j = 0; j < $scope.profileDictionary.mileageTypes.length; j += 1) {
                            if ($scope.profileDetails.profileDetails[i].mileageType === $scope.profileDictionary.mileageTypes[j].id.mileageType &&
                                    $scope.profileDetails.profileDetails[i].mileageVersion ===
                                    $scope.profileDictionary.mileageTypes[j].id.mileageVersion) {
                                $scope.profileDetails.profileDetails[i].mileageCalc = $scope.profileDictionary.mileageTypes[j].id;
                            }
                        }
                    }
                }
            }
        };

        var findIndexOfObject = function (array) {
            var index = 0;

            for (index = 0; index < array.length; index += 1) {
                if (array[index].ltlPricingType === 'BLANKET_CSP') {
                    break;
                }
            }

            return index;
        };

        function fixSMC3Tariff() {
            if ($scope.profileDetails && $scope.profileDetails.profileDetails) {
                _.each($scope.profileDetails.profileDetails, function(profileDetail) {
                    if (profileDetail.smc3Tariff) {
                        var tariffName = profileDetail.smc3Tariff.substr(0, profileDetail.smc3Tariff.lastIndexOf("_") + 1);
                        var foundTariff = _.find($scope.profileDictionary.smc3Tariffs, function(tariff) {
                            return tariff.tariffName.indexOf(tariffName) === 0;
                        });
                        if (foundTariff) {
                            profileDetail.smc3Tariff = foundTariff.tariffName;
                        }
                    }
                });
            }
        }

        var reloadPricingType = function (parentType, dictionaryPricingTypes) {
            var index = findIndexOfObject(dictionaryPricingTypes);
            dictionaryPricingTypes.splice(index, 1);
        };

        ProfileDictionaryService.get(function (response) {
            $scope.profileDictionary = response;
            $scope.profileDictionary.carrierTypesWithoutAPI = $scope.profileDictionary.carrierTypes.filter(function(type) {
                return type.ltlRatingCarrierType !== "CARRIER_API" && type.ltlRatingCarrierType !== "LTLLC";
            });
            setMileageTypes();
            fixSMC3Tariff();
            $scope.setSMC3Carrier();

            $scope.$watch('profileDetails', function (val) {
                if (($route.current.params.operationType === 'cpy' && !_.isUndefined(val) && val.ltlPricingType !== 'BLANKET')
                        || ($scope.profileDetails.ltlPricingType !== 'BLANKET_CSP' && $route.current.params.operationType === 'edit')
                        || ($route.current.params.operationType === 'add')) {
                    if ($route.current.params.operationType === 'add') {
                         $scope.profileDictionary.pricingTypes = $scope.profileDictionary.pricingTypes.filter(function(pricType) {
                               return pricType.ltlPricingType !== 'BENCHMARK' && pricType.ltlPricingType !== 'SMC3';
                         });
                    }
                    reloadPricingType(val.ltlPricingType, $scope.profileDictionary.pricingTypes);
                }
            });
        });

        $scope.overrideProfile = "N";

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
                $scope.profileDetails.applicableCustomers = [{}];
                $scope.profileDetails.applicableCustomers[0].customer = {};
                $scope.profileDetails.applicableCustomers[0].customer.id = $routeParams.pricingId;
            }

            $scope.profileDetails.isActive = (profile.status ? (profile.status === 'ACTIVE') : true);
            $scope.profileDetails.isBlocked = (profile.blocked ? (profile.blocked === 'Y') : false);
            $scope.profileDetails.isBlckdFrmBkng = profile.blockedFromBooking === 'YES';
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
                    if ($scope.profileDetails.carrierOrganization === null || $scope.profileDetails.carrierOrganization === undefined) {
                        $scope.benchmarkCarrierOption = "ALL";
                    } else {
                        $scope.benchmarkCarrierOption = "SPECIFIC";
                    }
                }

                if ($scope.profileDetails.id === null || $scope.profileDetails.id === undefined) {
                    if ($scope.profileDetails.carrierOrganization === null || $scope.profileDetails.carrierOrganization === undefined) {
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

            if ($scope.profileDictionary !== null && $scope.profileDictionary !== undefined) {
                setMileageTypes();
                fixSMC3Tariff();
            }
            
            $scope.profileDetails.isLTLLCApi = function() {
                return $scope.profileDetails.profileDetails[0].carrierType === "LTLLC";
            };
        };

        var loadProfile = function (profileId) {
            ProfileDetailsService.get({id: profileId}, function (profile) {
                setProfileToScope(profile);
            });
        };

        $scope.initProfileDetails = function () {
            if ($scope.profileDetails.ltlPricingType !== "BUY_SELL") {
                if ($scope.profileDetails.profileDetails === undefined || $scope.profileDetails.profileDetails.length === 0) {
                    $scope.profileDetails.profileDetails = [{}];
                    $scope.profileDetails.profileDetails[0].mscale = "M10";
                }
            } else {
                if ($scope.profileDetails.profileDetails === undefined || $scope.profileDetails.profileDetails.length === 0) {
                    $scope.profileDetails.profileDetails = [{}, {}];
                    $scope.profileDetails.profileDetails[0].mscale = "M10";
                    $scope.profileDetails.profileDetails[1].mscale = "M10";
                } else if ($scope.profileDetails.profileDetails.length === 1) {
                    $scope.profileDetails.profileDetails[1] = {};
                    $scope.profileDetails.profileDetails[1].mscale = "M10";
                }
            }

            $scope.setSMC3Carrier();

            if (_.isUndefined($scope.profileDetails.profileDetails[0].carrierType)) {
                $scope.profileDetails.profileDetails[0].carrierType = "SMC3";
            }
        };

        $scope.openAssignedCustomersDialog = function () {
            $scope.$broadcast('openAssignedCustomersDialog');
        };

        $scope.openAffectedCustomersDialog = function () {
            $scope.$broadcast('openAffectedCustomersDialog');
        };

        $scope.submit = function() {
            if (!$scope.profileDetails.effDate || !$scope.profileDetails.rateName || $scope.profileDetails.rateName.trim() === ""
                    || (_.isEmpty($scope.profileDetails.carrierOrganization) && $scope.benchmarkCarrierOption !== 'ALL'
                            && $scope.profileDetails.ltlPricingType !== "MARGIN")) {
                $scope.$root.$emit('event:application-error', 'Saving Profile validation failed!', 'Please fill required fields.');
                return;
            }

            if ($scope.profileDetails.ltlPricingType !== 'BLANKET' && $scope.profileDetails.ltlPricingType !== 'MARGIN'
                    && $scope.profileDetails.ltlPricingType !== 'BENCHMARK' && _.isEmpty($scope.profileDetails.applicableCustomers)) {
                $scope.$root.$emit('event:application-error',
                        'Saving Profile validation failed!', 'Please select customers applicable for the profile.');
                return;
            }

            if ($scope.profileDetails.isBlckdFrmBkng && _.isEmpty($scope.profileDetails.actCarrierOrganization)) {
                $scope.$root.$emit('event:application-error',
                        'Saving Profile validation failed!', 'Please select original SCAC.');
                return;
            }

            if ($scope.profileDetails.isActive) {
                $scope.profileDetails.status = 'ACTIVE';
            } else {
                $scope.profileDetails.status = 'INACTIVE';
            }
            
            if (!$scope.profileDetails.isLTLLCApi()){
                //Only ltllc profiles support this.
                $scope.profileDetails.displayQuoteNumberOnBol = false;
                $scope.profileDetails.trackWithLTLLC = false;
                $scope.profileDetails.dispatchWithLTLLC = false;
            }

            $scope.profileDetails.blockedFromBooking = $scope.profileDetails.isBlckdFrmBkng ? 'YES' : 'NO';

            if ($scope.profileDetails.isBlocked) {
                $scope.profileDetails.blocked = 'Y';
            } else {
                $scope.profileDetails.blocked = 'N';
            }

            if ($scope.profileDetails.ltlPricingType === 'BUY_SELL') {
                $scope.profileDetails.profileDetails[0].pricingDetailType = 'BUY';
                $scope.profileDetails.profileDetails[1].pricingDetailType = 'SELL';
                $scope.profileDetails.profileDetails[0].useBlanket = $scope.profileDetails.profileDetails.useBlanketAsBuy ? 'Y' : 'N';
                $scope.profileDetails.profileDetails[1].useBlanket = $scope.profileDetails.profileDetails.useBlanketAsSell ? 'Y' : 'N';
            }

            if ($scope.profileDetails.profileDetails[0] !== null) {
                if ($scope.profileDetails.profileDetails[0].carrierType !== 'API' && $scope.profileDetails.profileDetails[0].mileageCalc) {
                    $scope.profileDetails.profileDetails[0].mileageType = $scope.profileDetails.profileDetails[0].mileageCalc.mileageType;
                    $scope.profileDetails.profileDetails[0].mileageVersion = $scope.profileDetails.profileDetails[0].mileageCalc.mileageVersion;
                }
            }

            if ($scope.profileDetails.profileDetails.length > 1 && $scope.profileDetails.profileDetails[1] !== null) {
                if ($scope.profileDetails.profileDetails[1].carrierType !== 'API' && $scope.profileDetails.profileDetails[1].mileageCalc) {
                    $scope.profileDetails.profileDetails[1].mileageType = $scope.profileDetails.profileDetails[1].mileageCalc.mileageType;
                    $scope.profileDetails.profileDetails[1].mileageVersion = $scope.profileDetails.profileDetails[1].mileageCalc.mileageVersion;
                }
            }

            if (!$scope.profileDetails.id && $scope.profileDetails.ltlPricingType !== 'MARGIN') {
                if ($scope.profileDetails.ltlPricingType === 'BENCHMARK' && $scope.benchmarkCarrierOption === "ALL") {
                    $scope.profileDetails.carrierType = "SMC3";
                    $scope.profileDetails.carrierOrganization = null;
                } else {
                    $scope.profileDetails.carrierCode = $scope.profileDetails.carrierOrganization.name.substring(0, 4);
                }
            }

            if ((!$scope.profileDetails.aliasName || $scope.profileDetails.aliasName.trim() === "")
                    && ($scope.profileDetails.ltlPricingType !== 'MARGIN'
                    && ($scope.profileDetails.ltlPricingType === 'BENCHMARK' && $scope.benchmarkCarrierOption !== "ALL"))) {
                $scope.profileDetails.aliasName = $scope.profileDetails.carrierOrganization.name;
            }

            if (!$scope.plsProfileDetailsForm.$valid) {
                $scope.$root.$emit('event:application-error', 'Please, fill all fields correctly.');
                return;
            }

            if ($route.current.params.operationType !== 'cpy' && $route.current.params.operationType !== 'cpyBk') {
                var redirectAfterSave = false;

                if (_.isEmpty($scope.profileDetails.id)) {
                    redirectAfterSave = true;
                }

                ProfileDetailsService.save({}, $scope.profileDetails, function (response) {
                    if (redirectAfterSave) {
                        $scope.$root.ignoreLocationChange();
                        if ($scope.customerMarginSetup) {
                            $location.url('/pricing/tariffs/' + $routeParams.pricingId + '/cm?profile.details');
                            $scope.loadProfile($routeParams.operationType, $routeParams.pricingId);
                        } else {
                            $location.url('/pricing/tariffs/' + response.id + '/edit?profile.details');
                            loadProfile(response.id);
                        }
                    } else {
                        loadProfile(response.id);
                    }

                    $scope.$root.$emit('event:operation-success', 'Successfully saved profile!');
                }, function (response) {
                    if (response && response.data && response.data.errorMsg) {
                        $scope.$root.$emit('event:application-error', 'Saving Profile validation failed!', response.data.errorMsg);
                    } else {
                        $scope.$root.$emit('event:application-error', 'Saving Profile failed!', '');
                    }
                });
            } else {
                //TODO: Need to revisit this section.
                SaveCopiedProfileService.copy({}, $scope.profileDetails, function (response) {
                    $location.url('/pricing/tariffs/' + response.id + '/edit?profile.details');
                    $scope.$root.$emit('event:operation-success', 'Successfully saved profile!');
                }, function (response) {
                    if (response && response.data && response.data.errorMsg) {
                        $scope.$root.$emit('event:application-error', 'Saving Profile validation failed!', response.data.errorMsg);
                    } else {
                        $scope.$root.$emit('event:application-error', 'Saving Profile failed!', '');
                    }
                });
            }
        };

        $scope.clearSMC3TariffsForCarrierAPI = function() {
            var details = $scope.profileDetails.profileDetails[0];
            if (details.carrierType === "CARRIER_API" || details.carrierType === "LTLLC" ) {
                details.smc3Tariff = undefined;
            }
        };

        $scope.isCarrierAPISelectedForBuySell = function() {
            return $scope.profileDetails.ltlPricingType === 'BUY_SELL' && $scope.profileDetails.profileDetails[0].carrierType === 'CARRIER_API';
        };

        $scope.$watch('profileDetails.profileDetails[0].carrierType', function(newValue) {
            if ((newValue === 'CARRIER_API' || newValue === 'LTLLC') && $scope.profileDetails.ltlPricingType === 'BUY_SELL') {
                $scope.profileDetails.profileDetails.useBlanketAsBuy = true;
                $scope.profileDetails.profileDetails.useBlanketAsSell = false;
            }
        });

        $scope.getCarrierAPIDetails = function (profileDetails) {
            if (profileDetails.carrierType === 'API') {
                var carrierOrgId = $scope.profileDetails.carrierOrganization.id;

                if (carrierOrgId === null || carrierOrgId === undefined || carrierOrgId === 'undefined') {
                    carrierOrgId = $scope.profileDetails.carrierOrganization;
                }

                ProfileSelectedCarrierAPIService.get({id: carrierOrgId}, function (response) {
                    if (response.id) {
                        profileDetails.carrierAPIDetails = response;
                    } else {
                        alert("No API avaibale");
                        //TODO: ADD ERROR MESSAGE SAYING "No API available".
                    }
                });
            } else if (profileDetails.carrierType === 'SMC3') {
                $scope.setSMC3Carrier();
            }
        };

        $scope.setBenchmarkCarrierType = function () {
            if ($scope.benchmarkCarrierOption === "ALL") {
                $scope.profileDetails.carrierType = "SMC3";
            }
        };

        $scope.$watch('profileDetails.carrierOrganization', function () {
            $scope.setSMC3Carrier();
        }, true);

        $scope.setSMC3Carrier();

        $scope.cancel = function () {
            $location.url('/pricing/tariffs');
        };
    }
]);

angular.module('plsApp').controller('ShowAffectedCustomersCtrl', ['$scope', 'ProfileAffectedCustomersService', 'NgGridPluginFactory',
    function ($scope, ProfileAffectedCustomersService, NgGridPluginFactory) {
        'use strict';

        function loadCustomers() {
            if ($scope.profileDetails.id) {
                ProfileAffectedCustomersService.get({id: $scope.profileDetails.id}, function (response) {
                    $scope.customersList = response;
                });
            }
        }

        $scope.gridOptions = {
            enableColumnResize: true,
            data: 'customersList',
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            columnDefs: [{
                field: 'blockedCustomer.name',
                displayName: 'Name'
            }, {
                field: 'blockedCustomer.companyCode',
                displayName: 'Company Code',
                searchable: false
            }, {
                field: 'blockedCustomer.id',
                displayName: 'ID'
            }],
            refreshTable: loadCustomers,
            progressiveSearch: true,
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()]
        };

        /**
         * Open modal dialog.
         */
        $scope.$on('openAffectedCustomersDialog', function () {
            $scope.shouldBeOpen = true;

            loadCustomers();
        });

        /**
         * Close dialog.
         */
        $scope.close = function () {
            $scope.shouldBeOpen = false;
        };
    }
]);
