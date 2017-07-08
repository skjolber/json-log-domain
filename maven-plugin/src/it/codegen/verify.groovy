def file = new File(basedir.getAbsolutePath() + "/target/generated-sources-it/log-domain-codegen/com/example/network/NetworkMarker.java");

System.out.println(file);
return file.exists();