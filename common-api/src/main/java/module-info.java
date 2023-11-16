module org.example.age.common.api {
    exports org.example.age.common.api.data;

    requires com.fasterxml.jackson.databind;
    requires org.example.age.api;
    requires org.example.age.data;
    requires undertow.core;

    opens org.example.age.common.api.data to
            com.fasterxml.jackson.databind;
}
