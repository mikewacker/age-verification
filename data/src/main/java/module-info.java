module org.example.age.data {
    exports org.example.age.data;
    exports org.example.age.data.certificate;

    requires static org.immutables.value.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.guava;
    requires com.google.common;
    requires java.compiler;
}
