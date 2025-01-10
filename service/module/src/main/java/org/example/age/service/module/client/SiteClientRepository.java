package org.example.age.service.module.client;

import jakarta.ws.rs.NotFoundException;
import org.example.age.api.client.SiteApi;

/** Repository of clients for each site. Throws {@link NotFoundException} if the site is not registered. */
@FunctionalInterface
public interface SiteClientRepository {

    SiteApi get(String siteId);
}
