module org.example.age.common {
    exports org.example.age.common.account;
    exports org.example.age.common.html;
    exports org.example.age.common.server;
    exports org.example.age.common.site.api;
    exports org.example.age.common.site.auth;
    exports org.example.age.common.site.config;
    exports org.example.age.common.site.verification;

    requires static org.immutables.value.annotations;
    requires com.google.common;
    requires com.google.errorprone.annotations;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires jsr305;
    requires okhttp3;
    requires org.example.age.data;
    requires undertow.core;
    requires xnio.api;
}
