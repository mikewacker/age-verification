module org.example.age.data {
    exports org.example.age.data.certificate;
    exports org.example.age.data.crypto;
    exports org.example.age.data.user;
    exports org.example.age.data.utils;

    requires static org.immutables.value.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.guava;
    requires com.google.common;
    requires com.google.errorprone.annotations;
    requires jsr305;

    opens org.example.age.data.certificate to
            com.fasterxml.jackson.databind;
    opens org.example.age.data.crypto to
            com.fasterxml.jackson.databind;
    opens org.example.age.data.crypto.internal to
            com.fasterxml.jackson.databind;
    opens org.example.age.data.internal to
            com.fasterxml.jackson.databind;
    opens org.example.age.data.user to
            com.fasterxml.jackson.databind;
}
