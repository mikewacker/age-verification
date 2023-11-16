module org.example.age.common.service {
    exports org.example.age.common.service.data;
    exports org.example.age.common.service.store;

    requires static org.immutables.value.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires org.example.age.api;
    requires org.example.age.common.api;
    requires org.example.age.data;
    requires undertow.core;
    requires xnio.api;

    opens org.example.age.common.service.data to
            com.fasterxml.jackson.databind;
}
