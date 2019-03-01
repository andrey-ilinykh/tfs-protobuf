import scala.sys.process.Process

name := "tfs-protobuf"

organization := "org.ailinykh"

val tfDir = "tf"
val tfsDir = "tfs"

val tfClone = taskKey[Int]("Clone TensorFlow github repo")
val tfBranch = taskKey[Int]("Check out TensorFlow tag")

val tfsClone = taskKey[Int]("Clone TensorFlow Serving github repo")
val tfsBranch = taskKey[Int]("Check out TensorFlow Serving tag")


val tfTag = "v1.13.1"
val tfsTag = "1.13.0"
version := "1.13.0"

val protoDir = settingKey[File]("location of proto file direcitory")
protoDir := target.value / "proto"

val findProtoFiles = taskKey[Seq[File]]("Find all *.proto files")

tfClone := {
  val tfPath = s"${target.value}/$tfDir"
  if(new File(tfPath).exists())
    0
  else
    Process(s"git clone https://github.com/tensorflow/tensorflow.git $tfPath").!
}

tfsClone := {
  val tfsPath = s"${target.value}/$tfsDir"
  if(new File(tfsPath).exists())
    0
  else
    Process(s"git clone https://github.com/tensorflow/serving.git $tfsPath").!
}


tfBranch := {
  tfClone.value
  Process(s"git checkout tags/$tfTag", new  File(s"${target.value}/$tfDir")).!
}

tfsBranch := {
  tfsClone.value
  Process(s"git checkout tags/$tfsTag", new  File(s"${target.value}/$tfsDir")).!
}

val copyProtoFiles = taskKey[Int]("Copy protobuf files")



findProtoFiles := {
  tfBranch.value
  tfsBranch.value
  val tfRepoDir = new  File(s"${target.value}/$tfDir")
  val tfsRepoDir = new  File(s"${target.value}/$tfsDir")

  (tfRepoDir ** "*.proto").get ++ (tfsRepoDir ** "*.proto").get
}

mappings in packageBin in Compile ++= {
  import Path._
  val tfRepoDir = new  File(s"${target.value}/$tfDir")
  val tfsRepoDir = new  File(s"${target.value}/$tfsDir")
  val baseDirectories: Seq[File] =  file(tfRepoDir.getPath) :: file(tfsRepoDir.getPath) :: Nil
  findProtoFiles.value pair relativeTo(baseDirectories) map(p => (p._1 -> p._2))
}


useGpg := true

