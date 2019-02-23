# tfs-protobuf
TensorFlow Serving protobuf file extractor

tfs-protobuf is a collection of all protobuf files used by TensorFlow and TensorFlow Serving.
It is usefull to access TensorFlow Serving from scala.

# Usage

This is an example of tfs-protobuf usage with [ScalaPB](https://scalapb.github.io/)

```
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)


libraryDependencies ++=
  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "org.ailinykh" %% "tfs-protobuf" % "1.9.0" % "protobuf"
  )


PB.protoSources in Compile += target.value / "protobuf_external"
```