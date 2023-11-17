module org.example.age.common {
    exports org.example.age.common.avs.api;
    exports org.example.age.common.avs.config;
    exports org.example.age.common.avs.store;
    exports org.example.age.common.base.store;
    exports org.example.age.common.site.api;

    requires static org.immutables.value.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires com.google.errorprone.annotations;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires jsr305;
    requires okhttp3;
    requires org.example.age.api;
    requires org.example.age.avs.api;
    requires org.example.age.common.api;
    requires org.example.age.common.service;
    requires org.example.age.data;
    requires org.example.age.infra.api;
    requires org.example.age.site.api;
    requires org.example.age.site.service;
    requires undertow.core;
    requires xnio.api;
}
