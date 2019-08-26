all: compile

install:
	echo main-class: ru.lanolin.Main> src/manifest.mf
	jar -cmf src/manifest.mf infotecs_server-1.0.jar -C bin .
compile:
	rm -Rf bin
	mkdir bin
	javac -sourcepath src/main/java -d bin src/main/java/ru/lanolin/Main.java
	cp src/main/resources/* bin/
