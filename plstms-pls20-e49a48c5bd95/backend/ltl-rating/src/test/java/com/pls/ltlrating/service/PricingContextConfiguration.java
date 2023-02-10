package com.pls.ltlrating.service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.pls.mileage.service.MileageService;
import com.pls.smc3.service.CarrierTransitClient;

/**
 * Context configuration for pricing tests.
 * 
 * @author Aleksandr Leshchenko
 */
@Profile("PricingTest")
@Configuration
public class PricingContextConfiguration {

    /**
     * Get mock of {@link LtlSMC3Service}.
     * 
     * @return mock of {@link LtlSMC3Service}
     */
    @Bean
    @Primary
    public LtlSMC3Service ltlSMC3Service() {
        return Mockito.mock(LtlSMC3Service.class);
    }

    /**
     * Get mock of {@link CarrierTransitClient}.
     * 
     * @return mock of {@link CarrierTransitClient}
     */
    @Bean
    @Primary
    public CarrierTransitClient carrierTransitClient() {
        return Mockito.mock(CarrierTransitClient.class);
    }

    /**
     * Get mock of {@link MileageService}.
     * 
     * @return mock of {@link MileageService}
     */
    @Bean
    @Primary
    public MileageService mileageService() {
        return Mockito.mock(MileageService.class);
    }
}