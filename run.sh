cd out/artifacts/streamer_jar/
jars="";
for jar in *.jar
    do jars="$jars:$jar"
    done
java -classpath  $jars com.dataspawn.twitterstreamer.Main
