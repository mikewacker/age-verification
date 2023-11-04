module org.example.age.common.api {
    exports org.example.age.common.api;
    exports org.example.age.common.api.data.auth;
    exports org.example.age.common.api.data.account;
    exports org.example.age.common.api.exchange.impl;

    requires org.example.age.data;
    requires undertow.core;
}
