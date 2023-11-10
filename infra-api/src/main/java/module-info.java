module org.example.age.infra.api {
    exports org.example.age.infra.api;
    exports org.example.age.infra.api.request;

    requires com.fasterxml.jackson.databind;
    requires org.example.age.api;
    requires undertow.core;
    requires xnio.api;
}
