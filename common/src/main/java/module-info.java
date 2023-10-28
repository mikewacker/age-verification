module org.example.age.common {
    exports org.example.age.common.avs.api;
    exports org.example.age.common.avs.store;
    exports org.example.age.common.base.account;
    exports org.example.age.common.base.auth;
    exports org.example.age.common.base.store;
    exports org.example.age.common.server.html;
    exports org.example.age.common.server.undertow;
    exports org.example.age.common.site.api;
    exports org.example.age.common.site.config;
    exports org.example.age.common.site.store;

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
