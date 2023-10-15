module org.example.age.data {
    exports org.example.age.data;
    exports org.example.age.data.certificate;

    requires static org.immutables.value.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.guava;
    requires com.google.common;
    requires com.google.errorprone.annotations;
    requires jsr305;

    opens org.example.age.data to
            com.fasterxml.jackson.databind;
    opens org.example.age.data.certificate to
            com.fasterxml.jackson.databind;
    opens org.example.age.data.internal to
            com.fasterxml.jackson.databind;
}
