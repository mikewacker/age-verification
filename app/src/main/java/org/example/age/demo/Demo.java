package org.example.age.demo;

import org.example.age.api.client.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.app.AvsApp;
import org.example.age.app.SiteApp;

/** Runs the demo. */
@SuppressWarnings("unused")
public final class Demo {

    private static AvsApp checkMyAge = new AvsApp("check-my-age");
    private static SiteApp crackle = new SiteApp("crackle");
    private static SiteApp pop = new SiteApp("pop");

    private static AvsApi parentAvsClient = DemoInfra.createClient(9090, "John Smith", AvsApi.class);
    private static AvsApi childAvsClient = DemoInfra.createClient(9090, "Billy Smith", AvsApi.class);
    private static SiteApi parentCrackleClient = DemoInfra.createClient(8080, "publius", SiteApi.class);
    private static SiteApi childCrackleClient = DemoInfra.createClient(8080, "publius-jr", SiteApi.class);
    private static SiteApi parentPopClient = DemoInfra.createClient(8081, "JohnS", SiteApi.class);
    private static SiteApi childPopClient = DemoInfra.createClient(8081, "BillyS", SiteApi.class);

    /** Main method. */
    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) {
        try {
            DemoInfra.startServer(checkMyAge, "config-check-my-age.yaml");
            DemoInfra.startServer(crackle, "config-crackle.yaml");
            DemoInfra.startServer(pop, "config-pop.yaml");

            // TODO
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    // static class
    private Demo() {}
}
