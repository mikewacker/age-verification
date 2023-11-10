module common.service {
    exports org.example.age.common.service.data;

    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires org.example.age.api;
    requires org.example.age.common.api;
    requires org.example.age.data;
    requires undertow.core;
}
