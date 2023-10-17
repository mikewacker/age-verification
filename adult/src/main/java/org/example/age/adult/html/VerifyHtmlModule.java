package org.example.age.adult.html;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.html.HtmlModule;

/**
 * Dagger module that publishes a binding for <code>@Named("verifyHtml") {@link HttpHandler}</code>,
 * which overlays an age verification check on top of static HTML files.
 */
@Module(includes = {HtmlModule.class})
public interface VerifyHtmlModule {

    @Binds
    @Named("verifyHtml")
    HttpHandler bindHttpHandler(VerificationHandler impl);

    // Dynamically refreshable configuration is overkill for a proof-of-concept.
    @Provides
    @Singleton
    @Named("verifyPath")
    static String provideVerifyPath() {
        return "/verify.html";
    }

    @Provides
    @Singleton
    @Named("html")
    static Class<?> provideHtmlClass() {
        return VerifyHtmlModule.class;
    }
}
