def javaFile = new File(basedir.getAbsolutePath() + "/target/generated-sources-it/log-domain-codegen/java/com/example/network/NetworkMarker.java");
def markdownFile = new File(basedir.getAbsolutePath() + "/target/generated-sources-it/log-domain-codegen/markdown/Network.md");

return javaFile.exists() && markdownFile.exists();