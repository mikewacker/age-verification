module org.example.age.infra.service {
    exports org.example.age.infra.service.client;

    requires com.fasterxml.jackson.databind;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires okhttp3;
    requires org.example.age.api;
}
