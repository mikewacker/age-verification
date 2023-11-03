module org.example.age.common.server {
    exports org.example.age.common.server.html;
    exports org.example.age.common.server.undertow;

    requires com.google.common;
    requires dagger;
    requires java.compiler;
    requires javax.inject;
    requires undertow.core;
}
