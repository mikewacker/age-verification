module org.example.age.common.api {
    exports org.example.age.common.api.data.auth;
    exports org.example.age.common.api.data.account;

    requires org.example.age.api;
    requires org.example.age.data;
    requires undertow.core;
}
