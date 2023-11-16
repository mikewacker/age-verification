module org.example.age.site.service {
    exports org.example.age.site.service.config;
    exports org.example.age.site.service.store;
    exports org.example.age.site.service.config.internal; // to org.example.age.common

    requires static org.immutables.value.annotations;
    requires com.google.common;
    requires com.google.errorprone.annotations;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires jsr305;
    requires okhttp3;
    requires org.example.age.data;
}
