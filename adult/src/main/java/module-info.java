module org.example.age.adult {
    exports org.example.age.adult.server;

    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires org.example.age.common;
    requires undertow.core;
}
