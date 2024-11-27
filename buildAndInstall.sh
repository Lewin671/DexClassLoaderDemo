# jdk 切换
switch_jdk () {
  if [ -z "$1" ]; then
    # 没有指定版本，列出可用的 JDK 版本
    /usr/libexec/java_home -V
  else
    # 指定了版本，设置 JAVA_HOME 环境变量
    export JAVA_HOME=`/usr/libexec/java_home -v $1`
    export PATH=$JAVA_HOME/bin:$PATH
    export CLASS_PATH=$JAVA_HOME/lib
    # 显示当前的 JDK 版本
    java -version
  fi
}


switch_jdk 17
./gradlew :host:clean :host:assembleRelease
adb install ./host/build/outputs/apk/release/host-release.apk