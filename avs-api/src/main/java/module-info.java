module org.example.age.avs.api {
    exports org.example.age.avs.api;

    requires static org.immutables.value.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires okhttp3;
    requires org.example.age.api;
    requires org.example.age.common.api;
    requires org.example.age.data;
    requires org.example.age.infra.api;
    requires undertow.core;
}
