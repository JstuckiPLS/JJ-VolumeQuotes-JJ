package com.pls.core.dao;

/**
 * Manager allowing to enable specified hibernate fetch profiles.
 * 
 * @author Viacheslav Krot
 * 
 */
public interface FetchProfileManager {
    /**
     * Enable given fetch profile.
     * 
     * @param profileName
     *            fetch profile to enable.
     */
    void enableFetchProfile(String profileName);
}
