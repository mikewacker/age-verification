module org.example.age.avs.api {
    exports org.example.age.avs.api;

    requires com.fasterxml.jackson.databind;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires org.example.age.api;
    requires org.example.age.common.api;
    requires org.example.age.data;
    requires org.example.age.infra.api;
    requires undertow.core;
}
