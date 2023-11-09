module org.example.age.common.api {
    exports org.example.age.common.api;
    exports org.example.age.common.api.data.auth;
    exports org.example.age.common.api.data.account;
    exports org.example.age.common.api.exchange.impl;
    exports org.example.age.common.api.request.impl;

    requires com.fasterxml.jackson.databind;
    requires org.example.age.data;
    requires undertow.core;
    requires xnio.api;
}
