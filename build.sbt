import scala.sys.process.Process

name := "tfs-protobuf"

organization := "org.ailinykh"

val tfDir = "tf"
val tfsDir = "tfs"

val tfClone = taskKey[Int]("Clone TensorFlow github repo")
val tfBranch = taskKey[Int]("Check out TensorFlow branch")

val tfsClone = taskKey[Int]("Clone TensorFlow Serving github repo")
val tfsBranch = taskKey[Int]("Check out TensorFlow Serving branch")


val tfVersion = "1.10"
version := tfVersion+ ".0"

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
  Process(s"git checkout r$tfVersion", new  File(s"${target.value}/$tfDir")).!
}

tfsBranch := {
  tfsClone.value
  Process(s"git checkout r$tfVersion", new  File(s"${target.value}/$tfsDir")).!
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
  findProtoFiles.value pair rebase(baseDirectories, protoDir.value) map(p => (p._1 -> p._2.getPath))
}


useGpg := true

