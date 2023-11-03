module org.example.age.adult {
    exports org.example.age.adult.server;

    requires com.google.common;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires org.example.age.common;
    requires org.example.age.common.server;
    requires undertow.core;
}
