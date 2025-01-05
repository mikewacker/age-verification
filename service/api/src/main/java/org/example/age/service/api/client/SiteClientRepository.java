package org.example.age.service.api.client;

import jakarta.ws.rs.NotAuthorizedException;
import org.example.age.api.client.SiteApi;

/** Repository of clients for each site. Throws {@link NotAuthorizedException} if the site is not registered. */
@FunctionalInterface
public interface SiteClientRepository {

    SiteApi get(String siteId);
}
